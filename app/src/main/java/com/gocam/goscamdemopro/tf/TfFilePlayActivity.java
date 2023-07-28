package com.gocam.goscamdemopro.tf;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gocam.goscamdemopro.R;
import com.gocam.goscamdemopro.base.BaseActivity;
import com.gocam.goscamdemopro.base.BaseBindActivity;
import com.gocam.goscamdemopro.databinding.ActivityTfFilePlayBinding;
import com.gocam.goscamdemopro.entity.Device;
import com.gocam.goscamdemopro.utils.DeviceManager;
import com.gocam.goscamdemopro.utils.Packet;
import com.gos.avplayer.GosMediaPlayer;
import com.gos.avplayer.contact.BufferCacheType;
import com.gos.avplayer.contact.DecType;
import com.gos.avplayer.contact.RecEventType;
import com.gos.avplayer.jni.AvPlayerCodec;
import com.gos.avplayer.surface.GLFrameSurface;
import com.gos.avplayer.surface.GlRenderer;
import com.gos.platform.device.contact.StreamType;
import com.gos.platform.device.domain.AvFrame;
import com.gos.platform.device.domain.StRecordInfo;
import com.gos.platform.device.inter.IVideoPlay;
import com.gos.platform.device.inter.OnDevEventCallback;
import com.gos.platform.device.result.DevResult;


import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TfFilePlayActivity extends BaseBindActivity<ActivityTfFilePlayBinding> implements OnDevEventCallback, AvPlayerCodec.OnDecCallBack, AvPlayerCodec.OnRecCallBack {
    TextView mTvTitle;
    String mDevId;
    Device mDevice;
    GosMediaPlayer mMediaPlayer;
    int mStartTime;
    int mEndTime;
    ImageView ivBack;

    public static void startActivity(Activity activity, String deviceId, StRecordInfo info){
        Intent intent = new Intent(activity, TfFilePlayActivity.class);
        intent.putExtra("DEV_ID", deviceId);
        intent.putExtra("START_TIME", Integer.parseInt(info.startTimeStamp));
        intent.putExtra("END_TIME", Integer.parseInt(info.endTimeStamp));
        activity.startActivity(intent);
    }


    HandlerThread audioHandlerThread;
    AudioTrack sAudioTrack;
    AudioHandler sAudioHandler;

    @Override
    public int getLayoutId() {
        return R.layout.activity_tf_file_play;
    }

    @Override
    public void onCreateData(@Nullable Bundle bundle) {
        mTvTitle = findViewById(R.id.text_title);
        mTvTitle.setText(R.string.file_play);

        GLFrameSurface gl = findViewById(R.id.glsurface);
        gl.setEGLContextClientVersion(2);
        glRenderer = new GlRenderer(gl);
        gl.setRenderer(glRenderer);
        ivBack = findViewById(R.id.back_img);
        ivBack.setOnClickListener(v->{
            finish();
        });
        mStartTime = getIntent().getIntExtra("START_TIME",0);
        mEndTime = getIntent().getIntExtra("END_TIME",0);
        mDevId = getIntent().getStringExtra("DEV_ID");
        mDevice = DeviceManager.getInstance().findDeviceById(mDevId);
        mDevice.getConnection().addOnEventCallbackListener(this);
    }

    class AudioHandler extends Handler {
        public AudioHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] data = (byte[]) msg.obj;
            if(sAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
                sAudioTrack.write(data,0,data.length);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDevice.getConnection().removeOnEventCallbackListener(this);

        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.releasePort();
        }

        if(glRenderer != null){
            try {
                glRenderer.stopDisplay();
                glRenderer = null;
            }catch (Exception e){}
        }

        if(sAudioHandler != null){
            sAudioHandler.removeCallbacksAndMessages(null);
        }
        if(audioHandlerThread != null){
            audioHandlerThread.quit();
        }
        if(sAudioTrack != null){
            try {
                sAudioTrack.stop();
            }catch (Exception e){}
            try {
                sAudioTrack.release();
            }catch (Exception e){}
            sAudioTrack = null;
        }
    }

    @Override
    public void onDevEvent(String s, DevResult devResult) {

    }

    IVideoPlay iVideoPlay;
    String filePath;
    //方式1：下载文件转成MP4文件
    boolean isDownloadInit;
    public void startDownload(View v){
        if(!isDownloadInit) {
            findViewById(R.id.btn_play).setEnabled(false);
            findViewById(R.id.btn_stop).setEnabled(false);
            findViewById(R.id.btn_thumbnail).setEnabled(false);

            isDownloadInit = true;
            mMediaPlayer = new GosMediaPlayer();
            mMediaPlayer.setOnDecCallBack(this);
            mMediaPlayer.setOnRecCallBack(this);
            mMediaPlayer.getPort();
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mStartTime + ".mp4";
            mMediaPlayer.setFilePath(1, filePath, 0);
            mMediaPlayer.start(100);

            iVideoPlay = new IVideoPlay() {
                @Override
                public void onVideoStream(String s, AvFrame avFrame) {
                    Log.d("onStreamCallback","retVal="+retVal);
                    mMediaPlayer.putFrame(avFrame.data,avFrame.dataLen,0);
                }
            };
        }

        int timestamp = (int) (System.currentTimeMillis() / 1000L);// IPC 为unsigned int, so +24, timezone > 0;
        int timezone = (TimeZone.getDefault().getRawOffset() / 3600000) + 24;// on the IPC side, -24,
        if(TimeZone.getDefault().inDaylightTime(new Date())){
            timezone++;
        }
        mDevice.getConnection().startVideo(0, StreamType.STREAM_REC, "", timestamp, timezone, iVideoPlay);

        int startTime = mStartTime;
        int dur = mEndTime - startTime;
        mDevice.getConnection().setLocalStoreCfg(0, mStartTime, 2, dur, "");
    }


    //方式2：类似实时播放方式，播放TF文件
    boolean playInit;
    GlRenderer glRenderer;
    TextView tvTime;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long retVal;
    public void startPlay(View v){
        if(!playInit){
            playInit = true;
            tvTime = findViewById(R.id.tv_time);
            findViewById(R.id.btn_download).setEnabled(false);
            findViewById(R.id.btn_thumbnail).setEnabled(false);

            int sampleRate = 8000;
            int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
            int nMinBufSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
            sAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    nMinBufSize, AudioTrack.MODE_STREAM);
            sAudioTrack.play();
            audioHandlerThread = new HandlerThread("audio");
            audioHandlerThread.start();
            sAudioHandler = new AudioHandler(audioHandlerThread.getLooper());

            mMediaPlayer = new GosMediaPlayer();
            mMediaPlayer.setOnDecCallBack(this);
            mMediaPlayer.setOnRecCallBack(this);
            mMediaPlayer.getPort();
            mMediaPlayer.setDecodeType(DecType.YUV420);
            mMediaPlayer.setBufferSize(BufferCacheType.StreamCache, 200, 200 * 1024);
            mMediaPlayer.setFilePath(2, "", 0);
            mMediaPlayer.start(100);

            iVideoPlay = new IVideoPlay() {
                @Override
                public void onVideoStream(String s, AvFrame avFrame) {
                    retVal = mMediaPlayer.putFrame(avFrame.data,avFrame.dataLen,1);
                    Log.d("onStreamCallback","retVal="+retVal);
                    int nFrameType = Packet.byteArrayToInt_Little(avFrame.data, 4);//对应这个帧类型102
                    final int nTimestamp = Packet.byteArrayToInt_Little(avFrame.data, 16);//对应视频时间戳
                    int nDataSize = Packet.byteArrayToInt_Little(avFrame.data, 28);//数据长度
                    Log.e("videoPlay", "onVideoStream: nFrameType= " + nFrameType );
                    if (retVal == -20) {
                        mDevice.getConnection().pasueRecvStream(0, true);
                    }
                }
            };
        }

        int timestamp = (int) (System.currentTimeMillis() / 1000L);// IPC 为unsigned int, so +24, timezone > 0;
        int timezone = (TimeZone.getDefault().getRawOffset() / 3600000) + 24;// on the IPC side, -24,
        if(TimeZone.getDefault().inDaylightTime(new Date())){
            timezone++;
        }
        mDevice.getConnection().startVideo(0, StreamType.STREAM_REC, "", timestamp, timezone, iVideoPlay);
        mDevice.getConnection().setLocalStoreCfg(0, mStartTime, 1, 0, "");
    }

    public void stopPlay(View v){
        mDevice.getConnection().setLocalStoreStop(0);
        mDevice.getConnection().stopVideo(0, iVideoPlay);
    }

    public void thumbnail(View view){
        findViewById(R.id.btn_download).setEnabled(false);
        findViewById(R.id.btn_play).setEnabled(false);
        findViewById(R.id.btn_stop).setEnabled(false);

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mStartTime+".jpg";
        Log.d("thumbnail","filePath="+filePath);
        if(mMediaPlayer == null){
            mMediaPlayer = new GosMediaPlayer();
            mMediaPlayer.setOnDecCallBack(this);
            mMediaPlayer.getPort();
            mMediaPlayer.start(101);

            iVideoPlay = new IVideoPlay() {
                @Override
                public void onVideoStream(String s, AvFrame avFrame) {
                    int nFrameType = Packet.byteArrayToInt_Little(avFrame.data, 4);//对应这个帧类型102
                    final int nTimestamp = Packet.byteArrayToInt_Little(avFrame.data, 16);//对应视频时间戳
                    int nDataSize = Packet.byteArrayToInt_Little(avFrame.data, 28);//数据长度
                    Log.e("videoPlay 273", "onVideoStream: nFrameType= " + nFrameType );
                    mMediaPlayer.putFrame(avFrame.data,avFrame.dataLen,1);
                }
            };
        }
        mMediaPlayer.setFilePath(0, filePath, 0);

        int timestamp = (int) (System.currentTimeMillis() / 1000L);// IPC 为unsigned int, so +24, timezone > 0;
        int timezone = (TimeZone.getDefault().getRawOffset() / 3600000) + 24;// on the IPC side, -24,
        if(TimeZone.getDefault().inDaylightTime(new Date())){
            timezone++;
        }
        mDevice.getConnection().startVideo(0, StreamType.STREAM_REC, "", timestamp, timezone, iVideoPlay);
        mDevice.getConnection().setLocalStoreCfg(0, mStartTime, 0, 0, "");
    }

    @Override
    public void recCallBack(final RecEventType type, final long data, long flag) {
        Log.d("recCallBack","type="+ type+",data:"+data+","+dateFormat.format(new Date(data*1000)));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //流播时时间显示
                if(RecEventType.AVRetPlayRecTime == type){
                    tvTime.setText(dateFormat.format(new Date(data*1000)));
                }
            }
        });
    }

    @Override
    public void decCallBack(DecType type, byte[] data, int dataSize, int width, int height, int rate, int ch, int flag, int frameNo, String aiInfo) {
        if(DecType.YUV420 == type){
            Log.d("P2pDownload_decCallBack",type+":"+dataSize);
            ByteBuffer buf = ByteBuffer.wrap(data);
            glRenderer.update(buf ,width, height);

        }else if(DecType.AUDIO == type){
            byte[] buf = new byte[data.length];
            System.arraycopy(data, 0, buf, 0, data.length);
            sAudioHandler.obtainMessage(0, buf).sendToTarget();

        }if (DecType.TF_CACHE_BUF_IDLE == type) {//用于调节速度
            Log.d("P2pDownload_decCallBack",type+":"+dataSize);
            mDevice.getConnection().pasueRecvStream(0,false);

        } else if (DecType.TF_CACHE_BUF_FULL == type) {
            Log.d("P2pDownload_decCallBack",type+":"+dataSize);
            mDevice.getConnection().pasueRecvStream(0, true);

        }else if(DecType.TF_RECORD_FINISH == type && playInit){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("Play end");
                }
            });
        }else if(DecType.TF_CAPTURE_FINISH == type){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("Pic save:"+filePath);
                }
            });
        }

        if(type != DecType.TF_CACHE_BUF_IDLE){
            Log.d("DecCallBack",type+"");
        }

        //下载完成
        if (type == DecType.TF_RECORD_FINISH && isDownloadInit) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("P2pDownload","filePath:"+filePath);
                    showToast("Download success, " + filePath);
                }
            });
        }
    }
}
