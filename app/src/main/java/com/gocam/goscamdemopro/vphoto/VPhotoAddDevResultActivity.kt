package com.gocam.goscamdemopro.vphoto

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.MainActivity
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.add.QrCodeActivity
import com.gocam.goscamdemopro.add.WifiSelectActivity
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.cloud.data.GosCloud
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityPhotoAddDevResultBinding
import com.gocam.goscamdemopro.utils.SharedPreferencesUtil
import com.gocam.goscamdemopro.vphoto.data.DeviceCamResult
import com.gos.platform.api.inter.OnPlatformEventCallback
import com.gos.platform.api.result.PlatResult
import kotlinx.coroutines.launch

class VPhotoAddDevResultActivity: BaseBindActivity<ActivityPhotoAddDevResultBinding>(), OnPlatformEventCallback {
    private var TAG: String = "VPhotoAddDevResultActivity"
    private var mStatus: Int = 0
    private var text: String = "VPhoto"
    override fun getLayoutId(): Int {
        return R.layout.activity_photo_add_dev_result
    }

    override fun onCreateData(bundle: Bundle?) {
        mStatus = intent.getIntExtra("CODE", -1)
        mBinding?.toolBar?.backImg?.setOnClickListener {
            finish()
        }
        GosCloud.getCloud().addOnPlatformEventCallback(this)
        mBinding?.apply {
            if (mStatus == 0) {
                toolBar.textTitle.setText(R.string.add_sensor_success)
                etDevName.setText("VPhoto")
                tvResult.setText(R.string.add_sensor_success)
                val drawable = resources.getDrawable(R.mipmap.icon_success, null)
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                tvResult.setCompoundDrawables(null, drawable, null, null)
                llDevName.visibility = View.VISIBLE
                llFail.visibility = View.GONE
                btn2.visibility = View.GONE
                tvFail.visibility = View.GONE
                llError.visibility = View.GONE
                if (GApplication.app.vPhotoUser.indexVPhoto < GApplication.app.vPhotoUser.totalVPhoto) {
                    btn1.setText(R.string.add_con)
                } else {
                    btn1.setText(R.string.done)
                }
            } else {
                toolBar.textTitle.setText(R.string.add_1_add_fail)
                val drawable = resources.getDrawable(R.mipmap.icon_failure, null)
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                tvResult.setCompoundDrawables(null, drawable, null, null)
                llDevName.visibility = View.GONE
                tvFail.visibility = View.VISIBLE
                btn1.setText(R.string.message_exit_edit)
                btn2.setText(R.string.add_retry)
                llError.visibility = View.VISIBLE
                tvCode.text = "error code:" + mStatus
                tvServerCode.text = "server code:" + mStatus
                tvAddWay.setText(R.string.add_way_2)
                tvAddType.setText(R.string.dev_add_qr)
            }

            etDevName.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().length > 63) {
                        etDevName.setText(text)
                        etDevName.setSelection(text.length)
                    } else {
                        text = s.toString()
                    }
                }
            })

            tvFailNotice.setOnClickListener { readyGoFinish() }

            btn1.setOnClickListener {
                if (mStatus == 0) {
                    setLinkDevice()
                } else {
                    readyGoFinish()
                }
            }

            btn2.setOnClickListener {
                readyGoFinish()
            }
        }
    }

    private fun setLinkDevice() {
        if (GApplication.app.vPhotoUser.indexVPhoto == 0) {
            lifecycleScope.launch {
                val result = RemoteDataSource.modifyDeviceAttr(
                        GApplication.app.vPhotoUser.devVPhotoUid!!,
                        text, "", "", ""
                    )
                Log.e(TAG, "modifyName:result =  ${result?.json}, text = $text")
            }
            GApplication.app.vPhotoUser.linkName = text
            addDevice()
            return
        }

        if (TextUtils.isEmpty(GApplication.app.vPhotoUser.linkDevices)) {
            GApplication.app.vPhotoUser.linkDevices = GApplication.app.vPhotoUser.devUid
        } else {
            GApplication.app.vPhotoUser.linkDevices = GApplication.app.vPhotoUser.linkDevices + "," + GApplication.app.vPhotoUser.devUid
        }

        lifecycleScope.launch {
            Log.e(TAG, "modifyName:devVPhotoUid = ${GApplication.app.vPhotoUser.devVPhotoUid}")
            GApplication.app.vPhotoUser.devUid?.let {
                val result1 = RemoteDataSource.modifyDeviceAttr(
                    it, text, "", "", GApplication.app.vPhotoUser.devVPhotoUid
                )
                Log.e(TAG, "modifyName: result = ${result1?.json} ,text = $text")
            }
            Log.e(TAG, "modifyName:devVPhotoUid = ${GApplication.app.vPhotoUser.devVPhotoUid}")
            val result2 = RemoteDataSource.modifyDeviceAttr(
                GApplication.app.vPhotoUser.devVPhotoUid!!,
                GApplication.app.vPhotoUser.linkName!!,
                "",
                "",
                GApplication.app.vPhotoUser.linkDevices
            )
            Log.e(TAG, "modifyName: result = ${result2?.json} ,linkDevices = ${GApplication.app.vPhotoUser.linkDevices}")

            if (GApplication.app.vPhotoUser.indexVPhoto < GApplication.app.vPhotoUser.totalVPhoto) {
                addDevice()
            } else {
                GosCloud.getCloud().updateDeviceCam(
                    GApplication.app.vPhotoUser.vphotoAccessToken,
                    GApplication.app.vPhotoUser.vphotUserId
                )
            }
        }
    }


    private fun addDevice() {
        GApplication.app.vPhotoUser.indexVPhoto++
        val intent = Intent(this@VPhotoAddDevResultActivity, QrCodeActivity::class.java)
        intent.putExtra("ssid",GApplication.app.vPhotoUser.ssid)
        intent.putExtra("psw",GApplication.app.vPhotoUser.psw)
        this@VPhotoAddDevResultActivity.startActivity(intent)
    }

    private fun readyGoFinish() {
        val intent = Intent(this@VPhotoAddDevResultActivity, WifiSelectActivity::class.java)
        this@VPhotoAddDevResultActivity.startActivity(intent)
        this@VPhotoAddDevResultActivity.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        GosCloud.getCloud().removeOnPlatformEventCallback(this)
    }

    override fun OnPlatformEvent(result: PlatResult?) {
        val code = result?.responseCode
        when(result?.platCmd) {
            PlatResult.PlatCmd.deviceCamVPhoto -> {
                val camResult = result as DeviceCamResult
                if (camResult.responseCode == 0 && "success" == camResult.message) {
                    SharedPreferencesUtil.putBoolean(
                        SharedPreferencesUtil.SpreContant.V_PHOTO_DEVICE_CAM,
                        true
                    )
                } else {
                    SharedPreferencesUtil.putBoolean(
                        SharedPreferencesUtil.SpreContant.V_PHOTO_DEVICE_CAM,
                        false
                    )
                }
                val intent = Intent(this@VPhotoAddDevResultActivity, MainActivity::class.java)
                this@VPhotoAddDevResultActivity.startActivity(intent)
                this@VPhotoAddDevResultActivity.finish()
            }
        }
    }
}