package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityRebootLayoutBinding

/**
 * @Author cw
 * @Date 2023/12/14 11:48
 */
class RebootTimeActivity: BaseActivity<ActivityRebootLayoutBinding,RebootTimeViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_reboot_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        TODO("Not yet implemented")
    }
}