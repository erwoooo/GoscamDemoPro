package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivitySoundDetectionLayoutBinding

/**
 * @Author cw
 * @Date 2023/12/14 14:49
 */
class SoundDetectionActivity: BaseActivity<ActivitySoundDetectionLayoutBinding,SoundDetectionViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_sound_detection_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        TODO("Not yet implemented")
    }
}