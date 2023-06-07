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
import com.gos.platform.api.contact.VerifyWay
import kotlinx.coroutines.launch

class ForgetPswViewModel : BaseViewModel<BaseModel>() {

    private var codeResult = SingleLiveData<CommonResult>()
    private var modePswResult = SingleLiveData<CommonResult>()
    val mCodeResult: SingleLiveData<CommonResult>
        get() = codeResult
    val mModePswResult :SingleLiveData<CommonResult>
    get() = modePswResult

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }


    fun getVerificationCode(username: String) {
        viewModelScope.launch {
            val response = RemoteDataSource.getVerificationCode(
                FindType.EMAIL_ADDRESS,
                username,
                VerifyWay.FORGET_PSW,
                GApplication.app.userType,
                null,
                null
            )
            Log.e(TAG, "getVerificationCode: $response")

            codeResult.postValue(response)
        }
    }

    fun modifyPsw(username: String,code:String,newPsw:String){
        viewModelScope.launch {
            val response = RemoteDataSource.modifyUserPassword(username,code,newPsw,true)
            Log.e(TAG, "modifyPsw: $response" )
            modePswResult.postValue(response)
        }

    }
}