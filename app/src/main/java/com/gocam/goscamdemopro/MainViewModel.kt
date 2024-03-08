package com.gocam.goscamdemopro

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.cloud.data.GosCloud
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gocam.goscamdemopro.utils.SharedPreferencesUtil
import com.gocam.goscamdemopro.vphoto.data.SelectDeviceResult
import com.gocam.goscamdemopro.vphoto.data.SigninByUuidResult
import com.gos.platform.api.contact.DeviceType
import com.gos.platform.api.contact.PlatCode
import com.gos.platform.api.inter.OnPlatformEventCallback
import com.gos.platform.api.result.PlatResult
import kotlinx.coroutines.launch

class MainViewModel : BaseViewModel<BaseModel>(), OnPlatformEventCallback {
    private val deviceResult = MutableLiveData<List<Device>>()
    val mDeviceList: MutableLiveData<List<Device>>
        get() = deviceResult

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        GosCloud.getCloud().addOnPlatformEventCallback(this)
    }


    fun getDeviceList() {
        viewModelScope.launch {
            val deviceEntity =
                RemoteDataSource.getDeviceList(GApplication.app.user.userName!!)
            Log.e(TAG, "getDeviceList: deviceEntity= $deviceEntity")
            DeviceManager.getInstance().saveDevice(deviceEntity)
            refreshDataStatus()
            deviceEntity?.let {
                Log.e(TAG, "getDeviceList: $it")
                deviceResult.postValue(it)
            }

        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        GosCloud.getCloud().removeOnPlatformEventCallback(this)
    }

    private fun refreshDataStatus() {
        val deviceList = DeviceManager.getInstance().deviceList
        var hasVPhoto: Boolean = false
        for (device: Device in deviceList) {
            if (device.devType == DeviceType.V_PHOTO) {
                hasVPhoto = true
                break
            }
        }
        if (hasVPhoto) {
            if (GApplication.app.vPhotoUser.vphotoAccessToken == null) {
                GosCloud.getCloud().signinByUuid(GApplication.app.user.userName)
            } else {
                GosCloud.getCloud().selectByDevice(
                    GApplication.app.vPhotoUser.vphotoUserSystemToken,
                    GApplication.app.vPhotoUser.vphotoAccessToken,
                    GApplication.app.vPhotoUser.vphotUserId
                )
            }
        }
    }

    override fun OnPlatformEvent(result: PlatResult?) {
        val code = result?.responseCode
        when(result?.platCmd) {
            PlatResult.PlatCmd.signinByUuid -> {
                if (code == PlatCode.SUCCESS) {
                    val signinResult = result as SigninByUuidResult
                    GApplication.app.vPhotoUser.vphotUserId = signinResult.signinBean.user_id
                    GApplication.app.vPhotoUser.vphotoAccessToken = signinResult.signinBean.access_token
                    GApplication.app.vPhotoUser.vphotoUserSystemToken = signinResult.signinBean.user_system_token
                    GosCloud.getCloud().selectByDevice(
                        GApplication.app.vPhotoUser.vphotoUserSystemToken,
                        GApplication.app.vPhotoUser.vphotoAccessToken,
                        GApplication.app.vPhotoUser.vphotUserId
                    )
                }
            }
            PlatResult.PlatCmd.getUserDevice -> {
                if (code == PlatCode.SUCCESS) {
                    val selectResult = result as SelectDeviceResult
                    DeviceManager.getInstance().setPhotoDeviceList(selectResult.deleteBean.data)
                    if (!SharedPreferencesUtil.getBoolean(SharedPreferencesUtil.SpreContant.V_PHOTO_DEVICE_CAM, false)) {
                        GosCloud.getCloud().updateDeviceCam(
                            GApplication.app.vPhotoUser.vphotoAccessToken,
                            GApplication.app.vPhotoUser.vphotUserId
                        )
                    }
                }
            }
        }
    }


}