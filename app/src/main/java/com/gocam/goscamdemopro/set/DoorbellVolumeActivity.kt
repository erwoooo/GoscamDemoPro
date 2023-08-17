package com.gocam.goscamdemopro.set

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityVolumeBinding
import com.gocam.goscamdemopro.entity.BaseDeviceParam
import com.gocam.goscamdemopro.entity.BaseParamArray
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.device.contact.OnOff
import kotlinx.coroutines.launch

class DoorbellVolumeActivity : AppCompatActivity(){
    lateinit var mBinding : ActivityVolumeBinding

    lateinit var deviceId :String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_volume)
        deviceId = intent.getStringExtra("dev").toString()
        val mDoorbellVolumeParam  = DoorbellVolumeParam()
        mBinding.seekDoorVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    mDoorbellVolumeParam.doorbell_ring_switch = OnOff.On
                    mDoorbellVolumeParam.volume_level = progress
                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.DoorbellVolume,
                        mDoorbellVolumeParam
                    )
                    lifecycleScope.launch {
                        RemoteDataSource.setDeviceParam(devParamArray,deviceId = deviceId)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    inner class DoorbellVolumeParam : BaseDeviceParam(){
        var doorbell_ring_switch:Int = 0
        var volume_level:Int = 0;
    }

}