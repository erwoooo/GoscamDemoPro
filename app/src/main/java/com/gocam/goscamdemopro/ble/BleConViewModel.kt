package com.gocam.goscamdemopro.ble

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource

import kotlinx.coroutines.launch

/**
 * @Author cw
 * @Date 2023/12/28 14:44
 */
class BleConViewModel: BaseViewModel<BaseModel>() {


    val mTokenResult: MutableLiveData<String>
    get() = tokenResult
    private var tokenResult =  MutableLiveData<String>()

    fun getBleDevToken(){
        viewModelScope.launch {
            val result = RemoteDataSource.getBindToken(GApplication.app.user.userName!!, "")
            result?.let {
                tokenResult.postValue(it.BindToken)
            }

        }
    }


}