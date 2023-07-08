package com.gocam.goscamdemopro.login

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope

import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.base.SingleLiveData
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.LoginBeanResult
import com.gocam.goscamdemopro.utils.Util
import com.gos.platform.api.ConfigManager
import com.gos.platform.api.GosSession
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel<BaseModel>() {

    private val loginResult = SingleLiveData<LoginBeanResult>()
    val mLoginResult: SingleLiveData<LoginBeanResult>
        get() = loginResult
    private var isGetSuc: Boolean = true

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

    }

    fun login(username: String, psw: String,uuid:String) {
        viewModelScope.launch {
            if(isGetSuc){
                GosSession.getSession().appGetBSAddress(uuid,"")
                val result = RemoteDataSource.login(username, psw)
                loginResult.postValue(result)
            }else{

                val bsResult = RemoteDataSource.appGetBSAddress(uuid,"")
                Log.e(TAG, "login: $bsResult" )
                isGetSuc = true
                if (bsResult!!.ResultCode == 0){
                    val result = RemoteDataSource.login(username, psw)
                    loginResult.postValue(result)

                }

            }

        }
    }


}