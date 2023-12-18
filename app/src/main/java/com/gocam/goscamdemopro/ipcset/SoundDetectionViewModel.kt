package com.gocam.goscamdemopro.ipcset

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.PushIntervalParam
import com.gocam.goscamdemopro.entity.SoundDetectionParam
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.SoundDetection
import kotlinx.coroutines.launch

/**
 * @Author cw
 * @Date 2023/12/14 14:50
 */
class SoundDetectionViewModel: BaseViewModel<BaseModel>() {

    val mSoundDetectionParam:MutableLiveData<SoundDetectionParam>
    get() = soundSetParam
    private var soundSetParam = MutableLiveData<SoundDetectionParam>()

    fun getSoundDetectionData(devId:String){
        viewModelScope.launch {
            val result = RemoteDataSource.getDeviceParam(
                SoundDetection,
                deviceId = devId
            )
            result!!.let {
                val param = it[0]
                val soundDetectionParam = Gson().fromJson(param.DeviceParam, SoundDetectionParam::class.java)
                soundSetParam.postValue(soundDetectionParam)
            }


        }
    }

}