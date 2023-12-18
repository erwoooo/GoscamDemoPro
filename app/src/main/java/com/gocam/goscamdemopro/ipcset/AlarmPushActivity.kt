package com.gocam.goscamdemopro.ipcset

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityAlarmPushLayoutBinding
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.PushIntervalSetting
import com.gos.platform.device.contact.MirrorMode
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2023/12/14 11:20
 */
class AlarmPushActivity: BaseActivity<ActivityAlarmPushLayoutBinding,AlarmPushViewModel>(),CompoundButton.OnCheckedChangeListener,RadioGroup.OnCheckedChangeListener {
    lateinit var deviceId:String
    var pushIntervalDialog:Dialog?=null
    override fun getLayoutId(): Int {
        return R.layout.activity_alarm_push_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        deviceId = intent.getStringExtra("dev").toString()
        mViewModel.getAlarmPushData(deviceId)

        mViewModel.apply {
            mPushIntervalParam.observe(this@AlarmPushActivity){
                val second = it?.interval
                val min = second?.div(60)
                mBinding?.apply {
                    tvPushIntervalContent.text = "${min}min"
                    swHumanPush.apply {
                        setOnCheckedChangeListener(null)
                        isChecked = it?.person_detection_switch == OnOff.On
                        setOnCheckedChangeListener(this@AlarmPushActivity)
                    }

                    swSoundPush.apply {
                        setOnCheckedChangeListener(null)
                        isChecked = it?.sound_detection_switch == OnOff.On
                        setOnCheckedChangeListener(this@AlarmPushActivity)
                    }
                    swMotionPush.apply {
                        setOnCheckedChangeListener(null)
                        isChecked = it?.motion_detection_switch == OnOff.On
                        setOnCheckedChangeListener(this@AlarmPushActivity)
                    }

                    tvPushInterval.setOnClickListener {
                        pushIntervalDialog()
                    }

                    tvPushPlan.setOnClickListener {
                        if (scrollPushPlan.visibility == View.VISIBLE){
                            scrollPushPlan.visibility = View.GONE
                        }else{
                            scrollPushPlan.visibility = View.VISIBLE
                        }
                    }
                }
                val repeat = it?.schedule?.repeat
                val startTime = it?.schedule?.start_time
                val endTime = it?.schedule?.end_time
                val EVERY_DAY = 127
                val time8 = 8 * 60 * 60 //8点

                val time20 = 20 * 60 * 60 //20点

                when(it?.schedule?.enable){
                   0-> mBinding?.tvPushPlanContent!!.text="not turn on"
                    else->{
                        if (repeat == EVERY_DAY && startTime == time8 && endTime == time20){
                            mBinding?.tvPushPlanContent!!.text="dayTime"
                        }else if (repeat == EVERY_DAY && startTime == time20 && endTime == time8){
                            mBinding?.tvPushPlanContent!!.text="night"
                        }else{
                            mBinding?.tvPushPlanContent!!.text="customize"
                        }

                    }
                }

            }
        }

        mBinding?.apply {
            tvClosePlan.setOnClickListener {
                ivClosePlan.visibility = View.VISIBLE
                ivDayPlan.visibility = View.INVISIBLE
                ivNightPlan.visibility = View.INVISIBLE
                constrainCustom.visibility = View.INVISIBLE
                mViewModel?.mPushIntervalParam.value?.let {
                    it.schedule.enable = OnOff.Off

                    val devParamArray= DevParamArray(
                        PushIntervalSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }
            }
            tvDayPlan.setOnClickListener {
                ivClosePlan.visibility = View.INVISIBLE
                ivDayPlan.visibility = View.VISIBLE
                ivNightPlan.visibility = View.INVISIBLE
                constrainCustom.visibility = View.INVISIBLE
                mViewModel?.mPushIntervalParam.value?.let {
                    it.schedule.apply {
                        enable = OnOff.On
                        start_time = 28800
                        end_time = 72000
                        repeat = 127
                    }

                    val devParamArray= DevParamArray(
                        PushIntervalSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }

            }
            tvNightPlan.setOnClickListener {
                ivClosePlan.visibility = View.INVISIBLE
                ivDayPlan.visibility = View.INVISIBLE
                ivNightPlan.visibility = View.VISIBLE
                constrainCustom.visibility = View.INVISIBLE
                mViewModel?.mPushIntervalParam.value?.let {
                    it.schedule.apply {
                        enable = OnOff.On
                        start_time = 72000
                        end_time = 28800
                        repeat = 127
                    }

                    val devParamArray= DevParamArray(
                        PushIntervalSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }

            }
            tvCustomPlan.setOnClickListener {
                constrainCustom.visibility = View.VISIBLE
            }

            btnYes.setOnClickListener {
                val startTime:Int = etStartH.text.toString().toInt() * 3600 + etStartM.text.toString().toInt() * 60
                val endTime:Int = etEndH.text.toString().toInt() * 3600 + etEndM.text.toString().toInt() * 60
                var repeatW = 0;
                if (checkboxSun.isChecked){
                    repeatW = repeatW.or( 0x01)
                }
                if (checkboxMon.isChecked){
                    repeatW = repeatW.or( (0x01).shl(1))
                }
                if (checkboxTue.isChecked){
                    repeatW = repeatW.or( (0x01).shl(2))
                }
                if (checkboxWed.isChecked){
                    repeatW = repeatW.or( (0x01).shl(3))
                }
                if (checkboxThu.isChecked){
                    repeatW = repeatW.or( (0x01).shl(4))
                }
                if (checkboxFri.isChecked){
                    repeatW = repeatW.or( (0x01).shl(5))
                }
                if (checkboxSat.isChecked){
                    repeatW = repeatW.or( (0x01).shl(6))
                }


                mViewModel?.mPushIntervalParam.value?.let {
                    it.schedule.apply {
                        enable = OnOff.On
                        start_time = startTime
                        end_time = endTime
                        repeat = repeatW
                    }

                    val devParamArray= DevParamArray(
                        PushIntervalSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }
            }

        }


    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView?.id){
            R.id.sw_human_push->{
                mViewModel.mPushIntervalParam.value?.let {
                    it.person_detection_switch = if (isChecked){
                        OnOff.On
                    }else{
                        OnOff.Off
                    }

                    val devParamArray = DevParamArray(
                        PushIntervalSetting,
                        it
                    )

                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }
            }
            R.id.sw_motion_push->{
                mViewModel.mPushIntervalParam.value?.let {
                    it.motion_detection_switch = if (isChecked){
                        OnOff.On
                    }else{
                        OnOff.Off
                    }

                    val devParamArray = DevParamArray(
                        PushIntervalSetting,
                        it
                    )

                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }
            }
            R.id.sw_sound_push->{
                mViewModel.mPushIntervalParam.value?.let {
                    it.sound_detection_switch = if (isChecked){
                        OnOff.On
                    }else{
                        OnOff.Off
                    }

                    val devParamArray = DevParamArray(
                        PushIntervalSetting,
                        it
                    )

                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }
            }
        }

    }

    private fun pushIntervalDialog(){
        if (pushIntervalDialog == null){
            pushIntervalDialog = Dialog(this,R.style.DialogNoBackground)
            val pushIntervalView = LayoutInflater.from(this).inflate(R.layout.dialog_push_interval_layout,null,false)
            pushIntervalDialog!!.setContentView(pushIntervalView)
            val radioRotateGroup:RadioGroup = pushIntervalView.findViewById(R.id.radio_group_interval)
            radioRotateGroup.setOnCheckedChangeListener(this)
            val param: ViewGroup.LayoutParams = pushIntervalView.layoutParams
            param.width = 1080
        }
        pushIntervalDialog!!.show()

    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){
            R.id.radio_1->{
                mBinding?.tvPushIntervalContent?.text = "1min"
                val interval = 1 * 60
                mViewModel.mPushIntervalParam.value?.let {
                    it.interval = interval
                    val devParamArray = DevParamArray(
                        PushIntervalSetting,
                        it
                    )

                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }
            }
            R.id.radio_3->{
                mBinding?.tvPushIntervalContent?.text = "3min"
                val interval = 3 * 60
                mViewModel.mPushIntervalParam.value?.let {
                    it.interval = interval
                    val devParamArray = DevParamArray(
                        PushIntervalSetting,
                        it
                    )

                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }
            }
            R.id.radio_5->{
                mBinding?.tvPushIntervalContent?.text = "5min"
                val interval = 5 * 60
                mViewModel.mPushIntervalParam.value?.let {
                    it.interval = interval
                    val devParamArray = DevParamArray(
                        PushIntervalSetting,
                        it
                    )

                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }
            }
            R.id.radio_10->{
                mBinding?.tvPushIntervalContent?.text = "10min"
                val interval = 10 * 60
                mViewModel.mPushIntervalParam.value?.let {
                    it.interval = interval
                    val devParamArray = DevParamArray(
                        PushIntervalSetting,
                        it
                    )

                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }
            }
            R.id.radio_30->{
                mBinding?.tvPushIntervalContent?.text = "30min"
                val interval = 30 * 60
                mViewModel.mPushIntervalParam.value?.let {
                    it.interval = interval
                    val devParamArray = DevParamArray(
                        PushIntervalSetting,
                        it
                    )

                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }
            }
        }

    }


}