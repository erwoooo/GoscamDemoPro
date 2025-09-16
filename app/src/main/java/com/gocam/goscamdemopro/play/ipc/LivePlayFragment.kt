package com.gocam.goscamdemopro.play.ipc

import android.Manifest
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.constraintlayout.widget.ConstraintLayout
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.baby.LifeReportActivity
import com.gocam.goscamdemopro.baby.MusicPlayActivity
import com.gocam.goscamdemopro.baby.TimeAlbumActivity
import com.gocam.goscamdemopro.base.BaseFragment
import com.gocam.goscamdemopro.databinding.ActivityIpcPlayVideoBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.play.GAudioTrack
import com.gocam.goscamdemopro.play.PlayViewModel
import com.gocam.goscamdemopro.play.TalkPlay
import com.gocam.goscamdemopro.talk.StreamTalkPlay
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gocam.goscamdemopro.utils.dbg
import com.gos.avplayer.GosMediaPlayer
import com.gos.avplayer.contact.BufferCacheType
import com.gos.avplayer.contact.DecType
import com.gos.avplayer.contact.RecEventType
import com.gos.avplayer.jni.AvPlayerCodec
import com.gos.avplayer.surface.GLFrameSurface
import com.gos.avplayer.surface.GlRenderer
import com.gos.platform.api.contact.DeviceType
import com.gos.platform.api.contact.ResultCode
import com.gos.platform.device.contact.ConnectStatus
import com.gos.platform.device.contact.Ptz
import com.gos.platform.device.contact.StreamType
import com.gos.platform.device.domain.AvFrame
import com.gos.platform.device.inter.IVideoPlay
import com.gos.platform.device.inter.OnDevEventCallback
import com.gos.platform.device.result.ConnectResult
import com.gos.platform.device.result.DevResult
import com.gos.platform.device.result.DevResult.DevCmd
import com.icare.echo.EchoCancel
import com.icare.echo.EchoUtil
import java.nio.ByteBuffer
import java.util.Date
import java.util.TimeZone

/**
 *
 * @Author wuzb
 * @Date 2025/09/12 17:45
 */
class LivePlayFragment : BaseFragment<ActivityIpcPlayVideoBinding, PlayViewModel>(), OnDevEventCallback,
    AvPlayerCodec.OnDecCallBack, AvPlayerCodec.OnRecCallBack, IVideoPlay {
    private val TAG = LivePlayFragment::class.simpleName
    var glFrameSurface: GLFrameSurface? = null
    var devId: String? = null
    val STREAM_TYPE = 11

    var talkType = STREAM_TYPE;
    var mDevice: Device? = null

    var mGlRenderer: GlRenderer? = null
    var mMediaPlayer: GosMediaPlayer? = null


    var sAudioRecord: AudioRecord? = null
    var sAudioTrack: GAudioTrack? = null


    var mTalkPlay: TalkPlay? = null

    var btnPtz:Button?=null
    var scrMenu:ScrollView?=null
    var constPzt:ConstraintLayout?=null
    var playAudio = true;
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun initFragmentView() {
        dbg.D(TAG, "initFragmentView")
        devId = activity?.intent?.getStringExtra("dev")
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        if (mDevice == null)
            activity?.finish()
        glFrameSurface = mBinding?.glSurface
        mBinding?.toolBar!!.backImg.setOnClickListener {
            activity?.finish()
        }
        glFrameSurface?.setEGLContextClientVersion(2)
        mGlRenderer = GlRenderer(glFrameSurface)
        glFrameSurface?.apply {
            setRenderer(mGlRenderer)
            setEnableZoom(true)
        }


        EchoUtil.isFullDuplex = true
        EchoUtil.volumeLevel(true, mDevice!!.deviceSfwVer)
        EchoCancel.setplayertospeaker(GApplication.app)
        EchoUtil.init(GApplication.app, mDevice!!.deviceSfwVer)


        mMediaPlayer = GosMediaPlayer().apply {
            getPort()
            setDecodeType(DecType.YUV420)
            setBufferSize(BufferCacheType.StreamCache, 60, 200 * 1024)
            start(100)
            setOnDecCallBack(this@LivePlayFragment)
            setOnRecCallBack(this@LivePlayFragment)
        }


        val sampleRate = 8000
        val channelConfig = AudioFormat.CHANNEL_OUT_MONO
        val channelRecordConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val mMinBuffSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat) * 4

        sAudioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            sampleRate,
            channelRecordConfig,
            audioFormat,
            mMinBuffSize
        )
        sAudioTrack = GAudioTrack(
            8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT
        )
        sAudioTrack?.play()


        mDevice?.connection?.addOnEventCallbackListener(this)
        mTalkPlay = object : StreamTalkPlay(0, mDevice?.connection, 1) {
            override fun onStartTalk(DevResult: DevResult?) {

            }
        }
        //已知设备类型
        (mTalkPlay as StreamTalkPlay).intiParams(TalkPlay.AUDIO_G711A, 8000)


        if (mDevice?.connection?.isConnected == true) {
            mBinding?.apply {
                btnOpenStream.isEnabled = true
                btnStartTalk.isEnabled = true
                btnStartRecord.isEnabled = true
                btnCapture.isEnabled = true
                btnAudio.isEnabled = true
            }
        }

        btnPtz = mBinding?.btnPzt
        scrMenu = mBinding?.svMenu
        constPzt = mBinding?.constrainPzt
        mBinding?.btnPzt?.apply {
            setOnClickListener {
                scrMenu?.visibility = View.GONE
                constPzt?.visibility = View.VISIBLE
            }
        }

        mBinding?.tvBlack?.apply {
            setOnClickListener {
                scrMenu?.visibility = View.VISIBLE
                constPzt?.visibility = View.GONE
            }
        }

        mBinding?.btnOpenStream?.apply {
            setOnClickListener {
                startVideo()
            }
        }


        mBinding?.btnStartTalk?.apply {
            setOnClickListener {
                mDevice?.connection?.let {
                    if (it.isConnected) {
                        EchoUtil.start()
                        (mTalkPlay as StreamTalkPlay).startTalk()
                        mBinding?.btnStopTalk?.isEnabled = true
                    }
                }
            }
        }

        mBinding?.btnStopTalk?.apply {
            setOnClickListener {
                (mTalkPlay as StreamTalkPlay).stopTalk()

            }
        }

        mBinding?.btnCloseStream?.apply {
            setOnClickListener {
                EchoUtil.destroy(requireContext())
                mDevice?.connection!!.stopVideo(0, this@LivePlayFragment)
            }
        }
        mBinding?.btnStartRecord?.setOnClickListener {
            mBinding?.btnStartRecord?.isEnabled = false
            mBinding?.btnStopRecord?.isEnabled = true

            val recordPath =
                Environment.getExternalStorageDirectory().absolutePath + "/" + System.currentTimeMillis() + ".mp4"
            mMediaPlayer!!.startRecord(recordPath, 0)
        }

        mBinding?.btnCapture?.setOnClickListener {
            val capturePath =
                Environment.getExternalStorageDirectory().absolutePath + "/" + System.currentTimeMillis() + ".jpg"
            mMediaPlayer!!.capture(capturePath)
            showToast("pic save path : $capturePath")
            dbg.D("PlayActivity", capturePath)
        }

        mBinding?.btnAudio?.setOnClickListener {
            playAudio = !playAudio
            if (playAudio) {
                mDevice?.connection?.startAudio(0)
            } else {
                mDevice?.connection?.stopAudio(0)
            }
        }

        mBinding.apply {
            btnMusic.visibility = if (mDevice?.devCap?.isPeripheral == true) View.VISIBLE else View.GONE
            btnLife.visibility = if (mDevice?.devCap?.isPeripheral == true) View.VISIBLE else View.GONE
            btnAlbum.visibility = if (mDevice?.devCap?.isPeripheral == true) View.VISIBLE else View.GONE
            btnMusic.setOnClickListener {
                val intent = Intent(requireContext(), MusicPlayActivity::class.java)
                intent.putExtra("dev", mDevice?.devId)
                requireContext().startActivity(intent)
            }
            btnLife.setOnClickListener {
                val intent = Intent(requireContext(), LifeReportActivity::class.java)
                intent.putExtra("dev", mDevice?.devId)
                requireContext().startActivity(intent)
            }
            btnAlbum.setOnClickListener {
                val intent = Intent(requireContext(), TimeAlbumActivity::class.java)
                intent.putExtra("dev", mDevice?.devId)
                requireContext().startActivity(intent)
            }
            ivPztLeft.setOnClickListener {
                mViewModel?.operatePzt(mDevice!!.devId,Ptz.PTZ_LEFT)
                /**
                 *The hold event sends the PTZ_KEEP_LEFT instruction
                 *Send the PTZ_STOP command when stopped
                 */
            }

            ivPztDown.setOnClickListener {
                mViewModel?.operatePzt(mDevice!!.devId,Ptz.PTZ_DOWN)
                /**
                 *The hold event sends the PTZ_KEEP_DOWN instruction
                 *Send the PTZ_STOP command when stopped
                 */
            }

            ivPztRight.setOnClickListener {
                mViewModel?.operatePzt(mDevice!!.devId,Ptz.PTZ_RIGHT)
                /**
                 *The hold event sends the PTZ_KEEP_RIGHT instruction
                 *Send the PTZ_STOP command when stopped
                 */
            }

            ivPztUp.setOnClickListener {

                mViewModel?.operatePzt(mDevice!!.devId,Ptz.PTZ_UP)
                /**
                 *The hold event sends the PTZ_KEEP_UP instruction
                 *Send the PTZ_STOP command when stopped
                 */
            }
        }



        mViewModel?.mDeviceOnline?.observe(this) {
            mDevice?.connection?.setPlatDevOnline(it)
            mDevice?.doConnect()
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_ipc_play_video

    var isOnStop = true
    override fun onStart() {
        super.onStart()
        dbg.D(TAG, "onStart")
    }
    override fun onResume() {
        super.onResume()
        dbg.D(TAG, "onResume")
//        if (mDevice != null &&
//            (mDevice!!.devType == DeviceType.DOOR_BELL || mDevice!!.devType == DeviceType.BATTERY_IPC)
//        ) {
//            mViewModel.handleDoorbellWakeup(devId!!)
//        }
//        if (isOnStop) {
//            startVideo()
//            isOnStop = false
//        }
    }

    override fun onPause() {
        super.onPause()
        dbg.D(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        dbg.D(TAG, "onStop")
//        isOnStop = true
//        EchoUtil.destroy(requireContext())
//        mDevice?.connection!!.stopVideo(0, this@LivePlayFragment)
    }

    override fun onDevEvent(
        devId: String?,
        devResult: DevResult
    ) {
        if (!TextUtils.equals(devId, mDevice!!.devId)) {
            return
        }

        val devCmd: DevCmd = devResult.getDevCmd()
        val code: Int = devResult.getResponseCode()
        when (devCmd) {
            DevCmd.connect -> {
                val connectResult = devResult as ConnectResult
                if (connectResult.connectStatus == ConnectStatus.CONNECT_SUCCESS) {
                    showToast("connect success")
                    mBinding.apply {
                        btnOpenStream.isEnabled = true
                        btnStartTalk.isEnabled = true
                        btnStartRecord.isEnabled = true
                        btnCapture.isEnabled = true
                        btnAudio.isEnabled = true
                    }
                }
            }
            DevCmd.startVideo -> if (ResultCode.SUCCESS == code) {
                mBinding?.btnCloseStream?.isEnabled = true
                showToast("start video success")
            }
            DevCmd.stopVideo -> {
            }
            DevCmd.startTalk -> if (ResultCode.SUCCESS == code) {
                showToast("start talk success")
            } else {
                mDevice?.connection!!.stopTalk(0)
            }
            DevCmd.sendSpeakFile -> mDevice?.connection!!.stopTalk(0)
            DevCmd.stopTalk -> {
            }
        }
    }

    private fun startVideo() {
        if (mDevice?.connection?.isConnected == true) {
            mBinding?.btnStartRecord?.isEnabled = true
            mBinding?.btnCapture?.isEnabled = true
            val timestamp = (System.currentTimeMillis() / 1000L).toInt()
            var timezone = (TimeZone.getDefault().rawOffset / 3600000 + 24).toInt()
            if (TimeZone.getDefault().inDaylightTime(Date())) {
                timezone++
            }
            mDevice?.connection?.startVideo(
                0,
                StreamType.VIDEO_AUDIO,
                mDevice?.streamPsw,
                timestamp,
                timezone,
                this@LivePlayFragment
            )
        }
    }

    override fun decCallBack(
        type: DecType?,
        data: ByteArray?,
        dataSize: Int,
        width: Int,
        height: Int,
        rate: Int,
        ch: Int,
        flag: Int,
        frameNo: Int,
        aiInfo: String?
    ) {
        if (DecType.YUV420 == type) {
            val buf = ByteBuffer.wrap(data)
            mGlRenderer!!.update(buf, width, height)
        } else if (DecType.AUDIO == type) {
            val t = ByteArray(dataSize)
            System.arraycopy(data, 0, t, 0, dataSize)
            if (playAudio) {
                sAudioTrack?.write(t, t.size)
            }

        }
    }

    /**
     * devId:  NO USE
     * avFrame: Audio & Video Data Streaming
     */
    override fun onVideoStream(devId: String?, avFrame: AvFrame?) {
        val temp = ByteArray(avFrame!!.data.size)
        System.arraycopy(avFrame.data, 0, temp, 0, avFrame.data.size)
        mMediaPlayer!!.putFrame(temp, temp.size, 1)
    }

    override fun recCallBack(type: RecEventType?, data: Long, flag: Long) {

    }

    override fun onDestroy() {
        super.onDestroy()
        dbg.D(TAG, "onDestroy")
        mDevice?.connection!!.removeOnEventCallbackListener(this)
        mGlRenderer!!.stopDisplay()
        mGlRenderer!!.release()

        mMediaPlayer!!.stop()
        mMediaPlayer!!.releasePort()
        mMediaPlayer!!.setOnDecCallBack(this)
        mMediaPlayer!!.setOnRecCallBack(this)

        mDevice?.connection!!.stopTalk(0)
        mDevice?.connection!!.stopVideo(0, this)

        sAudioRecord!!.stop()
        sAudioRecord!!.release()
        sAudioTrack!!.stop()
        sAudioTrack!!.release()
    }

    private fun showToast(msg: CharSequence?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}