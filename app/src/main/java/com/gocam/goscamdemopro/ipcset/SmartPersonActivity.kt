package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import android.widget.CompoundButton
import android.widget.SeekBar
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivitySmartPersonLayoutBinding
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2023/12/19 11:16
 */
class SmartPersonActivity: BaseActivity<ActivitySmartPersonLayoutBinding,SmartPersonViewModel>(),CompoundButton.OnCheckedChangeListener,SeekBar.OnSeekBarChangeListener {
    private lateinit var deviceId:String
    override fun getLayoutId(): Int {

        return R.layout.activity_smart_person_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        deviceId = intent.getStringExtra("dev") as String

        mViewModel.getSmartData(deviceId)

        mViewModel.apply {
            mSmartPersonParam.observe(this@SmartPersonActivity){
                    mBinding?.apply {
                        swPersonDet.apply {
                            setOnCheckedChangeListener(null)
                            isChecked = it.un_switch == OnOff.On
                            setOnCheckedChangeListener(this@SmartPersonActivity)
                        }
                        tvPersonLevel.text = "${it.un_sensitivity}"
                        seekPersonLevel.progress = it.un_sensitivity - 1
                        seekPersonLevel.setOnSeekBarChangeListener(this@SmartPersonActivity)
                    }
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        mViewModel.mSmartPersonParam.value?.let {
            it.un_switch = if (isChecked){
                OnOff.On
            }else{
                OnOff.Off
            }
            val devParams = DevParamArray(
                DevParam.DevParamCmdType.SmdAlarmSetting,
                it
            )

            mViewModel.setSwitchParam(devParams,deviceId)
        }

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {


    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        mViewModel.mSmartPersonParam.value?.let {
            it.un_sensitivity = seekBar?.progress!! + 1
            mBinding?.tvPersonLevel?.text = "${it.un_sensitivity}"
            val devParams = DevParamArray(
                DevParam.DevParamCmdType.SmdAlarmSetting,
                it
            )
            mViewModel.setSwitchParam(devParams,deviceId)
        }
    }
}