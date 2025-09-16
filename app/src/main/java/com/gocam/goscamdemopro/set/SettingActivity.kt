package com.gocam.goscamdemopro.set

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.coroutineScope
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.cloud.data.GosCloud
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivitySettingBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.set.ai.AiModeSettingActivity
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gos.platform.api.contact.DeviceType
import kotlinx.coroutines.launch

class SettingActivity : BaseBindActivity<ActivitySettingBinding>() {
    private lateinit var mDevice: Device
    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")?: return
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        if (mDevice == null)
            finish()


        mBinding?.apply {

            toolBar.backImg.setOnClickListener {
                finish()
            }

            btnAiMode.visibility = if (mDevice.devCap.isPeripheral) View.VISIBLE else View.GONE

            btnModifyName.setOnClickListener {
                val intent = Intent(this@SettingActivity, CommonSetActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@SettingActivity.startActivity(intent)
            }

            btnDevShare.setOnClickListener {
                val intent = Intent(this@SettingActivity, DevShareActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@SettingActivity.startActivity(intent)
            }

            btnDeleteDev.setOnClickListener {

                val builder = AlertDialog.Builder(this@SettingActivity)
                builder.setTitle("Notice")
                builder.setMessage(R.string.sure_delete)
                builder.setNegativeButton(
                    "Cancel"
                ) { dialog, which -> dialog.dismiss() }
                builder.setPositiveButton(
                    "Delete"
                ) { dialog, which ->
                    dialog.dismiss()

                    lifecycle.coroutineScope.launch {
                        val isOwner = if (mDevice.isOwn) {
                            1
                        } else {
                            0
                        }
                        val result = RemoteDataSource.unbindSmartDevice(
                            GApplication.app.user.userName!!,
                            mDevice.devId,
                            isOwner
                        )
                        checkNotNull(result) {
                            finish()
                        }
                    }

                }
                builder.show()

            }

            btnMoveMotion.setOnClickListener {
                val intent = Intent(this@SettingActivity, MotionDetectionActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@SettingActivity.startActivity(intent)
            }

            btnTime.setOnClickListener {
                val intent = Intent(this@SettingActivity, TimeVerifyActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@SettingActivity.startActivity(intent)
            }

            btnTfInfo.setOnClickListener {
                val intent = Intent(this@SettingActivity, TfInfoActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@SettingActivity.startActivity(intent)
            }

            btnSoftUpdate.setOnClickListener {
                val intent = Intent(this@SettingActivity, DevSoftUpdateActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@SettingActivity.startActivity(intent)
            }

            btnAiMode.setOnClickListener {
                // Ai模式设置页面
                val intent = Intent(this@SettingActivity, AiModeSettingActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@SettingActivity.startActivity(intent)
            }
        }
    }
}