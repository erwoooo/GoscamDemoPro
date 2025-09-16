package com.gocam.goscamdemopro.peripheral

import android.content.Intent
import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.databinding.ActivityAddVphotoScheduleBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gos.platform.device.inter.OnDevEventCallback
import com.gos.platform.device.result.DevResult
import com.gos.platform.device.result.DevResult.DevCmd
import java.util.Timer
import java.util.TimerTask

/**
 *
 * @Author wuzb
 * @Date 2025/08/28 14:21
 */
class AddPeripheralDeviceActivity : BaseBindActivity<ActivityAddVphotoScheduleBinding>(),
    OnDevEventCallback {
    private lateinit var mDevice: Device

    private val timerTask: MyTimerTask by lazy {
        MyTimerTask()
    }
    private val mTimer = Timer()

    override fun getLayoutId(): Int = R.layout.activity_add_vphoto_schedule

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        mBinding?.apply {
            tvAddToast.setText(R.string.string_add_peripherals_toast)
        }
        mDevice.connection.addOnEventCallbackListener(this)
        mDevice.connection.addDevice(0, 1)
    }

    override fun onResume() {
        super.onResume()
        mTimer.schedule(timerTask, 1000, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        reset()
    }

    private fun reset() {
        mTimer.cancel()
        timerTask.cancel()
        mDevice.connection.removeOnEventCallbackListener(this)
    }

    override fun onDevEvent(devId: String?, baseResult: DevResult?) {
        baseResult?.apply {
            val cmd: DevCmd = devCmd
            val code: Int = responseCode
            if (DevResult.DevCmd.addDevice == devCmd) {
                reset()
                val intent = Intent(this@AddPeripheralDeviceActivity, AddPeripheralResultActivity::class.java)
                intent.putExtra("PAY_RESULT", code)
                startActivity(intent)
                finish()
            }
        }
    }

    inner class MyTimerTask: TimerTask() {
        override fun run() {
            mBinding?.apply {
                if (vphotoSpreadView.count < 100) {
                    vphotoSpreadView.count += 1
                }
            }

        }
    }
}