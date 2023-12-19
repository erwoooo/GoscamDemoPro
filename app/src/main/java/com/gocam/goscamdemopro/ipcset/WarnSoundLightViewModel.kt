package com.gocam.goscamdemopro.ipcset

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.BaseParamArray
import com.gocam.goscamdemopro.entity.VoicePlayParam
import com.gocam.goscamdemopro.entity.WarnSettingParam
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.WarnSetting
import kotlinx.coroutines.launch

/**
 * @Author cw
 * @Date 2023/12/18 17:41
 */
class WarnSoundLightViewModel: BaseViewModel<BaseModel>() {
    val mWarnSettingParam : MutableLiveData<WarnSettingParam>
    get() = warnSettingParam
    private var warnSettingParam = MutableLiveData<WarnSettingParam>()

    val mVoicePlayParam: MutableLiveData<VoicePlayParam>
    get() = voicePlayParam
    private var voicePlayParam = MutableLiveData<VoicePlayParam>()
    fun getWarnData(devId: String){
        viewModelScope.launch {
            val result = RemoteDataSource.getDeviceParam(
                WarnSetting,
                deviceId = devId
            )
            result.let {
                val param = it[0]
                val warnParam = Gson().fromJson(param.DeviceParam, WarnSettingParam::class.java)
                warnSettingParam.postValue(warnParam)
            }


        }
    }

    fun getPlayContent(devId: String){
        viewModelScope.launch {
            val result = RemoteDataSource.getVoicePlay(devId)

            result?.let {
                voicePlayParam.postValue(it)
            }

        }
    }

    fun setSwitchParam(baseParamArray: BaseParamArray, devId: String){
        viewModelScope.launch {
            RemoteDataSource.setDeviceParam(baseParamArray, deviceId = devId)
        }
    }
}