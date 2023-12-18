package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivitySwitchLayoutBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2023/12/14 11:10
 */
class SwitchActivity: BaseActivity<ActivitySwitchLayoutBinding,SwitchViewModel>() {
    lateinit var mDevice: Device
    override fun getLayoutId(): Int {
        return R.layout.activity_switch_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev") as String
        mDevice = DeviceManager.getInstance().findDeviceById(devId)

        mViewModel.getThirdSwitch(devId)

        mViewModel.apply {
            mLedSwitchParam.observe(this@SwitchActivity){
                mBinding?.swLightStatus?.isChecked = it.device_led_switch == OnOff.On
            }

            mObjTrackParam.observe(this@SwitchActivity){
                mBinding?.swObjTrack?.isChecked = it.un_switch == OnOff.On
            }

            mTfRecordParam.observe(this@SwitchActivity){
                mBinding?.swTfRecord?.isChecked = it.manual_record_switch == OnOff.On
            }


        }

    }
}