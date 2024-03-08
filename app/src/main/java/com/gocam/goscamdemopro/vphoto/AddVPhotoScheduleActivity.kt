package com.gocam.goscamdemopro.vphoto

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.add.QrCodeActivity
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.cloud.data.GosCloud
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityAddVphotoScheduleBinding
import com.gocam.goscamdemopro.vphoto.data.BindByDeviceResult
import com.gos.platform.api.contact.DeviceType
import com.gos.platform.api.contact.PlatCode
import com.gos.platform.api.inter.OnPlatformEventCallback
import com.gos.platform.api.result.PlatResult
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class AddVPhotoScheduleActivity: BaseBindActivity<ActivityAddVphotoScheduleBinding>(),
    OnPlatformEventCallback {
    private lateinit var mDeviceId: String
    private val timerTask: MyTimerTask by lazy {
        MyTimerTask()
    }
    private val mTimer = Timer()
    override fun getLayoutId(): Int {
        return R.layout.activity_add_vphoto_schedule
    }

    override fun onCreateData(bundle: Bundle?) {
        mDeviceId = intent.getStringExtra("VPHOTO_DEVICE_ID") as String
        mBinding?.toolBar?.backImg?.setOnClickListener {
            finish()
        }
        GosCloud.getCloud().addOnPlatformEventCallback(this)
    }

    override fun onResume() {
        super.onResume()
        GosCloud.getCloud().bindByDevice(mDeviceId, GApplication.app.vPhotoUser.vphotoAccessToken, GApplication.app.vPhotoUser.vphotUserId!!)
        mTimer.schedule(timerTask, 1000, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        GosCloud.getCloud().removeOnPlatformEventCallback(this)
        reset()
    }

    private fun reset() {
        mTimer.cancel()
        timerTask.cancel()
    }

    override fun OnPlatformEvent(result: PlatResult?) {
        val cmd = result?.platCmd
        val code = result?.responseCode
        when(cmd) {
            PlatResult.PlatCmd.bindByDeviceConnection ->
                if (code == PlatCode.SUCCESS) {
                    val deviceResult = result as BindByDeviceResult
                    val deviceId = deviceResult.deviceBean.device_id.toString()
                    GApplication.app.vPhotoUser.devVPhotoUid = deviceId
                    forceUnbindDevice(deviceId)
                } else {
                    startResultActivity(code)
                }
//            PlatResult.PlatCmd.forceUnbindDevice ->
//                if (code == PlatCode.SUCCESS) {
//                    bindSmartDevice()
//                }
        }
    }

    private fun forceUnbindDevice(deviceId: String) {
        lifecycleScope.launch {
            val result = RemoteDataSource.forceUnbindDevice(deviceId)
            if (result?.code ==  PlatCode.SUCCESS || result?.code == -10093) {
                bindSmartDevice()
            } else{
                startResultActivity(result?.code)
            }
        }
    }

    private suspend fun bindSmartDevice() {
        val result = RemoteDataSource.bindSmartDevice(
            GApplication.app.user.userName,
            GApplication.app.vPhotoUser.devVPhotoUid, true, "相框",
            DeviceType.V_PHOTO, "", "", "", 1, mDeviceId
        )

        if (result?.code == PlatCode.SUCCESS) {
            startResultActivity(0)
        } else{
            startResultActivity(result?.code)
        }
    }

    private fun startResultActivity(code: Int?) {
        val intent = Intent(this@AddVPhotoScheduleActivity, VPhotoAddDevResultActivity::class.java)
        intent.putExtra("CODE", code)
        this@AddVPhotoScheduleActivity.startActivity(intent)
    }

    inner class MyTimerTask: TimerTask() {
        override fun run() {
            mBinding?.apply {
                if (vphotoSpreadView.count < 100) {
                    vphotoSpreadView.count = vphotoSpreadView.count + 1
                }
            }

        }
    }
}