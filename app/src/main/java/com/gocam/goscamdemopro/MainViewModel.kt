package com.gocam.goscamdemopro

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.base.SingleLiveData
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.entity.LoginBeanResult
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gos.platform.api.GosSession
import kotlinx.coroutines.launch

class MainViewModel : BaseViewModel<BaseModel>() {
    private val deviceResult = MutableLiveData<List<Device>>()
    val mDeviceList: MutableLiveData<List<Device>>
        get() = deviceResult

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }


    fun getDeviceList() {
        viewModelScope.launch {
            val deviceEntity =
                RemoteDataSource.getDeviceList(GApplication.app.user.userName!!)
            Log.e(TAG, "getDeviceList: deviceEntity= $deviceEntity")
            DeviceManager.getInstance().saveDevice(deviceEntity)

            deviceEntity?.let {
                Log.e(TAG, "getDeviceList: $it")
                deviceResult.postValue(it)
            }

        }
    }


}