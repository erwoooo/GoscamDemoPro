package com.gocam.goscamdemopro.set.ai

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityAiModeSettingBinding
import com.gocam.goscamdemopro.entity.AiDetectionBoxParam
import com.gocam.goscamdemopro.entity.BoundarySettingParam
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.entity.NightLightColorParam
import com.gocam.goscamdemopro.set.DevShareActivity
import com.gocam.goscamdemopro.utils.DeviceManager
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam.DevParamCmdType
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.BoundaryParam
import kotlinx.coroutines.launch

/**
 *
 * @Author wuzb
 * @Date 2025/09/01 10:43
 */
class AiModeSettingActivity : BaseBindActivity<ActivityAiModeSettingBinding>() {
    private lateinit var mDevice: Device

    override fun getLayoutId(): Int = R.layout.activity_ai_mode_setting

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")?: return
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        if (mDevice == null)
            finish()
        mBinding?.apply {
            btnComfort.setOnClickListener {
                val intent = Intent(this@AiModeSettingActivity, AiPlacationActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@AiModeSettingActivity.startActivity(intent)
            }
            btnBoundary.setOnClickListener {
                val intent = Intent(this@AiModeSettingActivity, BoundaryMonitorActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@AiModeSettingActivity.startActivity(intent)
            }
            btnSleepMonitor.setOnClickListener {
                val intent = Intent(this@AiModeSettingActivity, SleepMonitorActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@AiModeSettingActivity.startActivity(intent)
            }
            btnPrivacyOcclusion.setOnClickListener {
                val intent = Intent(this@AiModeSettingActivity, PrivacyOcclusionActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@AiModeSettingActivity.startActivity(intent)
            }
            btnLightColor.setOnClickListener {
                val intent = Intent(this@AiModeSettingActivity, NightLightSettingActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@AiModeSettingActivity.startActivity(intent)
            }
            sivMoveSwitch.setOnCheckedChangeListener { v, isChecked ->
                saveCap(if (isChecked) 1 else 0)
            }
        }
        getCap()
    }

    private fun getCap() {
        lifecycleScope.launch {
            val result = RemoteDataSource.getDeviceParam(DevParamCmdType.DetectionBox, deviceId = mDevice.devId)
            result.let {
                for (param in it) {
                    if (param.CMDType == DevParamCmdType.DetectionBox) {
                        val boundaryParam = Gson().fromJson(param.DeviceParam, AiDetectionBoxParam::class.java)
                        mBinding?.sivMoveSwitch?.isChecked = boundaryParam.un_switch == 1
                    }
                }
            }
        }
    }

    private fun saveCap(index: Int) {
        val methodParam =
            AiDetectionBoxParam(index)
        val devParamArray = DevParamArray(
            DevParamCmdType.DetectionBox,
            methodParam
        )
        lifecycleScope.launch {
            val result = RemoteDataSource.setDeviceParam(devParamArray, deviceId = mDevice.devId)
        }
    }
}