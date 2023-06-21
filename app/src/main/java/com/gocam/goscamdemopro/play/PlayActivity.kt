package com.gocam.goscamdemopro.play

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.*
import android.os.*
import android.text.TextUtils
import android.widget.Button

import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.databinding.ActivityPlayVideoBinding
import com.gocam.goscamdemopro.entity.Device
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
import com.gos.platform.device.base.Connection
import com.gos.platform.device.contact.ConnectStatus
import com.gos.platform.device.contact.StreamType
import com.gos.platform.device.domain.AvFrame
import com.gos.platform.device.inter.IVideoPlay
import com.gos.platform.device.inter.OnDevEventCallback
import com.gos.platform.device.result.ConnectResult
import com.gos.platform.device.result.DevResult
import com.gos.platform.device.result.DevResult.DevCmd
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.*

class PlayActivity : BaseActivity<ActivityPlayVideoBinding,PlayViewModel>(), OnDevEventCallback,
    AvPlayerCodec.OnDecCallBack, AvPlayerCodec.OnRecCallBack, IVideoPlay {
    override fun getLayoutId(): Int = R.layout.activity_play_video
    var glFrameSurface: GLFrameSurface? = null
    var devId: String? = null

    val FILE_TYPE = 10
    val STREAM_TYPE = 11

    //    var talkType = FILE_TYPE
    var talkType = STREAM_TYPE;

    //var talkType = STREAM_TYPE;
    var mDevice: Device? = null
    var mConnection: Connection? = null
    var mGlRenderer: GlRenderer? = null
    var mMediaPlayer: GosMediaPlayer? = null
    var recordHandlerThread: HandlerThread? = null
    var audioHandlerThread: HandlerThread? = null
    var sAudioRecord: AudioRecord? = null
    var sAudioTrack: AudioTrack? = null
    var sRecordHandler: RecordHandler? = null

    var sAudioHandler: AudioHandler? = null

    companion object {

        fun startActivity(context: Context, devId: String) {
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra("dev", devId)
            context.startActivity(intent)
        }
    }

    var playAudio = true;
    inner class AudioHandler : Handler {
        constructor(looper: Looper) : super(looper)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (!playAudio)
                return
            val data = msg.obj as ByteArray
            sAudioTrack?.apply {
                if (playState != AudioTrack.PLAYSTATE_PLAYING) {
                    play()
                }
                write(data, 0, data.size)
            }
        }
    }

    inner class RecordHandler : Handler {
        var isStartRecord: Boolean = false

        constructor(looper: Looper) : super(looper)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val audioData = ByteArray(640)
            if (sAudioRecord!!.state == AudioRecord.STATE_INITIALIZED) {
                sAudioRecord!!.startRecording()
            }

            var fileTalkDataPath: String? = null
            var fos: FileOutputStream? = null
            while (isStartRecord) {
                val size = sAudioRecord!!.read(audioData, 0, audioData.size)
                if (size == audioData.size) {
                    val g711Buf = ByteArray(320)
                    val len = AvPlayerCodec.nativeEncodePCMtoG711A(
                        8000,
                        1,
                        audioData,
                        audioData.size,
                        g711Buf
                    )
                    if (len > 0) {
                        if (STREAM_TYPE == talkType) {
                            mConnection!!.sendTalkData(0, 53, 8000, 0, g711Buf, g711Buf.size)
                        }
                    }
                }
            }

            if (STREAM_TYPE == talkType) {
                mConnection!!.stopTalk(0)
            }
        }

    }

    @SuppressLint("MissingPermission")
    override fun onCreateData(bundle: Bundle?) {
        devId = intent.getStringExtra("dev")
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        if (mDevice == null)
            finish()
        glFrameSurface = mBinding?.glSurface
        mBinding?.toolBar!!.backImg.setOnClickListener {
            finish()
        }
        glFrameSurface?.setEGLContextClientVersion(2)
        mGlRenderer = GlRenderer(glFrameSurface)
        glFrameSurface?.apply {
            setRenderer(mGlRenderer)
            setEnableZoom(true)
        }

        mMediaPlayer = GosMediaPlayer().apply {
            getPort()
            setDecodeType(DecType.YUV420)
            setBufferSize(BufferCacheType.StreamCache, 60, 200 * 1024)
            start(100)
            setOnDecCallBack(this@PlayActivity)
            setOnRecCallBack(this@PlayActivity)
        }

        val sampleRate = 8000
        val channelConfig = AudioFormat.CHANNEL_OUT_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val mMinBuffSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        sAudioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            audioFormat,
            mMinBuffSize
        )
        sAudioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            mMinBuffSize,
            AudioTrack.MODE_STREAM,
            sAudioRecord!!.audioSessionId
        )

        mConnection = mDevice?.connection
        mConnection?.addOnEventCallbackListener(this)

        recordHandlerThread = HandlerThread("record")
        recordHandlerThread!!.start()
        sRecordHandler = RecordHandler(recordHandlerThread!!.looper)
        audioHandlerThread = HandlerThread("audio")
        audioHandlerThread!!.start()
        sAudioHandler = AudioHandler(audioHandlerThread!!.looper)

        mBinding?.btnConnect?.apply {
            setOnClickListener {
                mConnection?.let {
                    if (it.isConnected) {
                        mBinding?.btnStartTalk?.isEnabled = true
                        mBinding?.btnStopTalk?.isEnabled = true
                    } else {
                        it.connect(0)
                    }
                }

                mBinding?.btnOpenStream?.apply {
                    setOnClickListener {
                        if (mConnection?.isConnected == true) {
                            mBinding?.btnStartRecord?.isEnabled = true
                            mBinding?.btnCapture?.isEnabled = true
                            val timestamp = (System.currentTimeMillis() / 1000L).toInt()
                            var timezone = (TimeZone.getDefault().rawOffset / 3600000 + 24).toInt()
                            if (TimeZone.getDefault().inDaylightTime(Date())) {
                                timezone++
                            }
                            mConnection?.startVideo(
                                0,
                                StreamType.VIDEO_AUDIO,
                                mDevice?.streamPsw,
                                timestamp,
                                timezone,
                                this@PlayActivity
                            )
                        }
                    }
                }


                mBinding?.btnStartTalk?.apply {
                    setOnClickListener {
                        mConnection?.let {
                            if (it.isConnected) {
                                it.startTalk(0, mDevice?.streamPsw)
                                mBinding?.btnStopTalk?.isEnabled = true
                            }
                        }
                    }
                }

                mBinding?.btnStopTalk?.apply {
                    setOnClickListener {
                        sRecordHandler!!.isStartRecord = false
                    }
                }

                mBinding?.btnCloseStream.apply {
                    setOnClickListener {
                        mConnection!!.stopVideo(0, this@PlayActivity)
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
                    showLToast("pic save path : $capturePath")
                    dbg.D("PlayActivity", capturePath)
                }

                mBinding?.btnAudio?.setOnClickListener {
                    playAudio = !playAudio
                    if(playAudio){
                        mDevice?.connection?.startAudio(0)
                    }else{
                        mDevice?.connection?.stopAudio(0)
                    }
                }
            }

        }

        mViewModel?.mDeviceOnline?.observe(this){
            mDevice?.setOnline(it)
            mConnection?.setPlatDevOnline(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mDevice != null &&
            (mDevice!!.devType == DeviceType.DOOR_BELL || mDevice!!.devType == DeviceType.BATTERY_IPC)
        ) {
            mViewModel.handleDoorbellWakeup(devId!!)
        }
    }

    override fun onDevEvent(devId: String?, devResult: DevResult) {
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
                    mBinding?.apply {
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
                sRecordHandler!!.isStartRecord = true
                sRecordHandler!!.sendEmptyMessage(0)
                showToast("start talk success")
            } else {
                mConnection!!.stopTalk(0)
            }
            DevCmd.sendSpeakFile -> mConnection!!.stopTalk(0)
            DevCmd.stopTalk -> {
            }
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
            val obtain = Message.obtain()
            obtain.obj = t
            sAudioHandler?.sendMessage(obtain)
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
        mConnection!!.removeOnEventCallbackListener(this)
        mGlRenderer!!.stopDisplay()
        mGlRenderer!!.release()

        mMediaPlayer!!.stop()
        mMediaPlayer!!.releasePort()
        mMediaPlayer!!.setOnDecCallBack(this)
        mMediaPlayer!!.setOnRecCallBack(this)

        recordHandlerThread!!.quitSafely()
        audioHandlerThread!!.quitSafely()
        sAudioHandler!!.removeCallbacksAndMessages(null)
        mConnection!!.stopTalk(0)
        mConnection!!.stopVideo(0, this)

        sAudioRecord!!.stop()
        sAudioRecord!!.release()
        sAudioTrack!!.stop()
        sAudioTrack!!.release()
    }
}