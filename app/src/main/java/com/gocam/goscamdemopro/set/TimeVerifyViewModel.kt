package com.gocam.goscamdemopro.set

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.entity.TimeParam
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.TimeZoneInfo
import kotlinx.coroutines.launch

class TimeVerifyViewModel: BaseViewModel<BaseModel>() {

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    fun setTimeVerify(deviceId:String){
        val device = DeviceManager.getInstance().findDeviceById(deviceId)
        viewModelScope.launch {
            val timeParam = TimeParam(
                (System.currentTimeMillis() / 1000).toInt(),
                device.timezoneVerifyType
            )
            val devParamArray = DevParamArray(
                TimeZoneInfo,
                timeParam
            )
            val result = RemoteDataSource.setDeviceParam(devParamArray,deviceId = deviceId)
        }
    }
}