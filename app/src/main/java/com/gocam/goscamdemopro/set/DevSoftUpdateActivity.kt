package com.gocam.goscamdemopro.set

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityDevSoftUpdateBinding
import com.gocam.goscamdemopro.entity.*
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gocam.goscamdemopro.utils.Util
import com.gos.platform.api.UlifeResultParser.EventType.IOTYPE_USER_IPCAM_GET_UPGRADE_INFO_REQ
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.UpgradeStatus

class DevSoftUpdateActivity : BaseActivity<ActivityDevSoftUpdateBinding, DevUpdateViewModel>() {
    lateinit var deviceId: String
    lateinit var mDevice: Device
    private var isUpdate: Boolean = false
    private lateinit var firmWareParam: FirmWareParam
    override fun getLayoutId(): Int = R.layout.activity_dev_soft_update

    override fun onCreateData(bundle: Bundle?) {
        deviceId = intent.getStringExtra("dev") as String
        mDevice = DeviceManager.getInstance().findDeviceById(deviceId)
        if (mDevice == null)
            finish()

        mBinding?.apply {
            tvSoftInfo.text = mDevice.deviceSfwVer
            tvHardInfo.text = mDevice.deviceHdwVer
        }

        mViewModel?.getDeviceFirmware(mDevice.deviceHdType)

        mViewModel?.mFirmWareParam.observe(this) {
            firmWareParam = it
            val hasNew = Util.compareDeviceSoftwareVersion(mDevice.deviceSfwVer, it.Version)
            mBinding?.btnUpdate?.isEnabled = hasNew
        }

        mBinding?.btnUpdate?.setOnClickListener {
            if (isUpdate)
                return@setOnClickListener
            val upGradeStatusParam = UpgradeStatusParam(
                0, 0, -1
            )

            val devParamArray = DevParamArray(
                UpgradeStatus,
                upGradeStatusParam
            )
            mViewModel?.setDeviceParam(devParamArray, deviceId)


            val updateRestParam = GetUpgradeInfoRequestParam(
                IOTYPE_USER_IPCAM_GET_UPGRADE_INFO_REQ,
                0,
                1,
                firmWareParam.Url,
                firmWareParam.Version,
                firmWareParam.MD5,
                firmWareParam.FileSize
            )
            mViewModel?.setDeviceUpdate(updateRestParam, deviceId = deviceId)
        }
    }
}