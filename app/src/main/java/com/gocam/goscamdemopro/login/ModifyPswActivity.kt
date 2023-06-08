package com.gocam.goscamdemopro.login

import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityModifyPswBinding

class ModifyPswActivity : BaseActivity<ActivityModifyPswBinding,ModifyPswViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_modify_psw
    }

    override fun onCreateData(bundle: Bundle?) {
        mBinding?.apply {
            toolBar.backImg.setOnClickListener {
                finish()
            }
        }
    }
}