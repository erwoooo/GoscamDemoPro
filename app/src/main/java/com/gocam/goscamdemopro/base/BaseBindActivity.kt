package com.gocam.goscamdemopro.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gocam.goscamdemopro.dialog.LoadingDialog

abstract class BaseBindActivity<VB : ViewDataBinding> : AppCompatActivity() {


    var mBinding: VB? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        onCreateData(savedInstanceState)
    }

    abstract fun getLayoutId(): Int


    abstract fun onCreateData(bundle: Bundle?)

    override fun onDestroy() {
        super.onDestroy()
        mBinding?.unbind()
    }


}