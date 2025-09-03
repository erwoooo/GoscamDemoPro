package com.gocam.goscamdemopro.peripheral

import android.text.TextUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.cloud.data.GosCloud
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.PeripheralSetting
import com.gocam.goscamdemopro.entity.PeripheralParam
import com.gos.platform.api.inter.OnPlatformEventCallback
import com.gos.platform.api.result.DeviceParamReportNotifyResult
import com.gos.platform.api.result.PlatResult
import kotlinx.coroutines.launch

/**
 *
 * @Author wuzb
 * @Date 2025/08/28 10:59
 */
class PeripheralListViewModel : BaseViewModel<BaseModel>(), OnPlatformEventCallback {
    private var peripheralParam = MutableLiveData<PeripheralParam>()
    val mPeripheralParam: MutableLiveData<PeripheralParam>
        get() = peripheralParam

    private var mDeviceId: String? = null

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        GosCloud.getCloud().addOnPlatformEventCallback(this)
    }

    fun getPeripheralParam(deviceId: String) {
        mDeviceId = deviceId
        viewModelScope.launch {
            val result = RemoteDataSource.getDeviceParam(PeripheralSetting, deviceId = deviceId)
            result.let {
                for (param in it) {
                    if (param.CMDType == PeripheralSetting) {
                        val peripheral = Gson().fromJson(param.DeviceParam, PeripheralParam::class.java)
                        peripheralParam.postValue(peripheral)
                    }
                }
            }
        }
    }

    override fun OnPlatformEvent(result: PlatResult?) {
        val cmd: PlatResult.PlatCmd? = result?.platCmd
        cmd?.let {
            if (it == PlatResult.PlatCmd.DeviceParamReportNotify) {
                val reportNotifyResult = result as DeviceParamReportNotifyResult
                if (TextUtils.equals(reportNotifyResult.deviceId, mDeviceId)) {
                    var i = 0
                    while (reportNotifyResult.responseCode == 0 && reportNotifyResult.devParamList != null && i < reportNotifyResult.devParamList.size) {
                        val devParam = reportNotifyResult.devParamList[i]
                        when (devParam.CMDType) {
                            PeripheralSetting -> {
                                val peripheral = devParam as PeripheralParam
                                peripheralParam.postValue(peripheral)
                            }
                        }
                        i++
                    }
                }
            }
        }
    }
}