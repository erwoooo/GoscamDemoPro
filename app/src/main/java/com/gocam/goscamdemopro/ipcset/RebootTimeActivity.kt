package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import android.view.View
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityRebootLayoutBinding
import com.gocam.goscamdemopro.entity.CameraPlanParam
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.ResetPlanParam
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2023/12/14 11:48
 */
class RebootTimeActivity: BaseActivity<ActivityRebootLayoutBinding,RebootTimeViewModel>() {
    lateinit var devId:String
    override fun getLayoutId(): Int {
        return R.layout.activity_reboot_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        devId = intent.getStringExtra("dev") as String

        mViewModel.getDevResetData(devId)

        mViewModel.apply {
            mResetPlanParam.observe(this@RebootTimeActivity){
                if (it.enable == OnOff.On){

                    mBinding?.constrainCustom?.visibility = View.VISIBLE
                    mBinding?.ivCloseReboot?.visibility = View.INVISIBLE
                }else{
                    mBinding?.constrainCustom?.visibility = View.INVISIBLE
                    mBinding?.ivCloseReboot?.visibility = View.VISIBLE
                }

            }
        }

        mBinding?.apply {
            tvCustomReboot.setOnClickListener {
                constrainCustom.visibility = View.VISIBLE
            }
            tvCloseReboot.setOnClickListener {
                val cameraPlanParam = ResetPlanParam(
                    OnOff.Off,1,0
                )
                val devParamArray = DevParamArray(
                    DevParam.DevParamCmdType.ResetSetting,
                    cameraPlanParam
                )
                mViewModel.setSwitchParam(devParamArray,devId)
                constrainCustom.visibility = View.INVISIBLE
                ivCloseReboot.visibility = View.VISIBLE
            }
            btnYes.setOnClickListener {
                val startTime:Int = etStartH.text.toString().toInt()  * 3600 +  etStartM.text.toString().toInt() * 60
                var repeat = 0
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

                val cameraPlanParam = ResetPlanParam(
                    OnOff.On,repeat,startTime
                )
                val devParamArray = DevParamArray(
                    DevParam.DevParamCmdType.ResetSetting,
                    cameraPlanParam
                )
                ivCloseReboot.visibility = View.INVISIBLE
                mViewModel.setSwitchParam(devParamArray,devId)

            }
        }
    }
}