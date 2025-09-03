package com.gocam.goscamdemopro.peripheral

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindFragment
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.FragmentReminderMethodLightBinding
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.entity.ReminderMethodParam
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gocam.goscamdemopro.utils.ReminderManager
import com.gos.platform.api.devparam.DevParam.DevParamCmdType
import com.gos.platform.api.devparam.PeripheralElement
import kotlinx.coroutines.launch

/**
 *
 * @Author wuzb
 * @Date 2025/08/29 17:04
 */
class ReminderLightSettingFragment : BaseBindFragment<FragmentReminderMethodLightBinding>() {
    private lateinit var mDevice: Device
    private var mMethod: PeripheralElement.MethodBean? = null
    private var mType: Int = 0
    private var mEventTypeId: Int = 0
    private var mSubDevId: String = ""
    private val mOption: Array<String> by lazy {
        if (mType == 0) {
            resources.getStringArray(R.array.reminder_ringtone)
        } else if (mType == 1) {
            resources.getStringArray(R.array.reminder_light_color)
        } else {
            resources.getStringArray(R.array.reminder_light_effects)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isClickable = true
    }

    override fun getLayoutId(): Int = R.layout.fragment_reminder_method_light

    override fun initFragmentView() {
        arguments?.apply {
            mType = getInt("ALARM_TYPE")
            mEventTypeId = getInt("EVENT_TYPE")
            val devId = getString("dev")
            mSubDevId = getString("subId", "0")
            mMethod = ReminderManager.getInstance().getMethodBean(mEventTypeId)
            mDevice = DeviceManager.getInstance().findDeviceById(devId)
        }
        val count = if (mType == 0) {
            mMethod?.un_ringindex?:0
        } else if (mType == 1) {
            mMethod?.un_colorindex?:0
        } else {
            mMethod?.un_ledeffect?:0
        }
        mBinding.apply {
            val back = view?.findViewById<ImageView>(R.id.back_img)
            back?.setOnClickListener { activity?.supportFragmentManager?.popBackStack() }
            val manager = LinearLayoutManager(context)
            rvSelect.layoutManager = manager
            val adapter = LightAdapter(mOption, count, mType, object : ItemClickListener {
                override fun onItemClick(position: Int) {
                    mMethod?.let {
                        if (mType == 0) {
                            it.un_ringindex = position
                        } else if (mType == 1) {
                            it.un_colorindex = position
                        } else if (mType == 2) {
                            it.un_ledeffect = position + 1
                        }
                        saveCap()
                    }
                }
            })
            rvSelect.adapter = adapter
        }
    }

    private fun saveCap() {
        val methodParam =
            ReminderMethodParam(
                mSubDevId,
                mType,
                ReminderManager.getInstance().methodBeans
            )
        val devParamArray = DevParamArray(
            DevParamCmdType.PeripheralSingle + "-" + mSubDevId,
            methodParam
        )
        lifecycleScope.launch {
            val result = RemoteDataSource.setDeviceParam(devParamArray, deviceId = mDevice.devId)
            result.let {
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    private class LightAdapter(
        private val names: Array<String>?,
        private var count: Int,
        private var type: Int,
        private val clickListener: ItemClickListener
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        fun setCount(count: Int) {
            this.count = count
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val viewHolder = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_select, parent, false)
            return LightHolder(viewHolder)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val lightHolder = holder as LightHolder
            if (type == 1) {
                lightHolder.back.visibility = View.VISIBLE
                lightHolder.name.visibility = View.INVISIBLE
                val drawable = GradientDrawable()
                drawable.shape = GradientDrawable.RECTANGLE
                drawable.setColor(Color.parseColor(names!![position]))
                drawable.setStroke(2, Color.parseColor("#000000"))
                lightHolder.back.background = drawable
            } else {
                lightHolder.back.visibility = View.GONE
                lightHolder.name.visibility = View.VISIBLE
                lightHolder.name.text = names!![position]
            }

            if (position > 0) {
                lightHolder.topLine.visibility = View.VISIBLE
            }

            if (position == count) {
                lightHolder.img.visibility = View.VISIBLE
            } else {
                lightHolder.img.visibility = View.GONE
            }
            lightHolder.itemView.setOnClickListener(View.OnClickListener {
                clickListener.onItemClick(lightHolder.layoutPosition)
                setCount(lightHolder.layoutPosition)
            })
        }

        override fun getItemCount(): Int {
            return names?.size ?:0
        }
    }

    private class LightHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var topLine: View = itemView.findViewById(R.id.view_top_line)
        var back: View = itemView.findViewById(R.id.iv_right_pic_2)
        var name: TextView = itemView.findViewById(R.id.tv_left_text)
        var img: ImageView = itemView.findViewById(R.id.iv_right_pic)
    }
}