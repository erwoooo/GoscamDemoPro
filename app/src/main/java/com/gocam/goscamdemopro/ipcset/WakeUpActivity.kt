package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityWakeUpLayoutBinding

/**
 * @Author cw
 * @Date 2023/12/14 11:34
 */
class WakeUpActivity: BaseActivity<ActivityWakeUpLayoutBinding,WakeUpViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_wake_up_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        TODO("Not yet implemented")
    }
}