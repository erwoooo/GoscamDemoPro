package com.gocam.goscamdemopro.login

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope

import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.base.SingleLiveData
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.LoginBeanResult
import com.gocam.goscamdemopro.utils.Util
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel<BaseModel>() {

    private val loginResult = SingleLiveData<LoginBeanResult>()
    val mLoginResult: SingleLiveData<LoginBeanResult>
        get() = loginResult
    private var isGetSuc: Boolean = false

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

    }

    fun login(username: String, psw: String) {
        viewModelScope.launch {
            val result = RemoteDataSource.login(username, psw)
            loginResult.postValue(result)
        }
    }


}