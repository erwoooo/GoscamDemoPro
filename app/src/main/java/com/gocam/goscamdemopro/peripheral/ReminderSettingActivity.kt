package com.gocam.goscamdemopro.peripheral

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aigestudio.wheelpicker.WheelPicker
import com.aigestudio.wheelpicker.WheelPicker.OnWheelChangeListener
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityReminderSettingBinding
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.entity.PeripheralParam
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gocam.goscamdemopro.utils.PeripheralManager
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.MotionDetection
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.PeripheralSetting
import com.gos.platform.api.devparam.PeripheralElement
import com.gos.platform.device.inter.OnDevEventCallback
import com.gos.platform.device.result.DevResult
import kotlinx.coroutines.launch

/**
 *
 * @Author wuzb
 * @Date 2025/08/28 18:02
 */
class ReminderSettingActivity : BaseBindActivity<ActivityReminderSettingBinding>(),
    OnDevEventCallback {
    private lateinit var mDevice: Device
    private lateinit var mElement: PeripheralElement
    private lateinit var mVolumeDialog: Dialog
    private lateinit var mTimeDialog: Dialog
    private lateinit var mDurationDialog: Dialog
    private lateinit var mWpStartHH: WheelPicker
    private lateinit var mWpStartMM: WheelPicker
    private lateinit var mWpEndHH: WheelPicker
    private lateinit var mWpEndMM: WheelPicker
    private var mHoursTime: String = ""
    private var startTempStartTime = 0
    private var endTempEndTime = 0
    private var mTime = 0
    private var hhList: ArrayList<String> = ArrayList()
    private var mmList: ArrayList<String> = ArrayList()
    private val mDurations: List<Int> = mutableListOf(15, 30, 60)

    override fun getLayoutId(): Int = R.layout.activity_reminder_setting

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")
        val subDevId = intent.getStringExtra("sub")
        if (subDevId == null || devId == null) {
            finish()
            return
        }
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        mElement = PeripheralManager.getInstance().getElement(devId, subDevId)
        mBinding?.apply {
            alarmReminder.isChecked = mElement.un_switch == 1
            alarmVolume.setRightText(mElement.un_volume.toString())
            alarmReminder.setOnCheckedChangeListener { v, isChecked ->
                val type = if (isChecked) 1 else 0
                mElement.un_switch = type
                saveCap()
            }
            flWorkingHours.setOnClickListener {
                // 时间段
                if (mTimeDialog.isShowing) {
                    mTimeDialog.dismiss()
                    return@setOnClickListener
                }
                mTimeDialog.show()
            }
            alarmVolume.setOnClickListener {
                // 音量
                if (mVolumeDialog.isShowing) {
                    mVolumeDialog.dismiss()
                    return@setOnClickListener
                }
                mVolumeDialog.show()
            }
            reminderDuration.setOnClickListener {
                // 时长
                if (mDurationDialog.isShowing) {
                    mDurationDialog.dismiss()
                    return@setOnClickListener
                }
                mDurationDialog.show()
            }
            reminderMode.setOnClickListener {
                // 跳转到提醒方式设置
                val intent = Intent(this@ReminderSettingActivity, ReminderMethodActivity::class.java)
                intent.putExtra("dev", devId)
                intent.putExtra("sub", mElement.subDevId)
                startActivity(intent)
            }
            tvDelete.setOnClickListener {
                // 删除设备
                if (!mDevice.isPlatDevOnline) {
                    Toast.makeText(this@ReminderSettingActivity, R.string.device_offline, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                mDevice.connection.deleteDevice(mElement.subDevId, 0, mElement.devicetype)
            }
        }
        initVolume()
        initTime()
        initDuration()
        mDevice.connection.addOnEventCallbackListener(this)
    }

    private fun initDuration() {
        mTime = mElement.un_duration
        mBinding?.reminderDuration?.setRightText(mTime.toString() + "S")
        mDurationDialog = Dialog(this, R.style.DialogTheme)
        val durationView = View.inflate(this, R.layout.layout_music_select, null)
        mDurationDialog.setContentView(durationView)
        val window = mDurationDialog.window
        window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setWindowAnimations(R.style.anim_menu_bottombar)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        val name = durationView.findViewById<TextView>(R.id.item_name)
        name.setText(R.string.string_reminder_duration)
        val durationSelect = durationView.findViewById<RecyclerView>(R.id.rv_music_select)
        val layoutManager = LinearLayoutManager(this)
        durationSelect.layoutManager = layoutManager
        val index: Int = mDurations.indexOf(mTime)
        val mAdapter =
            DurationAdapter(
                this, mDurations, index
            )
        durationSelect.adapter = mAdapter
        mAdapter.setItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                mTime = mDurations[position]
            }
        })

        val mTvCancel = durationView.findViewById<TextView>(R.id.tv_cancel)
        mTvCancel.setOnClickListener { mDurationDialog.dismiss() }

        val mTvSure = durationView.findViewById<TextView>(R.id.tv_sure)
        mTvSure.setOnClickListener {
            mElement.un_duration = mTime
            mBinding?.reminderDuration?.setRightText(mTime.toString() + "S")
            saveCap()
            mDurationDialog.dismiss()
        }
    }

    private fun initTime() {
        setHoursTime()
        mTimeDialog = Dialog(this, R.style.DialogTheme)
        val popRange = View.inflate(this, R.layout.layout_pop_camera_plan, null)
        mTimeDialog.setContentView(popRange)
        val window = mTimeDialog.window
        window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setWindowAnimations(R.style.anim_menu_bottombar)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        val repeat = popRange.findViewById<LinearLayout>(R.id.rl_repeat)
        val day = popRange.findViewById<LinearLayout>(R.id.rl_day)
        repeat.visibility = View.GONE
        day.visibility = View.GONE
        mWpStartHH = popRange.findViewById(R.id.wp_start_hh)
        mWpStartMM = popRange.findViewById(R.id.wp_start_mm)
        mWpEndHH = popRange.findViewById(R.id.wp_end_hh)
        mWpEndMM = popRange.findViewById(R.id.wp_end_mm)
        val mTvCancel = popRange.findViewById<TextView>(R.id.tv_cancel)
        val mTvSure = popRange.findViewById<TextView>(R.id.tv_sure)
        mTvCancel.setOnClickListener { mTimeDialog.dismiss() }
        mTvSure.setOnClickListener {
            try {
                mTimeDialog.dismiss()
                updateHHMM()
                mElement.un_begintime = startTempStartTime
                mElement.un_endtime = endTempEndTime
                mBinding?.workingHours?.setRightText(mHoursTime)
                saveCap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        for (i in 0..23) {
            if (i < 10) {
                hhList.add("0$i")
            } else {
                hhList.add(i.toString() + "")
            }
        }

        for (i in 0..59) {
            if (i < 10) {
                mmList.add("0$i")
            } else {
                mmList.add(i.toString() + "")
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
        timeRangeWindow.setBackgroundDrawable(BitmapDrawable(resources, null as Bitmap?))
        timeRangeWindow.animationStyle = R.style.anim_menu_bottombar

        timeRangeWindow.setOnDismissListener(PopupWindow.OnDismissListener {
            val lp1 = getWindow().attributes
            lp1.alpha = 1f
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            getWindow().attributes = lp1
        })

        mTimeDialog.setOnDismissListener(DialogInterface.OnDismissListener {
            val lp1 = getWindow().attributes
            lp1.alpha = 1f
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            getWindow().attributes = lp1
        })
    }

    private fun setHoursTime() {
        val sh: String = fillZero(mElement.un_begintime, 3600)
        val sm: String = fillZero((mElement.un_begintime % 3600), 60)
        val eh: String = fillZero(mElement.un_endtime, 3600)
        val em: String = fillZero((mElement.un_endtime % 3600), 60)
        mHoursTime = "$sh:$sm-$eh:$em"
        mBinding?.workingHours?.setRightText(mHoursTime)
    }

    private fun fillZero(divided: Int, divisor: Int): String {
        val value = divided / divisor
        return if (value < 10) "0$value" else "$value"
    }

    private val onWheelChangeListener: OnWheelChangeListener = object : OnWheelChangeListener {
        override fun onWheelScrolled(offset: Int) {
        }

        override fun onWheelSelected(position: Int) {
            updateHHMM()
        }

        override fun onWheelScrollStateChanged(state: Int) {
        }
    }

    private fun updateHHMM() {
        val sh: String = hhList[mWpStartHH!!.currentItemPosition]
        val sm: String = mmList[mWpStartMM!!.currentItemPosition]
        startTempStartTime = sh.toInt() * 3600 + sm.toInt() * 60

        var eh: String = hhList[mWpEndHH!!.currentItemPosition]
        val em: String = mmList[mWpEndMM!!.currentItemPosition]
        endTempEndTime = eh.toInt() * 3600 + em.toInt() * 60
        if (endTempEndTime == 0) {
            eh = "24"
            endTempEndTime = 86400
        }
        mHoursTime = "$sh:$sm-$eh:$em"
    }

    private fun initVolume() {
        mVolumeDialog = Dialog(this, R.style.DialogTheme)
        val volumeView = View.inflate(this, R.layout.layout_item_volume, null)
        mVolumeDialog.setContentView(volumeView)
        val window = mVolumeDialog.window
        window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setWindowAnimations(R.style.anim_menu_bottombar)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        val seek =volumeView.findViewById<SeekBar>(R.id.sb_volume)
        val audio =volumeView.findViewById<TextView>(R.id.tv_audio_set)
        seek.max = 10
        seek.progress = mElement.un_volume
        refreshProgress(mElement.un_volume, seek, audio)
        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                refreshProgress(p1, seek, audio)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
                mElement.un_volume = p0.progress
                mBinding?.alarmVolume?.setRightText(p0.progress.toString())
                saveCap()
            }
        })
    }

    private fun refreshProgress(progress: Int, seek: SeekBar, audio: TextView) {
        audio.text = progress.toString()
        val layoutParams = audio.layoutParams as LinearLayout.LayoutParams
        val thumbWidth: Int = seek.thumb.bounds.width()
        val thumbStart: Int = seek.thumb.bounds.left
        val marginStart: Int = seek.left + thumbStart + thumbWidth - audio.width / 2
        layoutParams.marginStart = marginStart
        audio.layoutParams = layoutParams
        audio.visibility = View.VISIBLE
    }

    private fun saveCap() {
        val deviceParam = PeripheralParam(PeripheralManager.getInstance().getPeripheral(mDevice.devId))
        val devParamArray = DevParamArray(
            PeripheralSetting,
            deviceParam
        )
        lifecycleScope.launch {
            val result = RemoteDataSource.setDeviceParam(devParamArray, deviceId = mDevice.devId)
        }
    }

    override fun onDevEvent(devId: String?, baseResult: DevResult?) {
        TODO("Not yet implemented")
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private class DurationAdapter(
        private val mContext: Context,
        private val durations: List<Int>?,
        private var index: Int
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var itemClickListener: OnItemClickListener? = null

        private var oldIndex = 0

        fun setItemClickListener(itemClickListener: OnItemClickListener) {
            this.itemClickListener = itemClickListener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val viewHolder: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_select, parent, false)
            return DurationHoldDer(viewHolder)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (durations == null) {
                return
            }

            val durationHold = holder as DurationHoldDer
            val time = durations[position].toString() + "S"
            durationHold.tvLeft.text = time
            if (position == index) {
                durationHold.imgRight.visibility = View.VISIBLE
                durationHold.tvLeft.setTextColor(mContext.resources.getColor(R.color._3CCF9B))
            } else {
                durationHold.imgRight.visibility = View.GONE
                durationHold.tvLeft.setTextColor(mContext.resources.getColor(R.color.text2))
            }

            durationHold.itemView.setOnClickListener {
                if (itemClickListener != null) {
                    oldIndex = index
                    index = durationHold.layoutPosition
                    itemClickListener!!.onItemClick(index)
                    notifyDataSetChanged()
                }
            }
        }

        override fun getItemCount(): Int {
            return durations?.size ?: 0
        }
    }

    private class DurationHoldDer(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvLeft: TextView = itemView.findViewById(R.id.tv_left_text)

        var imgRight: ImageView = itemView.findViewById(R.id.iv_right_pic)
    }
}