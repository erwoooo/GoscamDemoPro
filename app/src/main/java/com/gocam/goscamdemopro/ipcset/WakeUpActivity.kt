package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityWakeUpLayoutBinding
import com.gocam.goscamdemopro.entity.CameraPlanParam
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.DevSwitchParam
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2023/12/14 11:34
 */
class WakeUpActivity: BaseActivity<ActivityWakeUpLayoutBinding,WakeUpViewModel>() ,CompoundButton.OnCheckedChangeListener{
    lateinit var deviceId:String
    override fun getLayoutId(): Int {
        return R.layout.activity_wake_up_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        deviceId = intent.getStringExtra("dev") as String
        mViewModel.getCameraSet(deviceId)

        mViewModel.apply {
            mCameraPlanParam.observe(this@WakeUpActivity){
                val startTime = it.start_time
                val endTime = it.end_time
                val repeat = it.repeat
                val enable = it.enable

                val EVERY_DAY = 127
                val time8 = 8 * 60 * 60 //8点

                val time20 = 20 * 60 * 60 //20点

                if (enable == OnOff.Off){
                    mBinding?.apply {
                        ivClosePlan.visibility = View.VISIBLE
                        ivDayPlan.visibility = View.INVISIBLE
                        ivNightPlan.visibility = View.INVISIBLE
                        constrainCustom.visibility = View.INVISIBLE
                    }
                }else if (repeat == EVERY_DAY && startTime == time8 && endTime == time20){
                    mBinding?.apply {
                        ivClosePlan.visibility = View.INVISIBLE
                        ivDayPlan.visibility = View.VISIBLE
                        ivNightPlan.visibility = View.INVISIBLE
                        constrainCustom.visibility = View.INVISIBLE
                    }

                }else if (repeat == EVERY_DAY && startTime == time20 && endTime == time8){
                    mBinding?.apply {
                        ivClosePlan.visibility = View.INVISIBLE
                        ivDayPlan.visibility = View.INVISIBLE
                        ivNightPlan.visibility = View.VISIBLE
                        constrainCustom.visibility = View.INVISIBLE
                    }
                }else{
                    mBinding?.apply {
                        constrainCustom.visibility = View.VISIBLE
                        ivClosePlan.visibility = View.INVISIBLE
                        ivDayPlan.visibility = View.INVISIBLE
                        ivNightPlan.visibility = View.INVISIBLE
                    }
                }

            }

            mDevSwitchParam.observe(this@WakeUpActivity){
                Log.e(TAG, "onCreateData: it$it", )
                mBinding?.swWakeUp?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.device_switch == OnOff.On
                    setOnCheckedChangeListener(this@WakeUpActivity)
                }
            }

        }


        mBinding?.apply {
            tvClosePlan.setOnClickListener {
                val cameraPlanParam = CameraPlanParam(
                    OnOff.Off,OnOff.Off,14400,0
                )
                val devParamArray = DevParamArray(
                    DevParam.DevParamCmdType.CameraSwitchPlanSetting,
                    cameraPlanParam
                )

                mViewModel.setSwitchParam(devParamArray,deviceId)

                ivDayPlan.visibility = View.INVISIBLE
                ivNightPlan.visibility = View.INVISIBLE
                ivClosePlan.visibility = View.VISIBLE
                constrainCustom.visibility = View.INVISIBLE
            }

            tvDayPlan.setOnClickListener {
                val cameraPlanParam = CameraPlanParam(
                    OnOff.On,127,28800,72000
                )
                val devParamArray = DevParamArray(
                    DevParam.DevParamCmdType.CameraSwitchPlanSetting,
                    cameraPlanParam
                )
                ivDayPlan.visibility = View.VISIBLE
                ivNightPlan.visibility = View.INVISIBLE
                ivClosePlan.visibility = View.INVISIBLE
                constrainCustom.visibility = View.INVISIBLE
                mViewModel.setSwitchParam(devParamArray,deviceId)
            }

            tvNightPlan.setOnClickListener {
                val cameraPlanParam = CameraPlanParam(
                    OnOff.On,127,72000,28800
                )
                val devParamArray = DevParamArray(
                    DevParam.DevParamCmdType.CameraSwitchPlanSetting,
                    cameraPlanParam
                )
                constrainCustom.visibility = View.INVISIBLE
                ivDayPlan.visibility = View.INVISIBLE
                ivNightPlan.visibility = View.VISIBLE
                ivClosePlan.visibility = View.INVISIBLE
                mViewModel.setSwitchParam(devParamArray,deviceId)
            }

            tvCustomPlan.setOnClickListener {
                ivDayPlan.visibility = View.INVISIBLE
                ivNightPlan.visibility = View.INVISIBLE
                ivClosePlan.visibility = View.INVISIBLE
                constrainCustom.visibility = View.VISIBLE
            }

            btnYes.setOnClickListener {

                val startTime:Int = etStartH.text.toString().toInt()  * 3600 +  etStartM.text.toString().toInt() * 60
                val endTime:Int = etEndH.text.toString().toInt() * 3600 + etEndM.text .toString().toInt() * 60

                var repeat = 0;
                if (checkboxSun.isChecked){
                    repeat = repeat.or( 0x01)
                }
                if (checkboxMon.isChecked){
                    repeat = repeat.or( (0x01).shl(1))
                }
                if (checkboxTue.isChecked){
                    repeat = repeat.or( (0x01).shl(2))
                }
                if (checkboxWed.isChecked){
                    repeat = repeat.or( (0x01).shl(3))
                }
                if (checkboxThu.isChecked){
                    repeat = repeat.or( (0x01).shl(4))
                }
                if (checkboxFri.isChecked){
                    repeat = repeat.or( (0x01).shl(5))
                }
                if (checkboxSat.isChecked){
                    repeat = repeat.or( (0x01).shl(6))
                }

                val cameraPlanParam = CameraPlanParam(
                    OnOff.On,repeat,startTime,endTime
                )
                val devParamArray = DevParamArray(
                    DevParam.DevParamCmdType.CameraSwitchPlanSetting,
                    cameraPlanParam
                )
                mViewModel.setSwitchParam(devParamArray,deviceId)
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        val devSwitchParam = DevSwitchParam(
            if (isChecked){
                OnOff.On
            }else{
                OnOff.Off
            }
        )
        val devParamArray = DevParamArray(
            DevParam.DevParamCmdType.DeviceSwitch,
            devSwitchParam
        )
        mViewModel.setSwitchParam(devParamArray,deviceId)
    }
}