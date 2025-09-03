package com.gocam.goscamdemopro.baby

import android.os.Bundle
import android.text.TextUtils
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityLifeReportBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.utils.CalendarAdjust
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gos.platform.api.contact.ResultCode
import com.gos.platform.device.inter.OnDevEventCallback
import com.gos.platform.device.result.DevResult
import com.gos.platform.device.result.DevResult.DevCmd
import com.gos.platform.device.result.GetBabyStatusResult

/**
 *
 * @Author wuzb
 * @Date 2025/09/02 14:39
 */
class LifeReportActivity : BaseActivity<ActivityLifeReportBinding, LifeReportViewModel>(), OnDevEventCallback {
    private lateinit var mDevice: Device

    private var todayTime: Long = 0

    private var mStartTime = 0 // 获取睡眠信息开始时间

    private var mEndTime = 0 // 获取睡眠信息结束时间

    override fun getLayoutId(): Int = R.layout.activity_life_report

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")?: return
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        if (mDevice == null)
            finish()
        mViewModel.apply {
            mSleepResult.observe(this@LifeReportActivity) {
                mBinding?.tvAllSleep?.text = it.TotalSleepTime.toString()
                mBinding?.tvMaxSleep?.text = it.MaxSleepTime.toString()
                mBinding?.tvCry?.text = it.CryingCount.toString()
                mBinding?.tvActivity?.text = it.ActivityCount.toString()
            }

            mTotalTimeResult.observe(this@LifeReportActivity) {
                it.TotalSleepTimeList?.let {
                    var text = ""
                    for (dayTime in it) {
                        text = text + "-" +dayTime.totalTime
                    }
                    mBinding?.tvSleepRecord?.text = text
                }
            }
        }
        mDevice.connection.addOnEventCallbackListener(this)
        mDevice.connection.getBabyStatus(0)
        getSleepData()
    }

    private fun getSleepData() {
        todayTime = System.currentTimeMillis()
        val totalStartTime =
            (CalendarAdjust.getDailyStartTime(todayTime) / 1000) - (6 * 86400)
        mEndTime = (CalendarAdjust.getDailyEndTime(todayTime) / 1000).toInt()
        mStartTime = (CalendarAdjust.getDailyStartTime(todayTime) / 1000).toInt()
        mViewModel.getSleepInfo(mDevice.devId, mStartTime, mEndTime, 0)
        mViewModel.getTotalSleepTime(mDevice.devId, totalStartTime.toInt(), mEndTime)
    }

    override fun onDevEvent(devId: String?, devResult: DevResult) {
        if (!TextUtils.equals(devId, mDevice.devId)) {
            return
        }

        val devCmd: DevCmd = devResult.devCmd
        val code: Int = devResult.getResponseCode()
        when (devCmd) {
            DevCmd.getBabyStatus -> if (ResultCode.SUCCESS == code) {
                val statusResult = devResult as GetBabyStatusResult
                mBinding?.apply {
                    if (statusResult.babyStatus == 0) {
                        tvStatus.setText(R.string.string_no_baby)
                    } else if (statusResult.babyStatus == 1) {
                        tvStatus.setText(R.string.string_baby_eventing)
                    } else {
                        tvStatus.setText(R.string.string_baby_sleeping)
                    }
                }
            }
        }
    }
}