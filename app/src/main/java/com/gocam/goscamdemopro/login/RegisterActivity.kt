package com.gocam.goscamdemopro.login

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityRegisterBinding

class RegisterActivity : BaseActivity<ActivityRegisterBinding,RegisterViewModel>(){
    override fun getLayoutId(): Int {
        return R.layout.activity_register
    }

    override fun onCreateData(bundle: Bundle?) {

        mBinding?.apply {

            btnCode.setOnClickListener {
                mViewModel.getVerifyCode(etUserName.text.toString())
            }

            btnRegister.setOnClickListener {
                mViewModel.register(etUserName.text.toString(),etPassword.text.toString(),etVerificationCode.text.toString())
            }
        }
    }
}