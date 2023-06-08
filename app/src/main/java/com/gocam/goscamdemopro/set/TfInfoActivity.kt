package com.gocam.goscamdemopro.set

import android.os.Bundle
import android.util.Log
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityTfInfoBinding
import com.gocam.goscamdemopro.utils.Util

class TfInfoActivity : BaseActivity<ActivityTfInfoBinding, TfInfoViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_tf_info
    }

    override fun onCreateData(bundle: Bundle?) {
        val deviceId = intent.getStringExtra("dev") as String
        mViewModel?.getTfInfo(deviceId)
        mBinding?.toolBar?.backImg?.setOnClickListener {
            finish()
        }
        mBinding?.btnFormat?.setOnClickListener {
            mViewModel?.formatSd(deviceId)
        }

        mViewModel?.apply {
            mTfInfoParam.observe(this@TfInfoActivity) {
                Log.e(TAG, "onCreateData: $it")
                mBinding?.tvUsedSpace?.text = Util.getStorage(it.a_used_size.toLong())
                mBinding?.tvTotalSpace?.text = Util.getStorage(it.a_total_size.toLong())
                mBinding?.tvUnusedSpace?.text = Util.getStorage(it.a_free_size.toLong())

            }
        }
    }
}