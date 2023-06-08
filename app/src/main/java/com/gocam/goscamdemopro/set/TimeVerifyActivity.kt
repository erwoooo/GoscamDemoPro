package com.gocam.goscamdemopro.set

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityTimeVerifyBinding

class TimeVerifyActivity : BaseActivity<ActivityTimeVerifyBinding, TimeVerifyViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_time_verify

    override fun onCreateData(bundle: Bundle?) {
        val deviceId = intent.getStringExtra("dev") as String
        mBinding?.btnVerify?.setOnClickListener {
            mViewModel.setTimeVerify(deviceId)
        }

        mBinding?.toolBar?.backImg?.setOnClickListener {
            finish()
        }
    }
}