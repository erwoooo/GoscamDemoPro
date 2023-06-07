package com.gocam.goscamdemopro.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.gocam.goscamdemopro.GApplication

import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VB : ViewDataBinding, VM : BaseViewModel<BaseModel>> : BaseBindFragment<VB>() {
    protected lateinit var mViewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun initViewModel() {
        var vmClass : Class<VM>
        val type = javaClass.genericSuperclass
        vmClass = if (type is ParameterizedType){
            type.actualTypeArguments[1] as Class<VM>
        }else{
            BaseViewModel::class.java as Class<VM>
        }
        mViewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory(GApplication.app))[vmClass]
        lifecycle.addObserver(mViewModel)
    }
}