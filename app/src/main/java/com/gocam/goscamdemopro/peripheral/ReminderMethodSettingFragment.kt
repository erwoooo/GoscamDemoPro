package com.gocam.goscamdemopro.peripheral

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindFragment
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.FragmentReminderMethodSettingBinding
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.entity.PeripheralParam
import com.gocam.goscamdemopro.entity.ReminderMethodParam
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gocam.goscamdemopro.utils.PeripheralManager
import com.gocam.goscamdemopro.utils.ReminderManager
import com.gos.platform.api.devparam.DevParam.DevParamCmdType
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.PeripheralSetting
import com.gos.platform.api.devparam.PeripheralElement
import kotlinx.coroutines.launch

/**
 *
 * @Author wuzb
 * @Date 2025/08/29 15:15
 */
class ReminderMethodSettingFragment : BaseBindFragment<FragmentReminderMethodSettingBinding>() {
    private lateinit var mDevice: Device
    private var mMethod: PeripheralElement.MethodBean? = null
    private var mType: Int = 0
    private var mSubDevId: String = ""
    private var targetFragment: Fragment? = null
    private val mColors: Array<String> by lazy {
        resources.getStringArray(R.array.reminder_light_color)
    }

    private val mRingtones: Array<String> by lazy {
        resources.getStringArray(R.array.reminder_ringtone)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isClickable = true
    }

    override fun getLayoutId(): Int = R.layout.fragment_reminder_method_setting

    override fun initFragmentView() {
        arguments?.apply {
            mType = getInt("ALARM_TYPE")
            val devId = getString("dev")
            mSubDevId = getString("subId", "0")
            mMethod = ReminderManager.getInstance().getMethodBean(mType)
            mDevice = DeviceManager.getInstance().findDeviceById(devId)
        }
        val back = view?.findViewById<ImageView>(R.id.back_img)
        back?.setOnClickListener { activity?.supportFragmentManager?.popBackStack() }
        mBinding.apply {
            mMethod?.let {
                ringtoneReminder.isChecked = it.un_ring == 1
                lightReminder.isChecked = it.un_light == 1
                vibrationReminder.isChecked = it.un_shake == 1
                tvRingtone.setRightText(mRingtones[it.un_ringindex])
                lightColor.setRightImage(mColors[it.un_colorindex])
                lightEffects.setRightText(if (it.un_ledeffect == 1) R.string.light else R.string.twinkle)
                ringtoneReminder.setOnCheckedChangeListener { v, isChecked ->
                    it.un_ring = if (isChecked) 1 else 0
                    saveCap()
                }
                lightReminder.setOnCheckedChangeListener { v, isChecked ->
                    it.un_light = if (isChecked) 1 else 0
                    saveCap()
                }
                vibrationReminder.setOnCheckedChangeListener { v, isChecked ->
                    it.un_shake = if (isChecked) 1 else 0
                    saveCap()
                }
                tvRingtone.setOnClickListener { startSetting(0) }
                lightColor.setOnClickListener { startSetting(1) }
                lightEffects.setOnClickListener { startSetting(2) }
            }
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
        }
    }

    private fun startSetting(type: Int) {
        mBinding.apply {
            fl.visibility = View.VISIBLE
            val fm = activity?.supportFragmentManager
            fm?.let {
                targetFragment = it.findFragmentByTag(ReminderLightSettingFragment::class.java.simpleName)
                if (targetFragment == null) {
                    val ft = it.beginTransaction()
                    targetFragment = ReminderLightSettingFragment()
                    val args = Bundle()
                    args.putInt("ALARM_TYPE", type)
                    args.putInt("EVENT_TYPE", mType)
                    args.putString("dev", mDevice.devId)
                    args.putString("subId", mSubDevId)
                    (targetFragment as ReminderLightSettingFragment).setArguments(args)
                    ft.replace(R.id.fl,
                        targetFragment as ReminderLightSettingFragment, ReminderLightSettingFragment::class.java.simpleName)
                    ft.addToBackStack(null)
                    ft.commit()
                }
            }
        }
    }
}