package com.gocam.goscamdemopro.set.ai

import android.content.Intent
import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.databinding.ActivityAiModeSettingBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.set.DevShareActivity
import com.gocam.goscamdemopro.utils.DeviceManager

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
        }
    }
}