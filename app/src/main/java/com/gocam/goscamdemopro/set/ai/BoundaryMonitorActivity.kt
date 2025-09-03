package com.gocam.goscamdemopro.set.ai

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.aigestudio.wheelpicker.WheelPicker
import com.aigestudio.wheelpicker.WheelPicker.OnWheelChangeListener
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.StringSignature
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityBoundaryMonitorBinding
import com.gocam.goscamdemopro.entity.BoundarySettingParam
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.utils.DateUtils
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gocam.goscamdemopro.utils.FileUtils
import com.gocam.goscamdemopro.view.PointInPathView.BOUNDARY_DETECT
import com.google.gson.Gson
import com.gos.platform.api.devparam.BoundarySettingParamElement
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.BoundaryParam
import kotlinx.coroutines.launch
import java.io.File

/**
 *
 * @Author wuzb
 * @Date 2025/09/01 16:40
 */
class BoundaryMonitorActivity : BaseBindActivity<ActivityBoundaryMonitorBinding>() {
    private lateinit var mDevice: Device
    private lateinit var mDialog: Dialog
    private lateinit var mDetectionDialog: Dialog
    private lateinit var mBoundaryParam: BoundarySettingParam

    private val hhList: ArrayList<String> by lazy {
        val list: ArrayList<String> = ArrayList<String>()
        for (i in 0..23) {
            if (i < 10) {
                list.add("0$i")
            } else {
                list.add(i.toString() + "")
            }
        }
        list
    }
    private val mmList: ArrayList<String>  by lazy {
        val list: ArrayList<String> = ArrayList<String>()
        for (i in 0..59) {
            if (i < 10) {
                list.add("0$i")
            } else {
                list.add(i.toString() + "")
            }
        }
        list
    }

    override fun getLayoutId(): Int = R.layout.activity_boundary_monitor

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")?: return
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        if (mDevice == null)
            finish()
        mBinding?.apply {
            pipv.setType(BOUNDARY_DETECT)
            val gesture = getResources().getColor(R.color._3CCF9B)
            val finger = getResources().getColor(R.color.app_color)
            val selected = getResources().getColor(R.color.app_color)
            pipv.setPaintColor(gesture, finger, selected)
            val userName: String? = GApplication.app.user.userName
            val path: String = FileUtils.getDeviceSanpshot(userName, mDevice.devId)
            val file = File(path)
            val signature = if (!file.exists()) "" else file.lastModified().toString() + ""
            Glide.with(this@BoundaryMonitorActivity).load(path)
                .placeholder(R.mipmap.ic_fg_device_item)
                .signature(StringSignature(signature))
                .error(R.mipmap.ic_fg_device_item)
                .into(ivImg)
            ivImg.scaleType = ImageView.ScaleType.FIT_XY
            itemSensitivity.setOnClickListener {
                if (mDialog.isShowing) {
                    mDialog.dismiss()
                } else {
                    setItemColor(mBoundaryParam.un_sensitivity)
                    mDialog.show()
                }
            }
            llDetectionTime.setOnClickListener {
                if (mDetectionDialog.isShowing) {
                    mDetectionDialog.dismiss()
                } else {
                    mDetectionDialog.show()
                }
            }
            btnSave.setOnClickListener {
                save()
            }
            sivMoveSwitch.setOnCheckedChangeListener { v, isChecked ->
                mBoundaryParam.un_switch = if (isChecked) 1 else 0
                saveCap()
            }
            itemBoundarySwitch.setOnCheckedChangeListener { v, isChecked ->
                mBoundaryParam.show_polygon = if (isChecked) 1 else 0
                saveCap()
            }
            btnSelectAll.setOnClickListener {
                pipv.clear()
            }
        }
        initDialog()
        initComfortDialog()
        getCap()
    }

    private fun getCap() {
        lifecycleScope.launch {
            val result = RemoteDataSource.getDeviceParam(BoundaryParam, deviceId = mDevice.devId)
            result.let {
                for (param in it) {
                    if (param.CMDType == BoundaryParam) {
                        val boundaryParam = Gson().fromJson(param.DeviceParam, BoundarySettingParam::class.java)
                        initView(boundaryParam)
                    }
                }
            }
        }
    }

    private fun initView(boundaryParam: BoundarySettingParam) {
        mBoundaryParam = boundaryParam
        mBinding?.apply {
            sivMoveSwitch.isChecked = mBoundaryParam.un_switch == 1
            itemBoundarySwitch.isChecked = mBoundaryParam.show_polygon == 1
            itemSensitivity.setRightText(getSensitivity(mBoundaryParam.un_sensitivity))
            itemDetectionTime.setRightText(
                DateUtils.getTimeHHMM(mBoundaryParam.starttime) + "-" + DateUtils.getTimeHHMM(mBoundaryParam.endtime)
            )
            pipv.initPath(mDevice.devId, mBoundaryParam)
        }
    }

    private fun getSensitivity(type: Int): String {
        if (type == 1) {
            return getResources().getString(R.string.string_open_height)
        } else if (type == 2) {
            return getResources().getString(R.string.string_open_mid)
        } else if (type == 3) {
            return getResources().getString(R.string.string_open_low)
        } else {
            return getResources().getString(R.string.string_support_off)
        }
    }

    private fun setItemColor(type: Int) {
        mHeightImg.visibility = View.INVISIBLE
        mMidImg.visibility = View.INVISIBLE
        mLowImg.visibility = View.INVISIBLE
        mHeightTv.setTextColor(getResources().getColor(R.color.text2))
        mMidTv.setTextColor(getResources().getColor(R.color.text2))
        mLowTv.setTextColor(getResources().getColor(R.color.text2))
        mBoundaryParam.un_sensitivity = type
        if (type == 1) {
            mHeightImg.visibility = View.VISIBLE
            mHeightTv.setTextColor(getResources().getColor(R.color._3CCF9B))
        } else if (type == 2) {
            mMidImg.visibility = View.VISIBLE
            mMidTv.setTextColor(getResources().getColor(R.color._3CCF9B))
        } else if (type == 3) {
            mLowImg.visibility = View.VISIBLE
            mLowTv.setTextColor(getResources().getColor(R.color._3CCF9B))
        }
    }

    private lateinit var mName: TextView
    private lateinit var mHeightTv: TextView
    private lateinit var mMidTv: TextView
    private lateinit var mLowTv: TextView
    private lateinit var mHeightImg: ImageView
    private lateinit var mMidImg: ImageView
    private lateinit var mLowImg: ImageView
    private fun initDialog() {
        mDialog = Dialog(this, R.style.DialogTheme)
        val view = View.inflate(this, R.layout.layout_setting_sleep_monitor, null)
        mDialog.setContentView(view)
        val window: Window? = mDialog.getWindow()
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.anim_menu_bottombar)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mName = view.findViewById<TextView>(R.id.item_name)
        mHeightTv = view.findViewById<TextView>(R.id.tv_height_time)
        val mHeightTitle = view.findViewById<TextView>(R.id.tv_height_time2)
        mMidTv = view.findViewById<TextView>(R.id.tv_mid_time)
        val mMidTitle = view.findViewById<TextView>(R.id.tv_mid_time2)
        mLowTv = view.findViewById<TextView>(R.id.tv_low_time)
        val mLowTitle = view.findViewById<TextView>(R.id.tv_low_time2)
        mHeightImg = view.findViewById<ImageView>(R.id.iv_height_time)
        mMidImg = view.findViewById<ImageView>(R.id.iv_mid_time)
        mLowImg = view.findViewById<ImageView>(R.id.iv_low_time)
        mName.setText(R.string.setting_sensitivity)
        mHeightTitle.setText(R.string.string_electronic_fence_height)
        mMidTitle.setText(R.string.string_electronic_fence_mid)
        mLowTitle.setText(R.string.string_electronic_fence_low)
        val mNotOpen = view.findViewById<LinearLayout>(R.id.ll_not_open)
        mNotOpen.visibility = View.GONE
        val mHeight = view.findViewById<LinearLayout>(R.id.ll_height)
        mHeight.setOnClickListener { setItemColor(1) }
        val mMid = view.findViewById<LinearLayout>(R.id.ll_below_time)
        mMid.setOnClickListener { setItemColor(2) }
        val mLow = view.findViewById<LinearLayout>(R.id.ll_low_time)
        mLow.setOnClickListener { setItemColor(3) }
        val mTvCancel = view.findViewById<TextView>(R.id.tv_cancel)
        mTvCancel.setOnClickListener { mDialog.dismiss() }
        val mTvSure = view.findViewById<TextView>(R.id.tv_sure)
        mTvSure.setOnClickListener {
            mBinding?.itemSensitivity?.setRightText(getSensitivity(mBoundaryParam.un_sensitivity))
            saveCap()
            mDialog.dismiss()
        }
    }

    private var startTempStartTime: Int = 0
    private var endTempEndTime: Int = 0
    private lateinit var mWpStartHH: WheelPicker
    private lateinit var mWpStartMM: WheelPicker
    private lateinit var mWpEndHH: WheelPicker
    private lateinit var mWpEndMM: WheelPicker
    private fun initComfortDialog() {
        mDetectionDialog = Dialog(this, R.style.DialogTheme)
        val popRange = View.inflate(this, R.layout.layout_pop_camera_plan, null)
        mDetectionDialog.setContentView(popRange)
        val window: Window? = mDetectionDialog.window
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.anim_menu_bottombar)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mWpStartHH = popRange.findViewById<WheelPicker>(R.id.wp_start_hh)
        mWpStartMM = popRange.findViewById<WheelPicker>(R.id.wp_start_mm)
        mWpEndHH = popRange.findViewById<WheelPicker>(R.id.wp_end_hh)
        mWpEndMM = popRange.findViewById<WheelPicker>(R.id.wp_end_mm)
        val mLlDay = popRange.findViewById<LinearLayout>(R.id.rl_day)
        val mLlRepeat = popRange.findViewById<LinearLayout>(R.id.rl_repeat)
        mLlDay.visibility = View.GONE
        mLlRepeat.visibility = View.GONE
        val mTvCancel = popRange.findViewById<TextView>(R.id.tv_cancel)
        val mTvSure = popRange.findViewById<TextView>(R.id.tv_sure)
        mTvCancel.setOnClickListener { mDetectionDialog.dismiss() }
        mTvSure.setOnClickListener {
            try {
                updateHHMM()
                mBoundaryParam.starttime = startTempStartTime
                mBoundaryParam.endtime = endTempEndTime
                mBinding?.itemDetectionTime?.setRightText(
                    DateUtils.getTimeHHMM(startTempStartTime) + "—" + DateUtils.getTimeHHMM(
                        endTempEndTime
                    )
                )
                mDetectionDialog.dismiss()
                saveCap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mWpStartHH.setData(hhList)
        mWpStartMM.setData(mmList)
        mWpEndHH.setData(hhList)
        mWpEndMM.setData(mmList)

        mWpStartHH.setOnWheelChangeListener(onWheelChangeListener)
        mWpStartMM.setOnWheelChangeListener(onWheelChangeListener)
        mWpEndHH.setOnWheelChangeListener(onWheelChangeListener)
        mWpEndMM.setOnWheelChangeListener(onWheelChangeListener)


        val timeRangeWindow = PopupWindow(
            popRange,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        timeRangeWindow.isTouchable = true
        timeRangeWindow.isFocusable = true
        timeRangeWindow.isOutsideTouchable = true
        timeRangeWindow.setBackgroundDrawable(BitmapDrawable(getResources(), null as Bitmap?))
        timeRangeWindow.animationStyle = R.style.anim_menu_bottombar

        timeRangeWindow.setOnDismissListener {
            val lp1 = getWindow().attributes
            lp1.alpha = 1f
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            getWindow().attributes = lp1
        }

        mDetectionDialog.setOnDismissListener {
            val lp1 = getWindow().attributes
            lp1.alpha = 1f
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            getWindow().attributes = lp1
        }
    }

    var onWheelChangeListener: OnWheelChangeListener = object : OnWheelChangeListener {
        override fun onWheelScrolled(offset: Int) {
        }

        override fun onWheelSelected(position: Int) {
            updateHHMM()
        }

        override fun onWheelScrollStateChanged(state: Int) {
        }
    }

    private fun updateHHMM() {
        val sh = hhList.get(mWpStartHH.currentItemPosition)
        val sm = mmList.get(mWpStartMM.currentItemPosition)
        startTempStartTime = sh.toInt() * 3600 + sm.toInt() * 60

        val eh = hhList.get(mWpEndHH.currentItemPosition)
        val em = mmList.get(mWpEndMM.currentItemPosition)
        endTempEndTime = eh.toInt() * 3600 + em.toInt() * 60
    }

    private fun save() {
        mBinding?.apply {
            val humanPoint: MutableList<BoundarySettingParamElement.Perms> =
                pipv.getBoundaryPoint()
            mBoundaryParam.permcnt = humanPoint.size
            mBoundaryParam.perms = humanPoint
            saveCap()
        }
    }

    private fun saveCap() {
        val devParamArray = DevParamArray(
            BoundaryParam,
            mBoundaryParam
        )
        lifecycleScope.launch {
            val result = RemoteDataSource.setDeviceParam(devParamArray, deviceId = mDevice.devId)
            initView(mBoundaryParam)
        }
    }
}