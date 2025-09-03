package com.gocam.goscamdemopro.baby

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gocam.goscamdemopro.base.BaseModel
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.cloud.data.GosCloud
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gos.platform.api.inter.OnPlatformEventCallback
import com.gos.platform.api.result.GetSleepInfoResult.SleepTime
import com.gos.platform.api.result.PlatResult
import kotlinx.coroutines.launch

/**
 *
 * @Author wuzb
 * @Date 2025/09/02 15:39
 */
class LifeReportViewModel : BaseViewModel<BaseModel>(), OnPlatformEventCallback {
    private val mSleepTimeList: MutableList<SleepTime> by lazy { ArrayList<SleepTime>() } // 睡眠时间列表
    private val mActivityTimeList: MutableList<SleepTime> by lazy { ArrayList<SleepTime>() } // 活动时间列表
    private val mCryingTimeList: MutableList<SleepTime> by lazy { ArrayList<SleepTime>() } // 哭闹时间列表
    private val mInfoResult: GetSleepInfoResult by lazy { GetSleepInfoResult() }
    private val sleepResult = MutableLiveData<GetSleepInfoResult>()
    val mSleepResult: MutableLiveData<GetSleepInfoResult>
        get() = sleepResult

    private val totalTimeResult = MutableLiveData<GetTotalTimeResult>()
    val mTotalTimeResult: MutableLiveData<GetTotalTimeResult>
        get() = totalTimeResult

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        GosCloud.getCloud().addOnPlatformEventCallback(this)
    }

    override fun OnPlatformEvent(result: PlatResult?) {
        TODO("Not yet implemented")
    }

    fun getSleepInfo(deviceId: String, utcTimeBegin: Int, utcTimeEnd: Int, record: Int) {
        viewModelScope.launch {
            val result = RemoteDataSource.getSleepInfo(deviceId, utcTimeBegin, utcTimeEnd, record)
            result?.let {
                var count = 0
                var recordId = 0
                mInfoResult.TotalSleepTime += it.TotalSleepTime
                if (mInfoResult.MaxSleepTime < it.MaxSleepTime) {
                    mInfoResult.MaxSleepTime = it.MaxSleepTime
                }
                mInfoResult.DeepSleepTime += it.DeepSleepTime
                mInfoResult.LightSleepTime += it.LightSleepTime
                mInfoResult.CryingCount += it.CryingCount
                mInfoResult.ActivityCount += it.ActivityCount
                it.SleepTimeList?.let { it1 ->
                    if (it1.isNotEmpty()) {
                        count += it1.size
                        for (sleepTime in it1) {
                            recordId =
                                if (recordId != 0 && recordId < sleepTime.recordId) recordId else sleepTime.recordId
                            mSleepTimeList.add(sleepTime)
                        }
                    }
                }
                it.ActivityTimeList?.let {it2 ->
                    if (it2.isNotEmpty()) {
                        count += it2.size
                        for (sleepTime in it2) {
                            recordId =
                                if (recordId != 0 && recordId < sleepTime.recordId) recordId else sleepTime.recordId
                            mActivityTimeList.add(sleepTime)
                        }
                    }
                }
                it.CryingTimeList?.let {it3 ->
                    if (it3.isNotEmpty()) {
                        count += it3.size
                        for (sleepTime in it3) {
                            recordId =
                                if (recordId != 0 && recordId < sleepTime.recordId) recordId else sleepTime.recordId
                            mCryingTimeList.add(sleepTime)
                        }
                    }
                }
                if (count != 0) {
                    getSleepInfo(deviceId, utcTimeBegin, utcTimeEnd, recordId)
                } else {
                    mInfoResult.SleepTimeList = mSleepTimeList
                    mInfoResult.ActivityTimeList = mActivityTimeList
                    mInfoResult.CryingTimeList = mCryingTimeList
                    sleepResult.postValue(mInfoResult)
                }
            }
        }
    }

    fun getTotalSleepTime(deviceId: String, utcTimeBegin: Int, utcTimeEnd: Int) {
        viewModelScope.launch {
            val result = RemoteDataSource.getTotalSleepTime(deviceId, utcTimeBegin, utcTimeEnd)
            result?.let {
                totalTimeResult.postValue(it)
            }
        }
    }
}