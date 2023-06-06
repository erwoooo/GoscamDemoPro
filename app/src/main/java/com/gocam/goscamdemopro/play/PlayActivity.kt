package com.gocam.goscamdemopro.play

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.*
import android.os.*
import android.widget.Button

import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.databinding.ActivityPlayVideoBinding
import com.gocam.goscamdemopro.entity.Device
import com.gos.avplayer.GosMediaPlayer
import com.gos.avplayer.contact.BufferCacheType
import com.gos.avplayer.contact.DecType
import com.gos.avplayer.contact.RecEventType
import com.gos.avplayer.jni.AvPlayerCodec
import com.gos.avplayer.surface.GLFrameSurface
import com.gos.avplayer.surface.GlRenderer
import com.gos.platform.device.base.Connection
import com.gos.platform.device.contact.StreamType
import com.gos.platform.device.domain.AvFrame
import com.gos.platform.device.inter.IVideoPlay
import com.gos.platform.device.inter.OnDevEventCallback
import com.gos.platform.device.result.DevResult
import java.util.*

class PlayActivity : BaseBindActivity<ActivityPlayVideoBinding>(), OnDevEventCallback,
    AvPlayerCodec.OnDecCallBack, AvPlayerCodec.OnRecCallBack, IVideoPlay {
    override fun getLayoutId(): Int = R.layout.activity_play_video
    var glFrameSurface: GLFrameSurface? = null
    var btnconnect: Button? = null
    var btnstartvideo: Button? = null
    var btnstopvideo: Button? = null
    var btnstarttalk: Button? = null
    var btnstoptalk: Button? = null
    var btnstartrecord: Button? = null
    var btnstoprecord: Button? = null
    var btncapture: Button? = null
    var devId: String? = null

    val FILE_TYPE = 10
    val STREAM_TYPE = 11
    var talkType = FILE_TYPE
    //int talkType = STREAM_TYPE;

    //int talkType = STREAM_TYPE;
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

    inner class AudioHandler : Handler{
        constructor(looper: Looper):super(looper)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val data = msg.obj as ByteArray
            sAudioTrack?.apply {
                if (playState != AudioTrack.PLAYSTATE_PLAYING){
                    play()
                }
                write(data,0,data.size)
            }
        }
    }

    inner class RecordHandler : Handler{
        var isStartRecord:Boolean = false
        constructor(looper: Looper):super(looper)
    }

    @SuppressLint("MissingPermission")
    override fun onCreateData(bundle: Bundle?) {
//        devId = intent.getStringExtra("dev")
        mBinding?.apply {
            glFrameSurface = glSurface
            btnconnect = btnConnect
            btnstartvideo = btnOpenStream
            btnstopvideo = btnCloseStream
            btnstarttalk = btnStartTalk
            btnstoptalk = btnStopTalk
            btnstartrecord = btnStartRecord
            btnstoprecord = btnStopRecord
            btncapture = btnCapture
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

        btnconnect?.setOnClickListener {
            mConnection?.let {
                if (it.isConnected) {
                    btnstartvideo?.isEnabled = true
                    btnstarttalk?.isEnabled = true
                } else {
                    it.connect(0)
                }

            }
        }

        btnstartvideo?.setOnClickListener {
            if (mConnection?.isConnected == true) {
                btnstartrecord?.isEnabled = true
                btncapture?.isEnabled = true
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
                    this
                )
            }
        }

        btnstarttalk?.setOnClickListener {
            mConnection?.let {
                if (it.isConnected) {
                    it.startTalk(0)
                    btnstoptalk?.isEnabled = true
                }
            }
        }

        btnstoptalk?.setOnClickListener {
            mConnection?.let {

            }
        }

        btnEnable(false)
    }


    private fun btnEnable(enable: Boolean) {
        btnstartvideo?.isEnabled = enable
        btnstopvideo?.isEnabled = enable
        btnstartvideo?.isEnabled = enable
        btnstarttalk?.isEnabled = enable
        btnstoptalk?.isEnabled = enable
        btnstartrecord?.isEnabled = enable
        btnstoprecord?.isEnabled = enable
        btncapture?.isEnabled = enable
    }

    override fun onDevEvent(devId: String?, baseResult: DevResult?) {

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