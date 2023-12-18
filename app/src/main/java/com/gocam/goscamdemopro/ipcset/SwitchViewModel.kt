package com.gocam.goscamdemopro.ipcset

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.*
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.*
import kotlinx.coroutines.launch

/**
 * @Author cw
 * @Date 2023/12/14 11:10
 */
class SwitchViewModel: BaseViewModel<BaseModel>() {

    val mTfRecordParam: MutableLiveData<TfRecordParam>
    get() = tfRecordSetParam
    private var tfRecordSetParam = MutableLiveData<TfRecordParam>()
    val mLedSwitchParam: MutableLiveData<LedSwitchParam>
    get() = ledSetParam
    private var ledSetParam = MutableLiveData<LedSwitchParam>()
    val mObjTrackParam: MutableLiveData<ObjTrackParam>
    get() = objTrackSetParam
    private var objTrackSetParam = MutableLiveData<ObjTrackParam>()

    fun getThirdSwitch(devId:String){
        viewModelScope.launch {
            val result = RemoteDataSource.getDeviceParam(
                TfRecordSetting,
                LedSwitch,
                ObjTrackSetting,
                deviceId = devId
            )
            result!!.let {
                for (param in it){
                    when(param.CMDType){
                        TfRecordSetting->{
                            val tfRecordParam = Gson().fromJson(param.DeviceParam, TfRecordParam::class.java)
                            tfRecordSetParam.postValue(tfRecordParam)
                        }
                        LedSwitch->{
                            val ledSwitchParam = Gson().fromJson(param.DeviceParam,
                                LedSwitchParam::class.java)
                            mLedSwitchParam.postValue(ledSwitchParam)
                        }
                        ObjTrackSetting->{
                            val objSwitchParam = Gson().fromJson(param.DeviceParam,
                                ObjTrackParam::class.java)
                            objTrackSetParam.postValue(objSwitchParam)
                        }

                    }
                }
            }

        }
    }
}