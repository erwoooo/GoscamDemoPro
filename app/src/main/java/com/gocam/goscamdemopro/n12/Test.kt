package com.gocam.goscamdemopro.n12

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.createCoroutine

/**
 * @Author cw
 * @Date 2024/1/5 16:53
 */
class Test {

    val TAG = "Test"
    val coroutine = suspend {
        0
    }.createCoroutine(object :Continuation<Int>{
        override val context: CoroutineContext
            get() =
                EmptyCoroutineContext

        override fun resumeWith(result: Result<Int>) {
            Log.e(TAG, "resumeWith: 协程返回值为result = $result")
        }

    })
}