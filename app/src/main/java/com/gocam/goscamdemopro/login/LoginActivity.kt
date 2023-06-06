package com.gocam.goscamdemopro.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.gocam.goscamdemopro.MainActivity
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.R

import com.gocam.goscamdemopro.databinding.ActivityLoginBinding
import com.gocam.goscamdemopro.utils.Util


class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun onCreateData(bundle: Bundle?) {

        mBinding?.apply {

            tvForgetPsw.setOnClickListener {
                val intent = Intent(this@LoginActivity, ForgetPswActivity::class.java)
                startActivity(intent)
            }

            tvSignUp.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            btnLogin.setOnClickListener {
                val uuid = Util.getPhoneUUID(this@LoginActivity)
                mViewModel.login(etUserName.text.toString(),etPassword.text.toString(),uuid)
            }

        }


        mViewModel?.apply {
            mLoginResult.observe(this@LoginActivity){
                Log.e(TAG, "onCreateData: ${it}" )
                val intent = Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
            }
        }
    }


}