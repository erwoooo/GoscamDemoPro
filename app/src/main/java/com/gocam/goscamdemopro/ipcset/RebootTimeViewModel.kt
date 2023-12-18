package com.gocam.goscamdemopro.ipcset

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.BaseParamArray
import com.gocam.goscamdemopro.entity.PushIntervalParam
import com.gocam.goscamdemopro.entity.ResetPlanParam
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.ResetSetting
import kotlinx.coroutines.launch

/**
 * @Author cw
 * @Date 2023/12/14 11:48
 */
class RebootTimeViewModel: BaseViewModel<BaseModel>() {

    val mResetPlanParam:MutableLiveData<ResetPlanParam>
    get() = resetSetParam
    private var resetSetParam = MutableLiveData<ResetPlanParam>()
    fun getDevResetData(devId:String){
        viewModelScope.launch {
            viewModelScope.launch {
                val result = RemoteDataSource.getDeviceParam(
                    ResetSetting,
                    deviceId = devId
                )
                result!!.let {
                    val param = it[0]
                    val resetPlanParam = Gson().fromJson(param.DeviceParam, ResetPlanParam::class.java)
                    resetSetParam.postValue(resetPlanParam)
                }


            }

        }
    }


    fun setSwitchParam(baseParamArray: BaseParamArray, devId: String){
        viewModelScope.launch {
            RemoteDataSource.setDeviceParam(baseParamArray, deviceId = devId)
        }
    }
}