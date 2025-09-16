package com.gocam.goscamdemopro.set

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.base.SingleLiveData
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.entity.ShareDeviceResult
import kotlinx.coroutines.launch

class DevShareViewModel : BaseViewModel<BaseModel>() {
    private var shareUserList = SingleLiveData<List<String>>()
    val mShareUserList: SingleLiveData<List<String>>
        get() = shareUserList

    private var shareResult = SingleLiveData<ShareDeviceResult>()
    val mShareResult:SingleLiveData<ShareDeviceResult>
    get() = shareResult

    private var unShareResult = SingleLiveData<ShareDeviceResult>()
    val mUnShareResult:SingleLiveData<ShareDeviceResult>
        get() = unShareResult

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }


    fun getShareList(deviceId: String) {
        viewModelScope.launch {
            val result = RemoteDataSource.getShareUserList(deviceId)
            Log.e(TAG, "getShareList: $result")
            result?.UserList?.let {
                shareUserList.postValue(it)
            }
        }
    }

    fun shareDeviceToPeople(username: String, device: Device) {
        viewModelScope.launch {
            val result = RemoteDataSource.shareSmartDevice(
                username,
                device.devId,
                0,
                device.devName,
                device.devType,
                "",
                "",
                "",
                1
            )
            Log.e(TAG, "shareDeviceToPeople: $result")
            shareResult.postValue(result)
        }
    }

    fun unSharedDevice(username: String, deviceId: String) {
        viewModelScope.launch {
            val result = RemoteDataSource.unbindSharedSmartDevice(username, deviceId, 0)
            Log.e(TAG, "unSharedDevice: $result")

            mUnShareResult.postValue(result)
        }
    }
}