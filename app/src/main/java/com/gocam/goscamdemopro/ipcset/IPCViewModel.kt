package com.gocam.goscamdemopro.ipcset

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.*
import com.google.gson.Gson
import com.gos.platform.api.UlifeResultParser
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.MirrorModeSetting
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.NightSetting
import com.gos.platform.api.devparam.MirrorModeSettingParam
import kotlinx.coroutines.launch

/**
 * @Author cw
 * @Date 2023/12/14 10:38
 */
class IPCViewModel: BaseViewModel<BaseModel>() {

    val mNightModeParam:MutableLiveData<NightModeParam>
    get() = nightSettingParam
    private var nightSettingParam = MutableLiveData<NightModeParam>()

    val mMirrorModeParam:MutableLiveData<MirrorModeParam>
    get() = mirrorModeSettingParam
    private var mirrorModeSettingParam = MutableLiveData<MirrorModeParam>()

    fun getData(devId:String){
        viewModelScope.launch {
            viewModelScope.launch {
                val result = RemoteDataSource.getDeviceParam(
                    MirrorModeSetting,
                    NightSetting,
                    deviceId = devId
                )
                result!!.let {
                    for (param in it){
                        when (param.CMDType){
                            NightSetting->{
                                val nightModeParam =
                                    Gson().fromJson(param.DeviceParam, NightModeParam::class.java)
                                nightSettingParam.postValue(nightModeParam)
                            }
                            MirrorModeSetting->{
                                val mirrorModeParam =
                                    Gson().fromJson(param.DeviceParam, MirrorModeParam::class.java)
                                mirrorModeSettingParam.postValue(mirrorModeParam)
                            }
                        }
                    }

                }


            }

        }
    }



    fun setSwitchParam(baseParamArray: BaseParamArray, devId: String){
        viewModelScope.launch {
            RemoteDataSource.setDeviceParam(baseParamArray, deviceId = devId)
        }
    }

    fun rebootParam(devId: String){
        viewModelScope.launch {
            val rebootParam = RebootParam(
                UlifeResultParser.EventType.BYPASS_RESET_DEV_REQ,0)
            RemoteDataSource.setCmdReq(devId,rebootParam)
        }
    }

}