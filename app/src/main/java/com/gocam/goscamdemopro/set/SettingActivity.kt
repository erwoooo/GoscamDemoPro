package com.gocam.goscamdemopro.set

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.databinding.ActivitySettingBinding

class SettingActivity : BaseBindActivity<ActivitySettingBinding>(){
    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun onCreateData(bundle: Bundle?) {

    }
}