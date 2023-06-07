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
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.databinding.ActivityPlayVideoBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gos.avplayer.GosMediaPlayer
import com.gos.avplayer.contact.BufferCacheType
import com.gos.avplayer.contact.DecType
import com.gos.avplayer.contact.RecEventType
import com.gos.avplayer.jni.AvPlayerCodec
import com.gos.avplayer.surface.GLFrameSurface
import com.gos.avplayer.surface.GlRenderer
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
import java.util.*

class PlayActivity : BaseBindActivity<ActivityPlayVideoBinding>(), OnDevEventCallback,
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

    companion object {

        fun startActivity(context: Context, devId: String) {
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra("dev", devId)
            context.startActivity(intent)
        }
    }

    inner class AudioHandler : Handler {
        constructor(looper: Looper) : super(looper)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
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
            if (FILE_TYPE == talkType) {
                fileTalkDataPath =
                    Environment.getExternalStorageDirectory().absolutePath + "/temp.g711"
                try {
                    fos = FileOutputStream(fileTalkDataPath)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }

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
                        } else {
                            try {
                                fos!!.write(g711Buf, 0, g711Buf.size)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            if (STREAM_TYPE == talkType) {
                mConnection!!.stopTalk(0)
            } else if (FILE_TYPE == talkType) {
                try {
                    fos!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mConnection!!.sendSpeakFile(0, fileTalkDataPath, 0)
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
            }

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
                    mBinding?.apply {
                        btnOpenStream.isEnabled = true
                        btnStartTalk.isEnabled = true
                        btnStartRecord.isEnabled = true
                        btnCapture.isEnabled = true
                        showToast("connect success")
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

    }

    /**
     * devId:  NO USE
     * avFrame: Audio & Video Data Streaming
     */
    override fun onVideoStream(devId: String?, avFrame: AvFrame?) {

    }

    override fun recCallBack(type: RecEventType?, data: Long, flag: Long) {

    }
}