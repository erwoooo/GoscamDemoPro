package com.gocam.goscamdemopro.base

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

import androidx.lifecycle.ViewModel

abstract class BaseViewModel<M : BaseModel> : ViewModel(), DefaultLifecycleObserver {
    var owner: LifecycleOwner? = null
    lateinit var mModel: M
    open var TAG = this::class.java.simpleName
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        this.owner = owner
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        onCleared()
    }

    override fun onCleared() {
        super.onCleared()
        if (this::mModel.isInitialized) {
            mModel.onCleared()
        }
    }

}