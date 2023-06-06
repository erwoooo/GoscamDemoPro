package com.gocam.goscamdemopro.base

import androidx.lifecycle.LifecycleOwner

interface MyLifecycle {
    fun onCreate(owner: LifecycleOwner)
    fun onDestroy(owner: LifecycleOwner)
}