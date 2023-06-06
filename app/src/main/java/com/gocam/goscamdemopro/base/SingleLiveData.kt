package com.gocam.goscamdemopro.base

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveData<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.e("TAG", "observe: has observer")
        }
        super.observe(owner) {
            if (mPending.compareAndSet(true, false))
                observer.onChanged(it)
        }
    }

    override fun setValue(value: T) {
        mPending.set(true)
        super.setValue(value)
    }
}