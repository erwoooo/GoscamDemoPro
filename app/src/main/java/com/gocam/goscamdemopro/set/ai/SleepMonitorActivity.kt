package com.gocam.goscamdemopro.set.ai

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivitySleepMonitorBinding
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.entity.SleepDetectionParam
import com.gocam.goscamdemopro.utils.DeviceManager
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.SleepParam
import kotlinx.coroutines.launch

/**
 *
 * @Author wuzb
 * @Date 2025/09/01 15:26
 */
class SleepMonitorActivity : BaseBindActivity<ActivitySleepMonitorBinding>() {
    private lateinit var mDevice: Device
    private lateinit var mSleepParam: SleepDetectionParam
    private lateinit var mDialog: Dialog
    private var mType = 0

    override fun getLayoutId(): Int = R.layout.activity_sleep_monitor

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")?: return
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        if (mDevice == null)
            finish()
        mBinding?.apply {
            itemShelter.setOnClickListener {
                if (mDialog.isShowing) {
                    mDialog.dismiss()
                } else {
                    mType = 1
                    setItemColor(mSleepParam.detect_masked_face)
                    mName.setText(R.string.string_shelter_monitor)
                    mHeightTitle.setText(R.string.string_shelter_monitor_height)
                    mMidTitle.setText(R.string.string_shelter_monitor_mid)
                    mLowTitle.setText(R.string.string_shelter_monitor_low)
                    mDialog.show()
                }
            }

            itemWakeUp.setOnClickListener {
                if (mDialog.isShowing) {
                    mDialog.dismiss()
                } else {
                    mType = 2
                    setItemColor(mSleepParam.detect_awaken)
                    mName.setText(R.string.string_wake_up)
                    mHeightTitle.setText(R.string.string_wake_up_height)
                    mMidTitle.setText(R.string.string_wake_up_mid)
                    mLowTitle.setText(R.string.string_wake_up_low)
                    mDialog.show()
                }
            }

            itemSleep.setOnCheckedChangeListener { v, isChecked ->
                mSleepParam.un_switch = if (isChecked) 1 else 0
                saveCap()
            }

            itemSleepQuality.setOnCheckedChangeListener { v, isChecked ->
                mSleepParam.sleep_quality = if (isChecked) 1 else 0
                saveCap()
            }
        }
        initDialog()
        getCap()
    }

    private fun getCap() {
        lifecycleScope.launch {
            val result = RemoteDataSource.getDeviceParam(SleepParam, deviceId = mDevice.devId)
            result.let {
                for (param in it) {
                    if (param.CMDType == SleepParam) {
                        val sleepParam = Gson().fromJson(param.DeviceParam, SleepDetectionParam::class.java)
                        initView(sleepParam)
                    }
                }
            }
        }
    }

    private fun initView(sleepParam: SleepDetectionParam) {
        mSleepParam = sleepParam

        mBinding?.apply {
            itemSleep.isChecked = mSleepParam.un_switch == 1
            itemSleepQuality.isChecked = mSleepParam.sleep_quality == 1
            itemShelter.setRightText(getSensitivity(mSleepParam.detect_masked_face))
            itemWakeUp.setRightText(getSensitivity(mSleepParam.detect_awaken))
        }
    }

    private fun getSensitivity(type: Int): String {
        return if (type == 1) {
            resources.getString(R.string.string_open_height)
        } else if (type == 2) {
            resources.getString(R.string.string_open_mid)
        } else if (type == 3) {
            resources.getString(R.string.string_open_low)
        } else {
            resources.getString(R.string.string_support_off)
        }
    }

    private lateinit var mName: TextView
    private lateinit var mNotOpenTv: TextView
    private lateinit var mHeightTv: TextView
    private lateinit var mHeightTitle: TextView
    private lateinit var mMidTv: TextView
    private lateinit var mMidTitle: TextView
    private lateinit var mLowTv: TextView
    private lateinit var mLowTitle: TextView
    private lateinit var mNotOpenImg: ImageView
    private lateinit var mHeightImg: ImageView
    private lateinit var mMidImg: ImageView
    private lateinit var mLowImg: ImageView
    private fun initDialog() {
        mDialog = Dialog(this, R.style.DialogTheme)
        val view = View.inflate(this, R.layout.layout_setting_sleep_monitor, null)
        mDialog.setContentView(view)
        val window = mDialog.window
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.anim_menu_bottombar)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mName = view.findViewById<TextView>(R.id.item_name)
        mNotOpenTv = view.findViewById<TextView>(R.id.tv_not_open)
        mHeightTv = view.findViewById<TextView>(R.id.tv_height_time)
        mHeightTitle = view.findViewById<TextView>(R.id.tv_height_time2)
        mMidTv = view.findViewById<TextView>(R.id.tv_mid_time)
        mMidTitle = view.findViewById<TextView>(R.id.tv_mid_time2)
        mLowTv = view.findViewById<TextView>(R.id.tv_low_time)
        mLowTitle = view.findViewById<TextView>(R.id.tv_low_time2)
        mNotOpenImg = view.findViewById<ImageView>(R.id.iv_not_open)
        mHeightImg = view.findViewById<ImageView>(R.id.iv_height_time)
        mMidImg = view.findViewById<ImageView>(R.id.iv_mid_time)
        mLowImg = view.findViewById<ImageView>(R.id.iv_low_time)
        val mNotOpen = view.findViewById<LinearLayout>(R.id.ll_not_open)
        mNotOpen.setOnClickListener {
            setItemColor(0)
        }
        val mHeight = view.findViewById<LinearLayout>(R.id.ll_height)
        mHeight.setOnClickListener {setItemColor(1)}
        val mMid = view.findViewById<LinearLayout>(R.id.ll_below_time)
        mMid.setOnClickListener {setItemColor(2)}
        val mLow = view.findViewById<LinearLayout>(R.id.ll_low_time)
        mLow.setOnClickListener {setItemColor(3)}
        val mTvCancel = view.findViewById<TextView>(R.id.tv_cancel)
        mTvCancel.setOnClickListener {mDialog.dismiss()}
        val mTvSure = view.findViewById<TextView>(R.id.tv_sure)
        mTvSure.setOnClickListener {
            saveCap()
            mDialog.dismiss()
        }
    }

    private fun setItemColor(type: Int) {
        mNotOpenImg.visibility = View.INVISIBLE
        mHeightImg.visibility = View.INVISIBLE
        mMidImg.visibility = View.INVISIBLE
        mLowImg.visibility = View.INVISIBLE
        mNotOpenTv.setTextColor(resources.getColor(R.color.text2))
        mHeightTv.setTextColor(resources.getColor(R.color.text2))
        mMidTv.setTextColor(resources.getColor(R.color.text2))
        mLowTv.setTextColor(resources.getColor(R.color.text2))
        if (mType == 1) {
            mSleepParam.detect_masked_face = type
        } else if (mType == 2) {
            mSleepParam.detect_awaken = type
        } else if (mType == 3) {
            mSleepParam.detect_motion = type
        }

        if (type == 1) {
            mHeightImg.visibility = View.VISIBLE
            mHeightTv.setTextColor(resources.getColor(R.color._3CCF9B))
        } else if (type == 2) {
            mMidImg.visibility = View.VISIBLE
            mMidTv.setTextColor(resources.getColor(R.color._3CCF9B))
        } else if (type == 3) {
            mLowImg.visibility = View.VISIBLE
            mLowTv.setTextColor(resources.getColor(R.color._3CCF9B))
        } else {
            mNotOpenImg.visibility = View.VISIBLE
            mNotOpenTv.setTextColor(resources.getColor(R.color._3CCF9B))
        }
    }

    private fun saveCap() {
        val devParamArray = DevParamArray(
            SleepParam,
            mSleepParam
        )
        lifecycleScope.launch {
            val result = RemoteDataSource.setDeviceParam(devParamArray, deviceId = mDevice.devId)
            initView(mSleepParam)
        }
    }
}