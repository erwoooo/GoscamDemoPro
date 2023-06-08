package com.gocam.goscamdemopro.set

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.base.SingleLiveData
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.MotionParam
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.MotionDetection
import kotlinx.coroutines.launch

class MotionViewModel : BaseViewModel<BaseModel>() {
    private var motionParam = SingleLiveData<MotionParam>()
    val mMotionParam: SingleLiveData<MotionParam>
        get() = motionParam

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    fun getMotionParam(deviceId: String) {
        viewModelScope.launch {
            val result = RemoteDataSource.getDeviceParam(MotionDetection, deviceId = deviceId)
            result!!.let {
                for (param in it) {
                    if (param.CMDType == MotionDetection) {
                        val motion = Gson().fromJson(param.DeviceParam, MotionParam::class.java)
                        motionParam.postValue(motion)
                    }
                }
            }
        }
    }

    fun setDeviceParam(devParamArray: DevParamArray, deviceId: String) {
        viewModelScope.launch {
            val result = RemoteDataSource.setDeviceParam(devParamArray, deviceId = deviceId)
        }
    }



}