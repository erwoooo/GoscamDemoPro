package com.gocam.goscamdemopro.login

import android.content.Context
import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityForgetPswBinding
import com.gocam.goscamdemopro.utils.CountDownTimerUtil
import com.gocam.goscamdemopro.utils.RegexUtils

class ForgetPswActivity : BaseActivity<ActivityForgetPswBinding, ForgetPswViewModel>(),
    CountDownTimerUtil.CountDownTimerListener {
    override fun getLayoutId(): Int {
        return R.layout.activity_forget_psw
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
                        mViewModel.getVerificationCode(userName)
                    }
                }

            }

            btnModify.setOnClickListener {
                val userName = etUserName.text.toString()
                val firstPsw = etPassword.text.toString()
                val secondPsw = etPasswordAgain.text.toString()
                val code = etVerificationCode.text.toString()
                if (RegexUtils.isMatch(RegexUtils.REGEX_EMAIL, userName)) {
                    if (userName.length < 4 || userName.length > 81) {
                        showToast(getString(R.string.username_format_error_tip))
                    } else if (checkPasswordPassed(firstPsw, secondPsw)) {
                        showLoading()
                        mViewModel.modifyPsw(userName, code, firstPsw)
                    }
                }

            }


        }

        mViewModel?.apply {
            mCodeResult.observe(this@ForgetPswActivity) {
                dismissLoading()
                mBinding?.btnCode?.isEnabled = false
                CountDownTimerUtil.startCountDownTimer(60000, 1000, this@ForgetPswActivity)
            }

            mModePswResult.observe(this@ForgetPswActivity){
                dismissLoading()
                showToast(resources.getString(R.string.modify_success))
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