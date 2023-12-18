package com.gocam.goscamdemopro.ipcset

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.CameraPlanParam
import com.gocam.goscamdemopro.entity.DevSwitchParam

import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.CameraSwitchPlanSetting
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.DeviceSwitch
import kotlinx.coroutines.launch

/**
 * @Author cw
 * @Date 2023/12/14 11:34
 */
class WakeUpViewModel: BaseViewModel<BaseModel>() {

    val mDevSwitchParam:MutableLiveData<DevSwitchParam>
    get() = devSwitchSetParam
    private var devSwitchSetParam = MutableLiveData<DevSwitchParam>()

    val mCameraPlanParam:MutableLiveData<CameraPlanParam>
        get() = cameraPlanSetParam
    private var cameraPlanSetParam = MutableLiveData<CameraPlanParam>()

    fun getCameraSet(devId:String){
        viewModelScope.launch {
            val result = RemoteDataSource.getDeviceParam(
                CameraSwitchPlanSetting,
                DeviceSwitch,
                deviceId = devId
            )
            result!!.let {
                for (param in it){
                    when(param.CMDType){
                        DeviceSwitch->{
                            val devSwitchParam = Gson().fromJson(param.DeviceParam, DevSwitchParam::class.java)
                            devSwitchSetParam.postValue(devSwitchParam)
                        }
                        CameraSwitchPlanSetting->{
                            val cameraSwitchParam = Gson().fromJson(param.DeviceParam,
                                CameraPlanParam::class.java)
                            cameraPlanSetParam.postValue(cameraSwitchParam)
                        }

                    }
                }
            }



        }
    }
}