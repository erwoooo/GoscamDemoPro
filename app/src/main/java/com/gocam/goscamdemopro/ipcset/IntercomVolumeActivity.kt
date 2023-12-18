package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import android.widget.SeekBar
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityIntercomLayoutBinding
import com.gocam.goscamdemopro.databinding.ActivityVolumeBinding
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.VolumeSetParam
import com.gos.platform.api.devparam.DevParam

/**
 * @Author cw
 * @Date 2023/12/14 11:53
 */
class IntercomVolumeActivity: BaseActivity<ActivityIntercomLayoutBinding,IntercomVolumeViewModel>(),SeekBar.OnSeekBarChangeListener {
    lateinit var devId:String
    override fun getLayoutId(): Int {
        return R.layout.activity_intercom_layout
    }

    override fun onCreateData(bundle: Bundle?) {
         devId = intent.getStringExtra("dev") as String

        mViewModel.getIntercomData(devId)

        mViewModel.apply {
            mVolumeSetParam.observe(this@IntercomVolumeActivity){
                mBinding?.seekIpcVolume?.progress = it.volume
                mBinding?.tvProgress?.text = "${it.volume}"
            }
        }

        mBinding?.apply {
            seekIpcVolume.setOnSeekBarChangeListener(this@IntercomVolumeActivity)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        mBinding?.tvProgress?.text="${seekBar?.progress}"
        val volumeSetParam = VolumeSetParam(seekBar?.progress!!)
        val devParamArray = DevParamArray(
            DevParam.DevParamCmdType.VolumeSetting,
            volumeSetParam
        )
        mViewModel.setSwitchParam(devParamArray, devId = devId)
    }
}