package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityAlarmPushLayoutBinding
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2023/12/14 11:20
 */
class AlarmPushActivity: BaseActivity<ActivityAlarmPushLayoutBinding,AlarmPushViewModel>() {
    lateinit var deviceId:String
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
                    swHumanPush.isChecked = it?.person_detection_switch == OnOff.On
                    swSoundPush.isChecked = it?.sound_detection_switch == OnOff.On
                    swMotionPush.isChecked = it?.motion_detection_switch == OnOff.On
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

    }

}