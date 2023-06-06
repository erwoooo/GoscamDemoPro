package com.gocam.goscamdemopro.add

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.databinding.ActivityWifiSelectBinding

class WifiSelectActivity:BaseBindActivity<ActivityWifiSelectBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_wifi_select
    }

    override fun onCreateData(bundle: Bundle?) {

    }
}