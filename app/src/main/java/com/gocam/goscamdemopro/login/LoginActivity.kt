package com.gocam.goscamdemopro.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.gocam.goscamdemopro.MainActivity
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.R

import com.gocam.goscamdemopro.databinding.ActivityLoginBinding
import com.gocam.goscamdemopro.utils.PermissionUtil
import com.gocam.goscamdemopro.utils.RegexUtils
import com.gocam.goscamdemopro.utils.Util


class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun onCreateData(bundle: Bundle?) {
        PermissionUtil.verifyAppPermissions(this)
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
                val userName = etUserName.text.toString()
                val psw = etPassword.text.toString()
                if (RegexUtils.isMatch(RegexUtils.REGEX_EMAIL, userName)) {
                    if (userName.length < 4 || userName.length > 81) {
                        showToast(getString(R.string.username_format_error_tip))
                    } else if (checkPasswordPassed(psw)) {
                        showLoading()
                        mViewModel.login(etUserName.text.toString(), etPassword.text.toString())
                    }

                }

            }


            mViewModel?.apply {
                mLoginResult.observe(this@LoginActivity) {
                    dismissLoading()
                    Log.e(TAG, "onCreateData: ${it}")
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }

    }
    private fun checkPasswordPassed(pwd: String): Boolean {
        if (!(!RegexUtils.isMatch(
                RegexUtils.REGEX_SINGLE_TYPE_CHARACTER,
                pwd
            ) && RegexUtils.isMatch(RegexUtils.REGEX_8_16_LETTER_NUMBER, pwd))
        ) {
            showToast(getString(R.string.password_format_error_tip))
            return false
        }
        return true
    }
}