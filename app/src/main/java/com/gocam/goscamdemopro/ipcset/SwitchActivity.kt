package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import android.widget.CompoundButton
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivitySwitchLayoutBinding
import com.gocam.goscamdemopro.entity.*
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2023/12/14 11:10
 */
class SwitchActivity: BaseActivity<ActivitySwitchLayoutBinding,SwitchViewModel>(),CompoundButton.OnCheckedChangeListener {
    lateinit var mDevice: Device
    lateinit var devId:String
    override fun getLayoutId(): Int {
        return R.layout.activity_switch_layout
    }

    override fun onCreateData(bundle: Bundle?) {
         devId = intent.getStringExtra("dev") as String
        mDevice = DeviceManager.getInstance().findDeviceById(devId)

        mViewModel.getThirdSwitch(devId)

        mViewModel.apply {
            mLedSwitchParam.observe(this@SwitchActivity){
                mBinding?.swLightStatus?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.device_led_switch == OnOff.On
                    setOnCheckedChangeListener(this@SwitchActivity)
                }
            }

            mObjTrackParam.observe(this@SwitchActivity){
                mBinding?.swObjTrack?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.un_switch == OnOff.On
                    setOnCheckedChangeListener(this@SwitchActivity)
                }
            }

            mTfRecordParam.observe(this@SwitchActivity){
                mBinding?.swTfRecord?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.manual_record_switch == OnOff.On
                    setOnCheckedChangeListener(this@SwitchActivity)
                }
            }

        }





    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView?.id){
            R.id.sw_tf_record->{
                val tvRecordParam = TfRecordParam(
                    if (isChecked){
                        OnOff.On
                    }else{
                        OnOff.Off
                    }
                )
                val devParamArray = DevParamArray(
                    DevParam.DevParamCmdType.TfRecordSetting,
                    tvRecordParam
                )
                mViewModel.setSwitchParam(devParamArray, devId = devId)
            }
            R.id.sw_obj_track->{
                val objectTrack = ObjTrackParam(
                    if (isChecked){
                        OnOff.On
                    }else{
                        OnOff.Off
                    }
                )
                val devParamArray = DevParamArray(
                    DevParam.DevParamCmdType.ObjTrackSetting,
                    objectTrack
                )
                mViewModel.setSwitchParam(devParamArray, devId = devId)
            }
            R.id.sw_light_status->{
                val ledSwitchParam = LedSwitchParam(
                    if (isChecked){
                        OnOff.On
                    }else{
                        OnOff.Off
                    }
                )
                val devParamArray = DevParamArray(
                    DevParam.DevParamCmdType.LedSwitch,
                    ledSwitchParam
                )
                mViewModel.setSwitchParam(devParamArray, devId = devId)
            }
        }

    }
}