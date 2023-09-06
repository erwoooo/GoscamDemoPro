package com.gocam.goscamdemopro.set

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.base.SingleLiveData
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.TFInfoParam
import com.gocam.goscamdemopro.entity.TfDeviceParam
import com.google.gson.Gson
import com.gos.platform.api.UlifeResultParser.EventType.IOTYPE_USER_IPCAM_FORMAT_STORAGE_REQ
import com.gos.platform.api.UlifeResultParser.EventType.IOTYPE_USER_IPCAM_GET_STORAGE_INFO_REQ
import kotlinx.coroutines.launch

class TfInfoViewModel : BaseViewModel<BaseModel>() {
    private var tfInfoParam = SingleLiveData<TFInfoParam>()
    val mTfInfoParam: SingleLiveData<TFInfoParam>
        get() = tfInfoParam

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    fun getTfInfo(deviceId: String) {
        viewModelScope.launch {
            val tfDeviceParam = TfDeviceParam(IOTYPE_USER_IPCAM_GET_STORAGE_INFO_REQ, 1,0)
            val result = RemoteDataSource.getCmdParam(tfDeviceParam, deviceId = deviceId)
            result!!.let {
                val response = Gson().fromJson(it.DeviceParam, TFInfoParam::class.java)
                tfInfoParam.postValue(response)
            }
        }
    }

    fun formatSd(deviceId: String) {
        viewModelScope.launch {
            val tfDeviceParam = TfDeviceParam(IOTYPE_USER_IPCAM_FORMAT_STORAGE_REQ, 1,0)
            val result = RemoteDataSource.getCmdParam(tfDeviceParam, deviceId = deviceId)
            result!!.let {
                val response = Gson().fromJson(it.DeviceParam, TFInfoParam::class.java)
                tfInfoParam.postValue(response)
            }
        }
    }
}