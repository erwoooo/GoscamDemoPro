package com.gocam.goscamdemopro.set

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.annotation.NonNull
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityMotionDetectionBinding
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.MotionParam
import com.gocam.goscamdemopro.utils.Util
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.MotionDetection
import com.gos.platform.device.contact.MotionDetectLevel
import com.gos.platform.device.contact.OnOff

class MotionDetectionActivity : BaseActivity<ActivityMotionDetectionBinding, MotionViewModel>(),
    CompoundButton.OnCheckedChangeListener {
    private lateinit var deviceId: String
    private lateinit var adapter: AreaAdapter
    private lateinit var motionParam: MotionParam
    var areaList = BooleanArray(16)


    override fun getLayoutId(): Int {
        return R.layout.activity_motion_detection
    }

    override fun onCreateData(bundle: Bundle?) {
        deviceId = intent.getStringExtra("dev") as String
        mViewModel.getMotionParam(deviceId)
        adapter = AreaAdapter()
        mViewModel.apply {
            mMotionParam.observe(this@MotionDetectionActivity) {
                motionParam = it
                mBinding?.iSwitch?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.c_switch == OnOff.On
                    setOnCheckedChangeListener(this@MotionDetectionActivity)
                }
                val level = it.c_sensitivity
                areaList = Util.parserIntEnable2BooleanArray(it.un_enable, 16)

                adapter.setAreaList(areaList)
                when (level) {
                    MotionDetectLevel.HEIGH -> {
                        mBinding?.rg?.check(R.id.rb_high)
                    }
                    MotionDetectLevel.MIDDEL -> {
                        mBinding?.rg?.check(R.id.rb_default)
                    }
                    else -> {
                        mBinding?.rg?.check(R.id.rb_low)
                    }

                }
            }
        }
        mBinding?.apply {
            recycleView.layoutManager = GridLayoutManager(this@MotionDetectionActivity, 4)
            recycleView.adapter = adapter

            btnSave.setOnClickListener {
                motionParam!!.apply {
                    when (mBinding?.rg?.checkedRadioButtonId) {
                        R.id.rb_high -> this.c_sensitivity = MotionDetectLevel.HEIGH
                        R.id.rb_default -> this.c_sensitivity = MotionDetectLevel.MIDDEL
                        R.id.rb_low -> this.c_sensitivity = MotionDetectLevel.LOW
                    }

                    c_switch = if (mBinding?.iSwitch?.isChecked == true) {
                        OnOff.On
                    } else {
                        OnOff.Off
                    }
                }
                val devParamArray = DevParamArray(
                    MotionDetection,
                    motionParam
                )

                mViewModel?.setDeviceParam(devParamArray, deviceId)
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {


    }


    internal class AreaAdapter : RecyclerView.Adapter<AreaAdapter.Vh?>() {

        private var areaList = BooleanArray(16)

        fun setAreaList(areas: BooleanArray) {
            areaList = areas
            notifyDataSetChanged()
        }

        @NonNull
        override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, type: Int): Vh {
            val view: View =
                LayoutInflater.from(viewGroup.context).inflate(R.layout.item_area, viewGroup, false)
            return Vh(view)
        }

        override fun onBindViewHolder(@NonNull vh: Vh, @SuppressLint("RecyclerView") i: Int) {
            vh.rl.setBackgroundColor(
                if (areaList[i]) Color.parseColor("#bbbbbb") else Color.parseColor(
                    "#dddddd"
                )
            )
            vh.rl.setOnClickListener {
                areaList[i] = !areaList[i]
                notifyDataSetChanged()
            }
        }


        internal inner class Vh(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
            var rl: View = itemView.findViewById(R.id.rl)

        }

        override fun getItemCount(): Int {
            return areaList.size
        }
    }

}