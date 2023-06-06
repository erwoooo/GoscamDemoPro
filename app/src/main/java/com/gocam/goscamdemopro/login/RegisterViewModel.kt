package com.gocam.goscamdemopro.login

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.GosApplication
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gos.platform.api.contact.FindType
import com.gos.platform.api.contact.RegistWay
import com.gos.platform.api.contact.VerifyWay
import kotlinx.coroutines.launch

class RegisterViewModel:BaseViewModel<BaseModel>() {

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    fun getVerifyCode(userInfo:String){
        viewModelScope.launch {
           val response =  RemoteDataSource.getVerificationCode(FindType.EMAIL_ADDRESS,userInfo, VerifyWay.REGIST,GosApplication.application.userType,"+86",null)
            Log.e(TAG, "getVerifyCode: ${response}")
        }
    }

    fun register(userInfo: String,psw:String,code:String){
        viewModelScope.launch {
            val response = RemoteDataSource.register(GosApplication.application.userType,RegistWay.EMAIL,userInfo,psw,"",userInfo,code,"+86")
            Log.e(TAG, "register: ${response}" )
        }
    }
}