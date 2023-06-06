package com.gocam.goscamdemopro.set

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityDevShareBinding

class DevShareActivity:BaseActivity<ActivityDevShareBinding,DevShareViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_dev_share

    override fun onCreateData(bundle: Bundle?) {

    }
}