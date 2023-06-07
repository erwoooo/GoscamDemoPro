package com.gocam.goscamdemopro.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

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
    protected open fun showToast(msg: CharSequence?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}