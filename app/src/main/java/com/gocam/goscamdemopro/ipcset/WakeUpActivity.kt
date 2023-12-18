package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import android.util.Log
import android.view.View
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityWakeUpLayoutBinding
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2023/12/14 11:34
 */
class WakeUpActivity: BaseActivity<ActivityWakeUpLayoutBinding,WakeUpViewModel>() {
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
                    }
                }else if (repeat == EVERY_DAY && startTime == time8 && endTime == time20){
                    mBinding?.apply {
                        ivClosePlan.visibility = View.INVISIBLE
                        ivDayPlan.visibility = View.VISIBLE
                        ivNightPlan.visibility = View.INVISIBLE
                    }

                }else if (repeat == EVERY_DAY && startTime == time20 && endTime == time8){
                    mBinding?.apply {
                        ivClosePlan.visibility = View.INVISIBLE
                        ivDayPlan.visibility = View.INVISIBLE
                        ivNightPlan.visibility = View.VISIBLE
                    }
                }else{
                    mBinding?.apply {
                        ivClosePlan.visibility = View.INVISIBLE
                        ivDayPlan.visibility = View.INVISIBLE
                        ivNightPlan.visibility = View.INVISIBLE
                    }
                }

            }

            mDevSwitchParam.observe(this@WakeUpActivity){
                Log.e(TAG, "onCreateData: it$it", )
                mBinding?.swWakeUp?.isChecked = it.device_switch == OnOff.On
            }

        }


    }
}