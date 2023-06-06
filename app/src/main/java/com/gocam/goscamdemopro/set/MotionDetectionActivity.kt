package com.gocam.goscamdemopro.set

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityMotionDetectionBinding

class MotionDetectionActivity : BaseActivity<ActivityMotionDetectionBinding,MotionViewModel>(){
    override fun getLayoutId(): Int {
        return R.layout.activity_motion_detection
    }

    override fun onCreateData(bundle: Bundle?) {
        TODO("Not yet implemented")
    }
}