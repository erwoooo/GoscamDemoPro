package com.gocam.goscamdemopro.add

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityCheckBindStatusBinding

class CheckBindStatusActivity :
    BaseActivity<ActivityCheckBindStatusBinding, CheckBindStatusViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_check_bind_status
    }

    override fun onCreateData(bundle: Bundle?) {

    }
}