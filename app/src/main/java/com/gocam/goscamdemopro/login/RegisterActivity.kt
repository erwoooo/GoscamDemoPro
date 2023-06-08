package com.gocam.goscamdemopro.login

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityRegisterBinding
import com.gocam.goscamdemopro.utils.CountDownTimerUtil
import com.gocam.goscamdemopro.utils.RegexUtils

class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(),
    CountDownTimerUtil.CountDownTimerListener {
    override fun getLayoutId(): Int {
        return R.layout.activity_register
    }

    override fun onCreateData(bundle: Bundle?) {

        mBinding?.apply {
            toolBar.backImg.setOnClickListener {
                finish()
            }
            btnCode.setOnClickListener {
                val userName = etUserName.text.toString()
                if (RegexUtils.isMatch(RegexUtils.REGEX_EMAIL, userName)) {
                    if (userName.length < 4 || userName.length > 81) {
                        showToast(getString(R.string.username_format_error_tip))
                    } else {
                        showLoading()
                        mViewModel.getVerifyCode(userName)
                    }
                }
            }

            btnRegister.setOnClickListener {
                val userName = etUserName.text.toString()
                val firstPsw = etPassword.text.toString()
                val secondPsw = etPasswordAgain.text.toString()
                val code = etVerificationCode.text.toString()
                if (RegexUtils.isMatch(RegexUtils.REGEX_EMAIL, userName)) {
                    if (userName.length < 4 || userName.length > 81) {
                        showToast(getString(R.string.username_format_error_tip))
                    } else if (checkPasswordPassed(firstPsw, secondPsw)) {
                        showLoading()
                        mViewModel.register(userName, firstPsw, code)
                    }
                }
            }
        }

        mViewModel?.apply {
            mCodeResult.observe(this@RegisterActivity) {
                dismissLoading()
                mBinding?.btnCode?.isEnabled = false
                CountDownTimerUtil.startCountDownTimer(60000, 1000, this@RegisterActivity)
            }

            mRegisterResult.observe(this@RegisterActivity) {
                dismissLoading()
                showToast(resources.getString(R.string.register_successfully))
                finish()
            }
        }
    }

    override fun onTick(millisUntilFinished: Long) {
        ((millisUntilFinished / 1000).toString() + "s").also { mBinding?.btnCode?.text = it }
    }

    override fun onFinish() {
        mBinding?.btnCode?.apply {
            isEnabled = true
            text = resources.getString(R.string.get_verification_code)
        }
    }

    private fun checkPasswordPassed(firstPwd: String, secondPwd: String): Boolean {
        if (firstPwd != secondPwd) {
            showToast(getString(R.string.sign_up_input_password_again_error_tip))
            return false
        } else if (!(!RegexUtils.isMatch(
                RegexUtils.REGEX_SINGLE_TYPE_CHARACTER,
                firstPwd
            ) && RegexUtils.isMatch(RegexUtils.REGEX_8_16_LETTER_NUMBER, firstPwd))
        ) {
            showToast(getString(R.string.password_format_error_tip))
            return false
        }
        return true
    }
}