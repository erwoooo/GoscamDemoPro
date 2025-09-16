package com.gocam.goscamdemopro.set.ai

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
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
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aigestudio.wheelpicker.WheelPicker
import com.aigestudio.wheelpicker.WheelPicker.OnWheelChangeListener
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityAiPlacationBinding
import com.gocam.goscamdemopro.entity.CryAlarmPushSettingParam
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.utils.DateUtils
import com.gocam.goscamdemopro.utils.DeviceManager
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.CryAlarmPushSetting
import kotlinx.coroutines.launch

/**
 *
 * @Author wuzb
 * @Date 2025/09/01 11:13
 */
class AiPlacationActivity : BaseBindActivity<ActivityAiPlacationBinding>() {
    private lateinit var mDevice: Device
    private lateinit var mCryParam: CryAlarmPushSettingParam
    private lateinit var mDialog: Dialog
    private lateinit var mMusicDialog: Dialog
    private lateinit var mComfortDialog: Dialog
    private lateinit var mWpStartHH: WheelPicker
    private lateinit var mWpStartMM: WheelPicker
    private lateinit var mWpEndHH: WheelPicker
    private lateinit var mWpEndMM: WheelPicker
    private var mAdapter: MusicAdapter? = null
    private var startTempStartTime: Int = 0
    private var endTempEndTime: Int = 0
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
    private val musics: Array<String> by lazy {
        getResources().getStringArray(R.array.soothing_music_play_raw_id_9)
    }

    override fun getLayoutId(): Int = R.layout.activity_ai_placation

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")?: return
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        if (mDevice == null)
            finish()
        mBinding?.apply {
            itemMusic.setOnClickListener {
                if (mMusicDialog.isShowing) {
                    mMusicDialog.dismiss()
                } else {
                    mMusicDialog.show()
                }
            }
            itemComfortTime.setOnClickListener {
                if (mComfortDialog.isShowing) {
                    mComfortDialog.dismiss()
                } else {
                    mComfortDialog.show()
                }
            }
            itemSensitivity.setOnClickListener {
                if (mDialog.isShowing) {
                    mDialog.dismiss()
                } else {
                    mDialog.show()
                }
            }
            itemCry.setOnCheckedChangeListener { v, isChecked ->
                mCryParam.un_switch = if (isChecked) 1 else 0
                saveCap()
            }
            itemNightLight.setOnCheckedChangeListener { v, isChecked ->
                mCryParam.un_nightlamp = if (isChecked) 1 else 0
                saveCap()
            }
            itemMusicalComfort.setOnCheckedChangeListener { v, isChecked ->
                mCryParam.un_soundsoothes = if (isChecked) 1 else 0
                saveCap()
            }
        }
        initDialog()
        initMusicDialog()
        initComfortDialog()
        getCap()
    }

    private fun getCap() {
        lifecycleScope.launch {
            val result = RemoteDataSource.getDeviceParam(CryAlarmPushSetting, deviceId = mDevice.devId)
            result.let {
                for (param in it) {
                    if (param.CMDType == CryAlarmPushSetting) {
                        val cryParam = Gson().fromJson(param.DeviceParam, CryAlarmPushSettingParam::class.java)
                        initView(cryParam)
                    }
                }
            }
        }
    }

    private fun initView(cryParam: CryAlarmPushSettingParam) {
        mCryParam = cryParam
        mBinding?.apply {
            itemCry.isChecked = mCryParam.un_switch == 1
            itemNightLight.isChecked = mCryParam.un_nightlamp == 1
            itemMusicalComfort.isChecked = mCryParam.un_soundsoothes == 1
            itemSensitivity.setRightText(
                if (mCryParam.un_sensitivity == 1) {
                    getResources().getString(R.string.string_open_height)
                } else if (mCryParam.un_sensitivity == 2) {
                    getResources().getString(R.string.string_open_mid)
                } else if (mCryParam.un_sensitivity == 3) {
                    getResources().getString(R.string.string_open_low)
                } else {
                    getResources().getString(R.string.string_support_off)
                }
            )
            itemComfortTime.setRightText(
                DateUtils.getTimeHHMM(mCryParam.starttime) + "-" + DateUtils.getTimeHHMM(mCryParam.endtime)
            )
            if (musics.size > mCryParam.un_musicindex) {
                itemMusic.setRightText(musics[mCryParam.un_musicindex])
                mAdapter?.setPosition(mCryParam.un_musicindex)
            }
        }
    }

    private fun initDialog() {
        mDialog = Dialog(this, R.style.DialogTheme)
        val view = View.inflate(this, R.layout.layout_setting_sleep_monitor, null)
        mDialog.setContentView(view)
        val window = mDialog.window
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.anim_menu_bottombar)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val mName = view.findViewById<TextView>(R.id.item_name)
        val mHeightTv = view.findViewById<TextView>(R.id.tv_height_time)
        val mHeightTitle = view.findViewById<TextView>(R.id.tv_height_time2)
        val mMidTv = view.findViewById<TextView>(R.id.tv_mid_time)
        val mMidTitle = view.findViewById<TextView>(R.id.tv_mid_time2)
        val mLowTv = view.findViewById<TextView>(R.id.tv_low_time)
        val mLowTitle = view.findViewById<TextView>(R.id.tv_low_time2)
        val mHeightImg = view.findViewById<ImageView>(R.id.iv_height_time)
        val mMidImg = view.findViewById<ImageView>(R.id.iv_mid_time)
        val mLowImg = view.findViewById<ImageView>(R.id.iv_low_time)
        mHeightTitle.setText(R.string.string_cry_detection_warn_1)
        mMidTitle.setText(R.string.string_cry_detection_warn_2)
        mLowTitle.setText(R.string.string_cry_detection_warn_3)
        mName.setText(R.string.setting_sensitivity)
        val mNotOpen = view.findViewById<LinearLayout>(R.id.ll_not_open)
        mNotOpen.visibility = View.GONE
        val mHeight = view.findViewById<LinearLayout>(R.id.ll_height)
        mHeight.setOnClickListener {
            mCryParam.un_sensitivity = 1
            mMidImg.visibility = View.INVISIBLE
            mLowImg.visibility = View.INVISIBLE
            mMidTv.setTextColor(resources.getColor(R.color.text2))
            mLowTv.setTextColor(resources.getColor(R.color.text2))
            mHeightImg.visibility = View.VISIBLE
            mHeightTv.setTextColor(resources.getColor(R.color._3CCF9B))
        }
        val mMid = view.findViewById<LinearLayout>(R.id.ll_below_time)
        mMid.setOnClickListener {
            mCryParam.un_sensitivity = 2
            mHeightImg.visibility = View.INVISIBLE
            mLowImg.visibility = View.INVISIBLE
            mHeightTv.setTextColor(resources.getColor(R.color.text2))
            mLowTv.setTextColor(resources.getColor(R.color.text2))
            mMidImg.visibility = View.VISIBLE
            mMidTv.setTextColor(resources.getColor(R.color._3CCF9B))
        }
        val mLow = view.findViewById<LinearLayout>(R.id.ll_low_time)
        mLow.setOnClickListener {
            mCryParam.un_sensitivity = 3
            mHeightImg.visibility = View.INVISIBLE
            mMidImg.visibility = View.INVISIBLE
            mHeightTv.setTextColor(resources.getColor(R.color.text2))
            mMidTv.setTextColor(resources.getColor(R.color.text2))
            mLowImg.visibility = View.VISIBLE
            mLowTv.setTextColor(resources.getColor(R.color._3CCF9B))
        }
        val mTvCancel = view.findViewById<TextView>(R.id.tv_cancel)
        mTvCancel.setOnClickListener {
            mDialog.dismiss()
        }
        val mTvSure = view.findViewById<TextView>(R.id.tv_sure)
        mTvSure.setOnClickListener {
            mDialog.dismiss()
            saveCap()
        }
    }

    private fun initMusicDialog() {
        mMusicDialog = Dialog(this, R.style.DialogTheme)
        val view = View.inflate(this, R.layout.layout_music_select, null)
        mMusicDialog.setContentView(view)
        val window = mMusicDialog.window
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.anim_menu_bottombar)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val musicSelect = view.findViewById<RecyclerView>(R.id.rv_music_select)
        val layoutManager = LinearLayoutManager(this)
        musicSelect.layoutManager = layoutManager
        mAdapter = MusicAdapter(
            this,
            musics
        )
        musicSelect.adapter = mAdapter
        mAdapter?.setItemClickListener(object :
            OnItemClickListener {
            override fun onItemClick(position: Int) {
                mCryParam.un_musicindex = position
                saveCap()
            }
        })

        val mTvCancel = view.findViewById<TextView>(R.id.tv_cancel)
        mTvCancel.setOnClickListener { mMusicDialog.dismiss() }

        val mTvSure = view.findViewById<TextView>(R.id.tv_sure)
        mTvSure.setOnClickListener { mMusicDialog.dismiss() }
    }

    private fun initComfortDialog() {
        mComfortDialog = Dialog(this, R.style.DialogTheme)
        val popRange = View.inflate(this, R.layout.layout_pop_camera_plan, null)
        mComfortDialog.setContentView(popRange)
        val window = mComfortDialog.window
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.anim_menu_bottombar)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mWpStartHH = popRange.findViewById(R.id.wp_start_hh)
        mWpStartMM = popRange.findViewById(R.id.wp_start_mm)
        mWpEndHH = popRange.findViewById(R.id.wp_end_hh)
        mWpEndMM = popRange.findViewById(R.id.wp_end_mm)
        val mLlDay = popRange.findViewById<LinearLayout>(R.id.rl_day)
        val mLlRepeat = popRange.findViewById<LinearLayout>(R.id.rl_repeat)
        mLlDay.visibility = View.GONE
        mLlRepeat.visibility = View.GONE
        val mTvCancel = popRange.findViewById<TextView>(R.id.tv_cancel)
        val mTvSure = popRange.findViewById<TextView>(R.id.tv_sure)
        mTvCancel.setOnClickListener { mComfortDialog.dismiss() }
        mTvSure.setOnClickListener {
            try {
                updateHHMM()
                mCryParam.starttime = startTempStartTime
                mCryParam.endtime = endTempEndTime
                saveCap()
                mComfortDialog.dismiss()
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
        timeRangeWindow.setBackgroundDrawable(BitmapDrawable(resources, null as Bitmap?))
        timeRangeWindow.animationStyle = R.style.anim_menu_bottombar

        timeRangeWindow.setOnDismissListener(PopupWindow.OnDismissListener {
            val lp1 = getWindow().attributes
            lp1.alpha = 1f
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            getWindow().attributes = lp1
        })

        mComfortDialog.setOnDismissListener(DialogInterface.OnDismissListener {
            val lp1 = getWindow().attributes
            lp1.alpha = 1f
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            getWindow().attributes = lp1
        })
    }

    private var onWheelChangeListener: OnWheelChangeListener = object : OnWheelChangeListener {
        override fun onWheelScrolled(offset: Int) {
        }

        override fun onWheelSelected(position: Int) {
            updateHHMM()
        }

        override fun onWheelScrollStateChanged(state: Int) {
        }
    }

    private fun updateHHMM() {
        val sh = hhList[mWpStartHH.currentItemPosition]
        val sm = mmList[mWpStartMM.currentItemPosition]
        startTempStartTime = sh.toInt() * 3600 + sm.toInt() * 60

        val eh = hhList[mWpEndHH.currentItemPosition]
        val em = mmList[mWpEndMM.currentItemPosition]
        endTempEndTime = eh.toInt() * 3600 + em.toInt() * 60
    }

    private fun saveCap() {
        val devParamArray = DevParamArray(
            CryAlarmPushSetting,
            mCryParam
        )
         lifecycleScope.launch {
             val result = RemoteDataSource.setDeviceParam(devParamArray, deviceId = mDevice.devId)
             initView(mCryParam)
         }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private class MusicAdapter(private val mContext: Context, musicList: Array<String>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var musicList: List<String> = ArrayList()

        private var itemClickListener: OnItemClickListener? = null

        private var mPosition = 0

        fun setItemClickListener(itemClickListener: OnItemClickListener?) {
            this.itemClickListener = itemClickListener
        }

        init {
            this.musicList = musicList.toList()
        }

        fun setPosition(position: Int) {
            this.mPosition = position
            notifyDataSetChanged()
        }

        fun setMusicList(musicList: List<String>) {
            this.musicList = musicList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val viewHolder = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_select, parent, false)
            val musicHolder = MusicHolder(viewHolder)
            musicHolder.itemView.setOnClickListener(View.OnClickListener {
                mPosition = musicHolder.layoutPosition
                itemClickListener!!.onItemClick(mPosition)
                notifyDataSetChanged()
            })
            return musicHolder
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (musicList.isEmpty()) {
                return
            }

            val musicHolder = holder as MusicHolder
            musicHolder.tvLeft.text = musicList[position]
            if (position == mPosition) {
                musicHolder.imgRight.visibility = View.VISIBLE
                musicHolder.tvLeft.setTextColor(mContext.resources.getColor(R.color._3CCF9B))
            } else {
                musicHolder.imgRight.visibility = View.GONE
                musicHolder.tvLeft.setTextColor(mContext.resources.getColor(R.color.text2))
            }
        }

        override fun getItemCount(): Int {
            return if (musicList.isNotEmpty()) musicList.size else 1
        }
    }

    private class MusicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvLeft: TextView = itemView.findViewById(R.id.tv_left_text)

        var imgRight: ImageView = itemView.findViewById(R.id.iv_right_pic)
    }
}