package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import android.widget.CompoundButton
import android.widget.SeekBar
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivitySoundDetectionLayoutBinding
import com.gocam.goscamdemopro.entity.BaseParamArray
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.SoundDetectionParam
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.device.contact.AudioDetectLevel
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2023/12/14 14:49
 */
class SoundDetectionActivity: BaseActivity<ActivitySoundDetectionLayoutBinding,SoundDetectionViewModel>()
    ,CompoundButton.OnCheckedChangeListener,SeekBar.OnSeekBarChangeListener {
    lateinit var devId:String
    override fun getLayoutId(): Int {
        return R.layout.activity_sound_detection_layout
    }

    override fun onCreateData(bundle: Bundle?) {
         devId = intent.getStringExtra("dev") as String
        mViewModel.getSoundDetectionData(devId)

        mViewModel.apply {
            mSoundDetectionParam.observe(this@SoundDetectionActivity){
                mBinding?.swSound?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.un_switch == OnOff.On
                    setOnCheckedChangeListener(this@SoundDetectionActivity)
                }
                mBinding?.seekSoundSensor?.setOnSeekBarChangeListener(this@SoundDetectionActivity)
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

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        val soundDetectionParam = SoundDetectionParam(
            if (isChecked){OnOff.On}else{OnOff.Off},
            mBinding?.seekSoundSensor?.progress!!
        )
        val baseParamArray = DevParamArray(
            DevParam.DevParamCmdType.SoundDetection,
            soundDetectionParam
        )
        mViewModel.setSwitchParam(baseParamArray, devId = devId)

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        val soundDetectionParam = SoundDetectionParam(
            if (mBinding?.swSound?.isChecked == true){OnOff.On}else{OnOff.Off},
            when(seekBar?.progress!!){
                0->{
                    AudioDetectLevel.LOW
                }
                1->{
                    AudioDetectLevel.MIDDLE
                }
                2->{
                    AudioDetectLevel.HEIGH
                }
                else->{
                AudioDetectLevel.LOW
                }
            }
        )
        val baseParamArray = DevParamArray(
            DevParam.DevParamCmdType.SoundDetection,
            soundDetectionParam
        )
        mViewModel.setSwitchParam(baseParamArray, devId = devId)
    }
}