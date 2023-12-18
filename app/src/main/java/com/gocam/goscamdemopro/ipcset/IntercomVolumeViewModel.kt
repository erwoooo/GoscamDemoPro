package com.gocam.goscamdemopro.ipcset

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.BaseParamArray
import com.gocam.goscamdemopro.entity.PushIntervalParam
import com.gocam.goscamdemopro.entity.VolumeSetParam
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.VolumeSetting
import kotlinx.coroutines.launch

/**
 * @Author cw
 * @Date 2023/12/14 11:54
 */
class IntercomVolumeViewModel: BaseViewModel<BaseModel>() {

    val mVolumeSetParam:MutableLiveData<VolumeSetParam>
    get() = volumeSetParam
    private var volumeSetParam = MutableLiveData<VolumeSetParam>()

    fun getIntercomData(devId:String){
        viewModelScope.launch {
            val result = RemoteDataSource.getDeviceParam(
                VolumeSetting,
                deviceId = devId
            )
            result!!.let {
                val param = it[0]
                val volumeParam = Gson().fromJson(param.DeviceParam, VolumeSetParam::class.java)
                volumeSetParam.postValue(volumeParam)
            }


        }

    }
    fun setSwitchParam(baseParamArray: BaseParamArray, devId: String){
        viewModelScope.launch {
            RemoteDataSource.setDeviceParam(baseParamArray, deviceId = devId)
        }
    }
}