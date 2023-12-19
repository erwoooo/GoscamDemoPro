package com.gocam.goscamdemopro.ipcset

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.BaseParamArray
import com.gocam.goscamdemopro.entity.SmartPersonParam
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.SmdAlarmSetting
import kotlinx.coroutines.launch

/**
 * @Author cw
 * @Date 2023/12/19 11:16
 */
class SmartPersonViewModel:BaseViewModel<BaseModel>() {

    val mSmartPersonParam: MutableLiveData<SmartPersonParam>
    get() = smartPersonParam
    private var smartPersonParam = MutableLiveData<SmartPersonParam>()

    fun getSmartData(devId:String){
        viewModelScope.launch {
           val result =  RemoteDataSource.getDeviceParam(SmdAlarmSetting, deviceId = devId)
            result.let {
                val param = it[0]
                val smartParam = Gson().fromJson(param.DeviceParam, SmartPersonParam::class.java)
                smartPersonParam.postValue(smartParam)
            }

        }
    }

    fun setSwitchParam(baseParamArray: BaseParamArray, devId: String){
        viewModelScope.launch {
            RemoteDataSource.setDeviceParam(baseParamArray, deviceId = devId)
        }
    }


}