package com.gocam.goscamdemopro.set

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityCommonSetBinding

class CommonSetActivity : BaseActivity<ActivityCommonSetBinding,CommonSetViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_common_set
    }

    override fun onCreateData(bundle: Bundle?) {
        TODO("Not yet implemented")
    }
}