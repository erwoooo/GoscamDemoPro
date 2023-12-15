package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityIntercomLayoutBinding
import com.gocam.goscamdemopro.databinding.ActivityVolumeBinding

/**
 * @Author cw
 * @Date 2023/12/14 11:53
 */
class IntercomVolumeActivity: BaseActivity<ActivityIntercomLayoutBinding,IntercomVolumeViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_intercom_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        TODO("Not yet implemented")
    }
}