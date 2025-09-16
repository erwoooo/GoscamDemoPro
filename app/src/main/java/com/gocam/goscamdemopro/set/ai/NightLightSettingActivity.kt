package com.gocam.goscamdemopro.set.ai

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
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.base.BaseBindFragment
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.FragmentReminderMethodLightBinding
import com.gocam.goscamdemopro.entity.AiDetectionBoxParam
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.entity.NightLightColorParam
import com.gocam.goscamdemopro.entity.ReminderMethodParam
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gocam.goscamdemopro.utils.ReminderManager
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam.DevParamCmdType
import com.gos.platform.api.devparam.PeripheralElement
import kotlinx.coroutines.launch

/**
 *
 * @Author wuzb
 * @Date 2025/08/29 17:04
 */
class NightLightSettingActivity : BaseBindActivity<FragmentReminderMethodLightBinding>() {
    private lateinit var mDevice: Device
    private val mAdapter: LightAdapter by lazy { LightAdapter(mOption,  object : ItemClickListener {
        override fun onItemClick(position: Int) {
            saveCap(position)
        }
    }) }
    private val mOption: Array<String> by lazy {
        resources.getStringArray(R.array.reminder_light_color)
    }

    override fun getLayoutId(): Int = R.layout.fragment_reminder_method_light
    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")?: return
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        mBinding?.apply {
            val manager = LinearLayoutManager(this@NightLightSettingActivity)
            rvSelect.layoutManager = manager
            rvSelect.adapter = mAdapter
        }
        getCap()
    }

    private fun getCap() {
        lifecycleScope.launch {
            val result = RemoteDataSource.getDeviceParam(DevParamCmdType.NightLightColor, deviceId = mDevice.devId)
            result.let {
                for (param in it) {
                    if (param.CMDType == DevParamCmdType.NightLightColor) {
                        val boundaryParam = Gson().fromJson(param.DeviceParam, NightLightColorParam::class.java)
                        mAdapter.count = boundaryParam.un_colorindex
                        mAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun saveCap(index: Int) {
        val methodParam =
            NightLightColorParam(index)
        val devParamArray = DevParamArray(
            DevParamCmdType.NightLightColor,
            methodParam
        )
        lifecycleScope.launch {
            val result = RemoteDataSource.setDeviceParam(devParamArray, deviceId = mDevice.devId)
        }
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    private class LightAdapter(
        private val names: Array<String>?,
        private val clickListener: ItemClickListener
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var count: Int = -1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val viewHolder = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_select, parent, false)
            return LightHolder(viewHolder)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val lightHolder = holder as LightHolder
            lightHolder.back.visibility = View.VISIBLE
            lightHolder.name.visibility = View.INVISIBLE
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.RECTANGLE
            drawable.setColor(Color.parseColor(names!![position]))
            drawable.setStroke(2, Color.parseColor("#000000"))
            lightHolder.back.background = drawable


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
                count = lightHolder.layoutPosition
                notifyDataSetChanged()
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