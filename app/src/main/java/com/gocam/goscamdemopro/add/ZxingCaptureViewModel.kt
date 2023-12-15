package com.gocam.goscamdemopro.add

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.base.SingleLiveData
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.BindStatus
import com.gocam.goscamdemopro.entity.BindTokenResult

import kotlinx.coroutines.launch

/**
 * @Author cw
 * @Date 2023/12/15 9:41
 */
class ZxingCaptureViewModel: BaseViewModel<BaseModel>() {
    val bindStatus:MutableLiveData<BindStatus?>
    get() = checkBindStatus
    private var checkBindStatus =  MutableLiveData<BindStatus?>()

    val bindToken:MutableLiveData<BindTokenResult?>
    get() = getBindTokenResult
    private var getBindTokenResult = MutableLiveData<BindTokenResult?>()
    fun queryBindStatus(devId:String){
        viewModelScope.launch {
           val result =  RemoteDataSource.checkBindStatus(devId)
            checkBindStatus.postValue(result)
        }
    }

    fun getBindToken(devId: String){
        viewModelScope.launch {
            val result = RemoteDataSource.getBindToken(GApplication.app.user.userName!!,devId)
            getBindTokenResult.postValue(result)
        }
    }
}