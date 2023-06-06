package com.golway.uilib.utils

import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 协程工具类
 */

private const val KEY_DEFAULT = "default"
private var jobs: MutableMap<String, MutableList<Job>> =
    Collections.synchronizedMap(HashMap<String, MutableList<Job>>())

// 在主线程执行任务
fun mainTask(
    tag: String = KEY_DEFAULT,
    taskCancelable: Boolean = true,
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val job = GlobalScope.launch(Dispatchers.Main) {
        try {
            block()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    if (taskCancelable) {
        addItemByTag(tag, job)
    }
    return job
}

fun mainTask(
    tag: String = KEY_DEFAULT,
    taskCancelable: Boolean = true,
    block: suspend CoroutineScope.() -> Unit,
    finallyBlock: suspend CoroutineScope.() -> Unit = {}
): Job {
    val job = GlobalScope.launch(Dispatchers.Main) {
        try {
            block()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            finallyBlock()
        }
    }
    if (taskCancelable) {
        addItemByTag(tag, job)
    }
    return job
}

// 执行异步任务
suspend fun <T> asyncTask(
    tag: String = KEY_DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T?> {
    val job = GlobalScope.async(Dispatchers.IO) {
        try {
            block()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    addItemByTag(tag, job)
    return job
}



private fun addItemByTag(tag: String, job: Job) {
    var list = jobs[tag]
    if (list == null) {
        list = ArrayList()
        jobs[tag] = list
    }
    list.add(job)
}

