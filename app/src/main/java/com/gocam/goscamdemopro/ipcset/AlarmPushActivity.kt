package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityAlarmPushLayoutBinding

/**
 * @Author cw
 * @Date 2023/12/14 11:20
 */
class AlarmPushActivity: BaseActivity<ActivityAlarmPushLayoutBinding,AlarmPushViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_alarm_push_layout
    }

    override fun onCreateData(bundle: Bundle?) {


    }

}