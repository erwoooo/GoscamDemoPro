package com.gocam.goscamdemopro.play

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.*
import com.google.gson.Gson
import com.gos.platform.api.UlifeResultParser
import com.gos.platform.api.UlifeResultParser.EventType.IOTYPE_USER_IPCAM_KEEPALIVE_REQ
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.LowpowerModeSetting
import com.gos.platform.api.domain.DeviceStatus
import com.gos.platform.device.contact.OnOff
import com.gos.platform.device.result.KeepAliveResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PlayViewModel : BaseViewModel<BaseModel>() {
    private var deviceOnline = MutableLiveData<Boolean>()
    val mDeviceOnline: MutableLiveData<Boolean>
        get() = deviceOnline

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    private suspend fun checkLowPower(deviceId: String): Boolean {
        val result = RemoteDataSource.getDeviceParam(
            LowpowerModeSetting,
            deviceId = deviceId
        )   // querry low power mode
        return result!!.let {
            val param = it[0]
            val lowPowerParam = Gson().fromJson(param.DeviceParam, LowPowerParam::class.java)

            return@let lowPowerParam.a_doorbell_lowpower == OnOff.On
        }
    }

    private suspend fun queryDeviceOnlineStatusSyn(deviceId: String): DevicePlatStatus {
        val result = RemoteDataSource.queryDeviceOnlineStatusSyn(deviceId)
        Log.e(TAG, "queryDeviceOnlineStatusSyn: $result")
        return result!!
    }

    fun handleDoorbellWakeup(deviceId: String) {
        viewModelScope.launch {
            val deviceStatus = queryDeviceOnlineStatusSyn(deviceId)
//            val lowPowerMode = checkLowPower(deviceId)
            if (deviceStatus.IsOnline == DeviceStatus.OFFLINE){
                deviceOnline.postValue(true)
            }else {
                var offLine = deviceStatus.IsOnline == DeviceStatus.SLEEP

                val flowJob = flow<WakeUpParam?> {
                    do {
                        delay(2000)
                        val wakeResult = RemoteDataSource.wakeDevice(deviceId)
                        emit(wakeResult)
                    } while (offLine)

                }.collect {
                    Log.e(TAG, "handleDoorbellWakeup: $it")
                    viewModelScope.launch {
                        val deviceStatus = queryDeviceOnlineStatusSyn(deviceId)
                        offLine = deviceStatus.IsOnline == DeviceStatus.ONLINE
                        deviceOnline.postValue(offLine)
                        Log.e(TAG, "handleDoorbellWakeup: $deviceStatus")
                        do {
                            val keepLiveParam = KeepLiveParam(IOTYPE_USER_IPCAM_KEEPALIVE_REQ, 0);
                            val result = RemoteDataSource.getCmdParam(keepLiveParam, deviceId)
                            delay(3000)
                        } while (offLine)
                    }

                }

            }
        }
    }


    fun operatePzt(deviceId: String,direction:Int){
        viewModelScope.launch {
            val pztCmdParam = PztCmdParam(direction,
                UlifeResultParser.EventType.IOTYPE_USER_IPCAM_PTZ_COMMAND,0)
            RemoteDataSource.setCmdReq(deviceId,pztCmdParam)
        }
    }



}