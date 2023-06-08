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
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.*
import kotlinx.coroutines.launch

class CommonSetViewModel : BaseViewModel<BaseModel>() {
    private var pirParamResult = SingleLiveData<PirParam>()
    val mPirParamResult: SingleLiveData<PirParam>
        get() = pirParamResult

    private var cameraParamResult = SingleLiveData<CameraParam>()
    val mCameraParamResult: SingleLiveData<CameraParam>
        get() = cameraParamResult

    private var nightParamResult = SingleLiveData<NightModeParam>()
    val mNightParamResult: SingleLiveData<NightModeParam>
        get() = nightParamResult

    private var ledParamResult = SingleLiveData<LedSwitchParam>()
    val mPLedParamResult: SingleLiveData<LedSwitchParam>
        get() = ledParamResult

    private var micParamResult = SingleLiveData<MicSwitchParam>()
    val mMicParamResult: SingleLiveData<MicSwitchParam>
        get() = micParamResult

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    fun modifyName(userName: String, deviceId: String) {
        viewModelScope.launch {
            val result = RemoteDataSource.modifyDeviceAttr(deviceId, userName, "", "")
            Log.e(TAG, "modifyName: $result")
        }
    }

    fun setDeviceParam(devParamArray: DevParamArray, deviceId: String) {
        viewModelScope.launch {
            val result = RemoteDataSource.setDeviceParam(devParamArray, deviceId = deviceId)
        }
    }


    fun getDeviceParam(deviceId: String) {
        viewModelScope.launch {
            val result = RemoteDataSource.getDeviceParam(
                PirSetting,
                DeviceSwitch,
                NightSetting,
                LedSwitch,
                MicSwitch,
                deviceId = deviceId
            )
            result!!.let {
                for (param in it) {
                    when (param.CMDType) {
                        PirSetting -> {
                            val pirParam = Gson().fromJson(param.DeviceParam, PirParam::class.java)
                            Log.e(TAG, "getDeviceParam: $pirParam")
                            pirParamResult.postValue(pirParam)
                        }
                        DeviceSwitch -> {
                            val cameraParam =
                                Gson().fromJson(param.DeviceParam, CameraParam::class.java)
                            Log.e(TAG, "getDeviceParam: $cameraParam")
                            cameraParamResult.postValue(cameraParam)
                        }
                        NightSetting -> {
                            val nightModeParam =
                                Gson().fromJson(param.DeviceParam, NightModeParam::class.java)
                            Log.e(TAG, "getDeviceParam: $nightModeParam")
                            nightParamResult.postValue(nightModeParam)
                        }
                        LedSwitch -> {
                            val ledSwitchParam =
                                Gson().fromJson(param.DeviceParam, LedSwitchParam::class.java)
                            Log.e(TAG, "getDeviceParam: $ledSwitchParam")
                            ledParamResult.postValue(ledSwitchParam)
                        }
                        MicSwitch -> {
                            val micSwitchParam =
                                Gson().fromJson(param.DeviceParam, MicSwitchParam::class.java)
                            Log.e(TAG, "getDeviceParam: $micSwitchParam")
                            micParamResult.postValue(micSwitchParam)
                        }
                    }
                }

            }
        }
    }

}