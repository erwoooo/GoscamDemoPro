package com.gocam.goscamdemopro.peripheral

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.gocam.goscamdemopro.MainActivity
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.databinding.ActivityAddPeripheralResultBinding

/**
 *
 * @Author wuzb
 * @Date 2025/08/28 15:25
 */class AddPeripheralResultActivity : BaseBindActivity<ActivityAddPeripheralResultBinding>() {
     private val mHandler: Handler = Handler(Looper.getMainLooper())

    override fun getLayoutId(): Int = R.layout.activity_add_peripheral_result

    override fun onCreateData(bundle: Bundle?) {
        val code = intent.getIntExtra("PAY_RESULT", -100)
        mBinding?.apply {
            if (code == 0) {
                ivIcon.isSelected = true
                tvTitle.setText(R.string.face_add_success)
                tvToast.visibility = View.INVISIBLE
            } else {
                ivIcon.isSelected = false
                tvTitle.setText(R.string.dev_add_fail)
                tvToast.visibility = View.VISIBLE
                tvToast.setText(R.string.string_add_peripherals_failed)
            }
        }

        mHandler.postDelayed({
            Intent(this@AddPeripheralResultActivity, MainActivity::class.java).also {
                startActivity(it)
            }
        }, 3000)
    }
}