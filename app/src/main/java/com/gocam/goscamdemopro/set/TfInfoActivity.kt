package com.gocam.goscamdemopro.set

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityTfInfoBinding

class TfInfoActivity : BaseActivity<ActivityTfInfoBinding,TfInfoViewModel>(){
    override fun getLayoutId(): Int {
        return R.layout.activity_tf_info
    }

    override fun onCreateData(bundle: Bundle?) {
        TODO("Not yet implemented")
    }
}