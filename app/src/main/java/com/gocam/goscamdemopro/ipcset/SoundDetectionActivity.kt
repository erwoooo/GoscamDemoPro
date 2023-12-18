package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivitySoundDetectionLayoutBinding
import com.gos.platform.device.contact.AudioDetectLevel
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2023/12/14 14:49
 */
class SoundDetectionActivity: BaseActivity<ActivitySoundDetectionLayoutBinding,SoundDetectionViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_sound_detection_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev") as String
        mViewModel.getSoundDetectionData(devId)

        mViewModel.apply {
            mSoundDetectionParam.observe(this@SoundDetectionActivity){
                mBinding?.swSound?.isChecked = it.un_switch == OnOff.On
                when(it.un_sensitivity){
                    AudioDetectLevel.LOW->{
                        mBinding?.seekSoundSensor?.progress = 0
                    }
                    AudioDetectLevel.MIDDLE->{
                        mBinding?.seekSoundSensor?.progress = 1
                    }
                    AudioDetectLevel.HEIGH->{
                        mBinding?.seekSoundSensor?.progress = 2
                    }
                }

            }
        }
    }
}