package com.gocam.goscamdemopro.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment


abstract class BaseBindFragment <VB : ViewDataBinding> : Fragment() {

    protected lateinit var mBinding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater,getLayoutId(), container,false)
        mBinding.lifecycleOwner = this
        mBinding.executePendingBindings()
        initFragmentView()
        return mBinding.root
    }

    abstract fun initFragmentView()

    abstract fun getLayoutId(): Int
}