package com.gocam.goscamdemopro.set.ai

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.StringSignature
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityPrivacyOcclusionBinding
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.entity.PrivacySettingParam
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gocam.goscamdemopro.utils.FileUtils
import com.gocam.goscamdemopro.view.PointInPathView.PRIVACY_OCCLUSION
import com.google.gson.Gson
import com.gos.platform.api.devparam.DevParam.DevParamCmdType.PrivacySetting
import kotlinx.coroutines.launch
import java.io.File

/**
 *
 * @Author wuzb
 * @Date 2025/09/02 09:24
 */
class PrivacyOcclusionActivity : BaseBindActivity<ActivityPrivacyOcclusionBinding>() {
    private lateinit var mDevice: Device
    private lateinit var mPrivacyParam: PrivacySettingParam

    override fun getLayoutId(): Int = R.layout.activity_privacy_occlusion

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")?: return
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        if (mDevice == null)
            finish()
        mBinding?.apply {
            pipv.setType(PRIVACY_OCCLUSION)
            val userName: String? = GApplication.app.user.userName
            val path: String = FileUtils.getDeviceSanpshot(userName, mDevice.devId)
            val file = File(path)
            val signature = if (!file.exists()) "" else file.lastModified().toString() + ""
            Glide.with(this@PrivacyOcclusionActivity).load(path)
                .placeholder(R.mipmap.ic_fg_device_item)
                .signature(StringSignature(signature))
                .error(R.mipmap.ic_fg_device_item)
                .into(ivImg)
            btnSelectAll.setOnClickListener {
                pipv.selectAllPath()
            }
            btnSave.setOnClickListener { save() }
            sivMoveSwitch.setOnCheckedChangeListener { v, isChecked ->
                mPrivacyParam.un_switch = if (isChecked) 1 else 0
                saveCap()
            }
        }
        getCap()
    }

    private fun getCap() {
        lifecycleScope.launch {
            val result = RemoteDataSource.getDeviceParam(PrivacySetting, deviceId = mDevice.devId)
            result.let {
                for (param in it) {
                    if (param.CMDType == PrivacySetting) {
                        val privacyParam = Gson().fromJson(param.DeviceParam, PrivacySettingParam::class.java)
                        initView(privacyParam)
                    }
                }
            }
        }
    }

    private fun initView(privacyParam: PrivacySettingParam) {
        mPrivacyParam = privacyParam
        mBinding?.apply {
            sivMoveSwitch.isChecked = mPrivacyParam.un_switch == 1
            pipv.initPath(mDevice.devId, mPrivacyParam)
        }
    }

    private fun save() {
        mBinding?.apply {
            mPrivacyParam = pipv.getPrivacyPoint(mPrivacyParam)
            saveCap()
        }
    }

    private fun saveCap() {
        val devParamArray = DevParamArray(
            PrivacySetting,
            mPrivacyParam
        )
        lifecycleScope.launch {
            val result = RemoteDataSource.setDeviceParam(devParamArray, deviceId = mDevice.devId)
            initView(mPrivacyParam)
        }
    }
}