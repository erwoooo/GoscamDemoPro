package com.gocam.goscamdemopro.ipcset

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.LowPowerParam
import com.gocam.goscamdemopro.entity.MotionParam
import com.gocam.goscamdemopro.entity.PushIntervalParam
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.api.devparam.PushIntervalSettingParam
import com.gos.platform.device.contact.OnOff
import kotlinx.coroutines.launch

/**
 * @Author cw
 * @Date 2023/12/14 11:20
 */
class AlarmPushViewModel: BaseViewModel<BaseModel>() {

    val mPushIntervalParam:MutableLiveData<PushIntervalParam?>
    get() = pusInterVal
    private var pusInterVal = MutableLiveData<PushIntervalParam?>()

    fun getAlarmPushData(devId:String){
        viewModelScope.launch {
            val result = RemoteDataSource.getDeviceParam(
                DevParam.DevParamCmdType.PushIntervalSetting,
                deviceId = devId
            )
             result!!.let {
                val param = it[0]
                 val pushParam = Gson().fromJson(param.DeviceParam, PushIntervalParam::class.java)
                 pusInterVal.postValue(pushParam)
            }


        }
    }
}