package com.gocam.goscamdemopro.add

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.cloud.data.GosCloud
import com.gocam.goscamdemopro.databinding.ActivityAddVphotoBinding
import com.gocam.goscamdemopro.utils.PermissionUtil
import com.gocam.goscamdemopro.vphoto.AddVPhotoScheduleActivity
import com.gocam.goscamdemopro.vphoto.data.BindByDeviceStatusResult
import com.gocam.goscamdemopro.vphoto.data.SigninByUuidResult
import com.gos.platform.api.contact.PlatCode
import com.gos.platform.api.inter.OnPlatformEventCallback
import com.gos.platform.api.result.PlatResult

class AddVPhotoActivity: BaseActivity<ActivityAddVphotoBinding, AddVPhotoViewModel>(),
    OnPlatformEventCallback {
    private var linkCode: String? = null

    private lateinit var ssid: String

    private lateinit var psw: String

    override fun getLayoutId(): Int {
        return R.layout.activity_add_vphoto
    }

    override fun onCreateData(bundle: Bundle?) {
        ssid = intent.getStringExtra("ssid") as String
        psw = intent.getStringExtra("psw") as String
        mBinding?.toolBar?.backImg?.setOnClickListener {
            finish()
        }
        GosCloud.getCloud().addOnPlatformEventCallback(this)
        mBinding?.apply {
            ivScan.setOnClickListener {
                if (PermissionUtil.verifyCameraPermissions(this@AddVPhotoActivity)){
                    val intent = Intent(this@AddVPhotoActivity,ZxingCaptureKtActivity::class.java)
                    intent.putExtra("COME_FROM", "ISFROM_ADD_VPHOTO")
                    this@AddVPhotoActivity.startActivityForResult(intent, 1000)
                }
            }

            btnStepNotice.setOnClickListener {
                linkCode = etDevId.text.toString()
                if (TextUtils.isEmpty(GApplication.app.vPhotoUser.vphotoAccessToken)) {
                    GosCloud.getCloud().signinByUuid(GApplication.app.user.userName)
                } else {
                    linkCode?.run {
                        GosCloud.getCloud().bindByDeviceStatus(linkCode, GApplication.app.vPhotoUser.vphotoAccessToken, GApplication.app.vPhotoUser.vphotUserId!!)
                    }
                }
            }
        }

        GApplication.app.vPhotoUser.ssid = ssid
        GApplication.app.vPhotoUser.psw = psw
        if (TextUtils.isEmpty(GApplication.app.vPhotoUser.vphotoAccessToken)) {
            GosCloud.getCloud().signinByUuid(GApplication.app.user.userName)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1001 && data != null) {
            mBinding?.etDevId?.setText(data.getStringExtra("PHOTO_LINK_CODE"))
        }
    }

    override fun OnPlatformEvent(result: PlatResult?) {
        val cmd = result?.platCmd;
        val code = result?.responseCode;
        when (cmd) {
            PlatResult.PlatCmd.userRegisterByUuid ->
                if (code == PlatCode.SUCCESS) {
                    GosCloud.getCloud().signinByUuid(GApplication.app.user.userName)
                }
            PlatResult.PlatCmd.signinByUuid ->
                if (code == PlatCode.SUCCESS) {
                    val signin = result as SigninByUuidResult
                    GApplication.app.vPhotoUser.vphotUserId = signin.signinBean.user_id
                    GApplication.app.vPhotoUser.vphotoAccessToken = signin.signinBean.access_token
                    GApplication.app.vPhotoUser.vphotoUserSystemToken = signin.signinBean.user_system_token
                    linkCode?.let {
                        GosCloud.getCloud().bindByDeviceStatus(linkCode, GApplication.app.vPhotoUser.vphotoAccessToken, GApplication.app.vPhotoUser.vphotUserId!!)
                    }
                } else if (code == 419) {
                    // 该帐号还没注册，需要先调接口注册
                    GosCloud.getCloud().userRegisterByUuid(GApplication.app.user.userName)
                }
            PlatResult.PlatCmd.bindStatus ->
                if (code == PlatCode.SUCCESS) {
                    val statusResult = result as BindByDeviceStatusResult
                    if ("nobind" == statusResult.message) {
                        val intent = Intent(this@AddVPhotoActivity, AddVPhotoScheduleActivity::class.java)
                        intent.putExtra("VPHOTO_DEVICE_ID", linkCode)
                        this@AddVPhotoActivity.startActivity(intent)
                    } else if ("bind" == statusResult.message) {
                        ToastUtils.showLong(R.string.dev_already_bind)
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GosCloud.getCloud().removeOnPlatformEventCallback(this)
    }
}