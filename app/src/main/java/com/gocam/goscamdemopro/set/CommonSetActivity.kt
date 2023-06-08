package com.gocam.goscamdemopro.set

import android.os.Bundle
import android.widget.CompoundButton
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityCommonSetBinding
import com.gocam.goscamdemopro.entity.*
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.*
import com.gos.platform.device.contact.OnOff

class CommonSetActivity : BaseActivity<ActivityCommonSetBinding, CommonSetViewModel>(),
    CompoundButton.OnCheckedChangeListener {
    private lateinit var deviceId: String

    override fun getLayoutId(): Int {
        return R.layout.activity_common_set
    }

    override fun onCreateData(bundle: Bundle?) {
        deviceId = intent.getStringExtra("dev").toString()

        mBinding?.apply {
            btnModify.setOnClickListener {
                val userName = etDevName.text.toString()
                if (!userName.isNullOrEmpty())
                    mViewModel.modifyName(userName, deviceId)
            }


        }

        mViewModel?.getDeviceParam(deviceId)

        mViewModel?.apply {
            mCameraParamResult.observe(this@CommonSetActivity) {
                mBinding?.cameraSwitch?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.device_switch == OnOff.On
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }
            }

            mPirParamResult.observe(this@CommonSetActivity) {
                mBinding?.pirSwitch?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.un_switch == OnOff.On
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }
            }

            mPLedParamResult.observe(this@CommonSetActivity) {
                mBinding?.ledSwitch?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.device_led_switch == OnOff.On
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }
            }

            mMicParamResult.observe(this@CommonSetActivity) {
                mBinding?.micSwitch?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.device_mic_switch == OnOff.On
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }
            }

            mNightParamResult.observe(this@CommonSetActivity) {
                mBinding?.rbAuto?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.un_auto == OnOff.On
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }

                mBinding?.rbOpen?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.un_day_night == OnOff.On
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }

                mBinding?.rbClose?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked =
                        it.un_auto == OnOff.Off && it.un_day_night == OnOff.Off
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }
            }

        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.camera_switch -> {
                val open = if (isChecked) {
                    OnOff.On
                } else {
                    OnOff.Off
                }
                val cameraParam = CameraParam(
                    open
                )
                val devParamArray = DevParamArray(
                    DeviceSwitch,
                    cameraParam
                )
                mViewModel.setDeviceParam(devParamArray, deviceId)
            }
            R.id.pir_switch -> {
                val open = if (isChecked) {
                    OnOff.On
                } else {
                    OnOff.Off
                }
                val pirParam = PirParam(
                    0, 0, 0, open
                )

                val devParamArray = DevParamArray(
                    PirSetting,
                    pirParam
                )
                mViewModel.setDeviceParam(devParamArray, deviceId)
            }
            R.id.led_switch -> {
                val open = if (isChecked) {
                    OnOff.On
                } else {
                    OnOff.Off
                }
                val ledSwitchParam = LedSwitchParam(
                    open
                )
                val devParamArray = DevParamArray(
                    LedSwitch,
                    ledSwitchParam
                )
                mViewModel.setDeviceParam(devParamArray, deviceId)
            }
            R.id.mic_switch -> {
                val open = if (isChecked) {
                    OnOff.On
                } else {
                    OnOff.Off
                }
                val micParam = MicSwitchParam(
                    open
                )
                val devParamArray = DevParamArray(
                    MicSwitch,
                    micParam
                )
                mViewModel.setDeviceParam(devParamArray, deviceId)
            }

            R.id.rb_auto->{
                mBinding?.rbClose?.apply {
                    setOnCheckedChangeListener(null)
                    this.isChecked = false
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }
                mBinding?.rbOpen?.apply {
                    setOnCheckedChangeListener(null)
                    this.isChecked = false
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }
                val nightModeParam = NightModeParam(
                    OnOff.On,
                    OnOff.Off
                )
                val devParamArray = DevParamArray(
                    NightSetting,
                    nightModeParam
                )
                mViewModel.setDeviceParam(devParamArray, deviceId)
            }
            R.id.rb_open->{
                mBinding?.rbClose?.apply {
                    setOnCheckedChangeListener(null)
                    this.isChecked = false
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }
                mBinding?.rbAuto?.apply {
                    setOnCheckedChangeListener(null)
                    this.isChecked = false
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }
                val nightModeParam = NightModeParam(
                    OnOff.Off,
                    OnOff.On
                )
                val devParamArray = DevParamArray(
                    NightSetting,
                    nightModeParam
                )
                mViewModel.setDeviceParam(devParamArray, deviceId)
            }
            R.id.rb_close->{
                mBinding?.rbOpen?.apply {
                    setOnCheckedChangeListener(null)
                    this.isChecked = false
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }
                mBinding?.rbAuto?.apply {
                    setOnCheckedChangeListener(null)
                    this.isChecked = false
                    setOnCheckedChangeListener(this@CommonSetActivity)
                }
                val nightModeParam = NightModeParam(
                    OnOff.Off,
                    OnOff.Off
                )
                val devParamArray = DevParamArray(
                    NightSetting,
                    nightModeParam
                )
                mViewModel.setDeviceParam(devParamArray, deviceId)

            }
        }
    }
}