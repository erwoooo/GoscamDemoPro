package com.gocam.goscamdemopro.peripheral

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityReminderMethodBinding
import com.gocam.goscamdemopro.entity.Device
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
 * @Date 2025/08/29 14:04
 */
class ReminderMethodActivity : BaseBindActivity<ActivityReminderMethodBinding>() {
    private lateinit var mDevice: Device
    private var reminderMethod: List<PeripheralElement.MethodBean>? = null
    private var mSubDevId: String = ""
    private var targetFragment: Fragment? = null

    private val mColors: Array<String> by lazy {
        getResources().getStringArray(R.array.reminder_light_color)
    }

    private val mRingtones: Array<String> by lazy {
        getResources().getStringArray(R.array.reminder_ringtone)
    }

    override fun getLayoutId(): Int = R.layout.activity_reminder_method

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")
        val subDevId = intent.getStringExtra("sub")
        if (subDevId == null || devId == null) {
            finish()
            return
        }
        mSubDevId = subDevId
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        mBinding?.apply {
            detectedCrying.setOnClickListener { startSetting(0) }
            hitTheFence.setOnClickListener { startSetting(1) }
            fellAsleep.setOnClickListener { startSetting(2) }
            tvAwake.setOnClickListener { startSetting(3) }
            coverFace.setOnClickListener { startSetting(4) }
        }
        getPeripheral()
    }

    private fun startSetting(type: Int) {
        mBinding?.apply {
            fl.visibility = View.VISIBLE
            val fm = supportFragmentManager
            targetFragment = fm.findFragmentByTag(ReminderMethodSettingFragment::class.java.simpleName)
            if (targetFragment == null) {
                val ft = fm.beginTransaction()
                targetFragment = ReminderMethodSettingFragment()
                val args = Bundle()
                args.putInt("ALARM_TYPE", type)
                args.putString("dev", mDevice.devId)
                args.putString("subId", mSubDevId)
                (targetFragment as ReminderMethodSettingFragment).setArguments(args)
                ft.replace(R.id.fl,
                    targetFragment as ReminderMethodSettingFragment, ReminderMethodSettingFragment::class.java.simpleName)
                ft.addToBackStack(null)
                ft.commit()
            }
        }
    }

    private fun getPeripheral() {
        val cmdType = DevParamCmdType.PeripheralSingle + "-" + mSubDevId;
        lifecycleScope.launch {
            val result = RemoteDataSource.getDeviceParam(cmdType, deviceId = mDevice.devId)
            result.let {
                for (param in it) {
                    if (param.CMDType == cmdType) {
                        val methodParam = Gson().fromJson(param.DeviceParam, ReminderMethodParam::class.java)
                        ReminderManager.getInstance().saveMethod(methodParam.reminder_method)
                        initView()
                    }
                }
            }
        }
    }

    private fun initView() {
        reminderMethod = ReminderManager.getInstance().methodBeans
        reminderMethod?.apply {
            for (method in this) {
                when(method.event_type) {
                    0 -> {
                        mBinding?.detectedCrying?.setRightText(mRingtones[method.un_ringindex])
                        mBinding?.detectedCrying?.setRightImage(mColors[method.un_colorindex])
                    }
                    1 -> {
                        mBinding?.hitTheFence?.setRightText(mRingtones[method.un_ringindex])
                        mBinding?.hitTheFence?.setRightImage(mColors[method.un_colorindex])
                    }
                    2 -> {
                        mBinding?.fellAsleep?.setRightText(mRingtones[method.un_ringindex])
                        mBinding?.fellAsleep?.setRightImage(mColors[method.un_colorindex])
                    }
                    3 -> {
                        mBinding?.tvAwake?.setRightText(mRingtones[method.un_ringindex])
                        mBinding?.tvAwake?.setRightImage(mColors[method.un_colorindex])
                    }
                    4 -> {
                        mBinding?.coverFace?.setRightText(mRingtones[method.un_ringindex])
                        mBinding?.coverFace?.setRightImage(mColors[method.un_colorindex])
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding?.fl?.visibility = View.GONE
    }
}