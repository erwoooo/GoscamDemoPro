package com.gocam.goscamdemopro.baby

import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.lifecycleScope
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityPlayMusicBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.entity.Param
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gos.platform.api.contact.ResultCode
import com.gos.platform.device.inter.OnDevEventCallback
import com.gos.platform.device.result.DevResult
import com.gos.platform.device.result.DevResult.DevCmd
import com.gos.platform.device.result.GetMusicStatusResult
import kotlinx.coroutines.launch

/**
 *
 * @Author wuzb
 * @Date 2025/09/02 10:40
 */
class MusicPlayActivity : BaseBindActivity<ActivityPlayMusicBinding>(), OnDevEventCallback {
    private lateinit var mDevice: Device

    private val voiceInfos: ArrayList<Param> by lazy {
        ArrayList<Param>()
    }

    private var mMusic: Param? = null

    private var mPlayFlag = false

    override fun getLayoutId(): Int = R.layout.activity_play_music

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")?: return
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        if (mDevice == null)
            finish()
        mBinding?.apply {
            btnPlay.setOnClickListener {
                mMusic?.let {
                    play(it, if (mPlayFlag) 0 else 1)
                }?: play(voiceInfos[0], 1)
            }
            btnNext.setOnClickListener {
                mMusic?.let {
                    var index = voiceInfos.indexOf(it)
                    if (index == voiceInfos.size - 1) {
                        index = 0
                    } else {
                        index++
                    }
                    val param = voiceInfos[index]
                    play(param, 1)
                }?: play(voiceInfos[0], 1)
            }
            btnUp.setOnClickListener {
                mMusic?.let {
                    var index = voiceInfos.indexOf(it)
                    if (index == 0) {
                        index = voiceInfos.size - 1
                    } else {
                        index--
                    }
                    val param = voiceInfos[index]
                    play(param, 1)
                }?: play(voiceInfos[0], 1)
            }
        }
        mDevice.connection.addOnEventCallbackListener(this)
        getVoice()
    }

    private fun play(param: Param, flag: Int) {
        mMusic = param
        mDevice.connection.playMusic(
            0, flag, param.VoicePlayId, param.VoiceTimeLength,
            0, param.VoiceMD5, param.VoiceUrl
        )
    }

    private fun getVoice() {
        lifecycleScope.launch {
            val result = RemoteDataSource.getVoiceInfoList(mDevice.devId, 2)
            result?.VoiceList?.let {
                voiceInfos.addAll(it)
                mDevice.connection.getMusicStatus(0)
            }
        }
    }

    override fun onDevEvent(devId: String?, devResult: DevResult) {
        if (!TextUtils.equals(devId, mDevice.devId)) {
            return
        }

        val devCmd: DevCmd = devResult.devCmd
        val code: Int = devResult.getResponseCode()
        when (devCmd) {
            DevCmd.playMusicResult -> if (ResultCode.SUCCESS == code) {
                showToast("play success")
            }
            DevCmd.getMusicStatus -> if (ResultCode.SUCCESS == code) {
                val statusResult = devResult as GetMusicStatusResult
                for (param in voiceInfos) {
                    if (param.VoicePlayId == statusResult.id) {
                        mMusic = param
                        mPlayFlag = statusResult.playFlag == 1
                        return
                    }
                }
            }
        }
    }
}