package com.gocam.goscamdemopro.set

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.base.SingleLiveData
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.*
import com.google.gson.Gson
import com.gos.platform.api.UlifeResultParser
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.api.devparam.DevParam.DevParamCmdType
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.UpgradeStatus
import kotlinx.coroutines.launch
import kotlin.math.log

class DevUpdateViewModel : BaseViewModel<BaseModel>() {
    private var firmWareParam = SingleLiveData<FirmWareParam>()
    val mFirmWareParam: SingleLiveData<FirmWareParam>
        get() = firmWareParam

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }


    fun getDeviceFirmware(deviceType: String) {
        viewModelScope.launch {
            val response = RemoteDataSource.checkNewVer(deviceType)
            mFirmWareParam.postValue(response)

        }
    }


    fun setDeviceParam(devParamArray: DevParamArray, deviceId: String) {
        viewModelScope.launch {
            val result = RemoteDataSource.setDeviceParam(devParamArray, deviceId = deviceId)
            Log.e(TAG, "setDeviceParam: $result")
        }
    }

    fun setDeviceUpdate(updateParam: GetUpgradeInfoRequestParam, deviceId: String) {
        viewModelScope.launch {
            val result = RemoteDataSource.getCmdParam(updateParam, deviceId = deviceId)
            Log.e(TAG, "setDeviceUpdate: $result")

            getUpdateProgressParam(deviceId)
        }
    }

    //Obtain once per second
    fun getUpdateProgressParam(deviceId: String) {
        viewModelScope.launch {
            val result = RemoteDataSource.getDeviceParam(UpgradeStatus, deviceId = deviceId)
            result!!.let {
                for (param in it) {
                    if (param.CMDType == UpgradeStatus) {
                        val progress = Gson().fromJson(param.DeviceParam, UpgradeStatusParam::class.java)
                        Log.e(TAG, "getUpdateProgressParam: $progress", )
                    }
                }
            }
        }
    }
}