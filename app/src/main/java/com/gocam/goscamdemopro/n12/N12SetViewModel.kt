package com.gocam.goscamdemopro.n12

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.BaseParamArray
import com.gocam.goscamdemopro.entity.NightStarParam
import com.gocam.goscamdemopro.entity.NightStarRotateParam
import com.gocam.goscamdemopro.entity.NightStatusParma
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.*
import kotlinx.coroutines.launch

/**
 * @Author cw
 * @Date 2024/1/4 11:49
 */
class N12SetViewModel: BaseViewModel<BaseModel>() {

    val mNightStatusParma: MutableLiveData<NightStatusParma>
    get() = nightSettingParam
    private var nightSettingParam = MutableLiveData<NightStatusParma>()

    val mNightStarParam: MutableLiveData<NightStarParam>
    get() = nightStarParam
    private var nightStarParam = MutableLiveData<NightStarParam>()

    val mNightStarRotateParam:MutableLiveData<NightStarRotateParam>
    get() = nightStarRotateParam
    private var nightStarRotateParam = MutableLiveData<NightStarRotateParam>()

    fun getDevParam(deviceId:String){
        viewModelScope.launch {
            val result = RemoteDataSource.getDeviceParam(
                NightStatusSetting,
                NightStarSetting,
                NightStarRotateSetting,
                deviceId = deviceId
            )
            result?.let {
                for (param in it){
                    when(param.CMDType){
                        NightStatusSetting->{
                            val setParma = Gson().fromJson(param.DeviceParam,NightStatusParma::class.java)
                            nightSettingParam.postValue(setParma)
                        }

                        NightStarSetting->{
                            val starParam = Gson().fromJson(param.DeviceParam,NightStarParam::class.java)
                            nightStarParam.postValue(starParam)
                        }

                        NightStarRotateSetting->{
                            val starRotateParam = Gson().fromJson(param.DeviceParam,NightStarRotateParam::class.java)
                            nightStarRotateParam.postValue(starRotateParam)
                        }
                    }
                }


            }


        }
    }


    fun setDevParam(deviceId: String,param: N12SetParam){
        viewModelScope.launch {
            Log.e(TAG, "setDevParam: param = $param", )
            val cmdParam = N12CmdParam(
                param = param,
                4,
                4946,
                0
            )
            Log.e(TAG, "setDevParam: cmdParam = $cmdParam")
           val result =  RemoteDataSource.setCmdReq(deviceId,cmdParam)
            Log.e(TAG, "setDevParam: $result")
        }
    }


    fun setSwitchParam(baseParamArray: BaseParamArray, devId: String){
        viewModelScope.launch {
            RemoteDataSource.setDeviceParam(baseParamArray, deviceId = devId)
        }
    }

}