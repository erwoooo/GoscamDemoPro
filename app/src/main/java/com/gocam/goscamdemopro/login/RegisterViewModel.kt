package com.gocam.goscamdemopro.login

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.GApplication

import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.base.SingleLiveData
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.entity.CommonResult
import com.gos.platform.api.contact.FindType
import com.gos.platform.api.contact.RegistWay
import com.gos.platform.api.contact.VerifyWay
import kotlinx.coroutines.launch

class RegisterViewModel : BaseViewModel<BaseModel>() {

    private var codeResult = SingleLiveData<CommonResult>()
    private var registerResult = SingleLiveData<CommonResult>()
    val mCodeResult: SingleLiveData<CommonResult>
        get() = codeResult
    val mRegisterResult : SingleLiveData<CommonResult>
        get() = registerResult

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    fun getVerifyCode(userInfo: String) {
        viewModelScope.launch {
            val response = RemoteDataSource.getVerificationCode(
                FindType.EMAIL_ADDRESS,
                userInfo,
                VerifyWay.REGIST,
                GApplication.app.userType,
                null,
                null
            )
            Log.e(TAG, "getVerifyCode: ${response}")
            codeResult.postValue(response)
        }
    }

    fun register(userInfo: String, psw: String, code: String, way: Int) {
        viewModelScope.launch {
            val response = RemoteDataSource.register(
                GApplication.app.userType,
                way,
                userInfo,
                psw,
                userInfo,
                userInfo,
                code,
                "+86"
            )
            Log.e(TAG, "register: ${response}")
            registerResult.postValue(response)
        }
    }
}