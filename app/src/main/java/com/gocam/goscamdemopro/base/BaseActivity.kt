package com.gocam.goscamdemopro.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.gocam.goscamdemopro.dialog.LoadingDialog
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VB : ViewDataBinding,VM : BaseViewModel<BaseModel>> : BaseBindActivity<VB>() {

    private lateinit var loadingDialog: LoadingDialog

    protected lateinit var mViewModel : VM


    override fun onCreate(savedInstanceState: Bundle?) {
        initViewModel()
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        loadingDialog = LoadingDialog(this)

    }

    private fun initViewModel() {
        var vmClass : Class<VM>
        val type = javaClass.genericSuperclass
        vmClass = if (type is ParameterizedType){
            type.actualTypeArguments[1] as Class<VM>
        }else{
            BaseViewModel::class.java as Class<VM>
        }

        mViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[vmClass]
        lifecycle.addObserver(mViewModel)

    }


    fun showLoading() {
        loadingDialog.showLoading()
    }

    fun dismissLoading() {
        loadingDialog.dismissLoading()
    }
}