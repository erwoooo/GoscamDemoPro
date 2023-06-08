package com.gocam.goscamdemopro.cloud;

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

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.gocam.goscamdemopro.GApplication;
import com.gocam.goscamdemopro.R;
import com.gocam.goscamdemopro.base.BaseBindActivity;
import com.gocam.goscamdemopro.cloud.data.GosCloud;
import com.gocam.goscamdemopro.cloud.data.entity.CameraEvent;
import com.gocam.goscamdemopro.cloud.data.entity.CloudPlayInfo;
import com.gocam.goscamdemopro.cloud.data.entity.OssInfo;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudOssInfoResult;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudPlayFileListResult;
import com.gocam.goscamdemopro.databinding.ActivityCloudFilePlayBinding;
import com.gocam.goscamdemopro.entity.User;
import com.gocam.goscamdemopro.utils.dbg;
import com.gos.avplayer.GosMediaPlayer;
import com.gos.avplayer.contact.DecType;
import com.gos.avplayer.contact.RecEventType;
import com.gos.avplayer.jni.AvPlayerCodec;
import com.gos.avplayer.surface.GLFrameSurface;
import com.gos.avplayer.surface.GlRenderer;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.inter.OnPlatformEventCallback;
import com.gos.platform.api.result.PlatResult;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CloudFilePlayActivity extends BaseBindActivity<ActivityCloudFilePlayBinding> implements OnPlatformEventCallback, AvPlayerCodec.OnRecCallBack, AvPlayerCodec.OnDecCallBack {
    GosMediaPlayer mMediaPlayer;
    GlRenderer mGlRenderer;
    AudioTrack mAudioTrack;
    String mDeviceId;
    boolean playInit;
    OSSClient mOssClient;
    User mUser;

    final String Tag = "CloudFilePlayActivity";
    HandlerThread mDownloadThread;
    DownloadHandler mDownloadHandler;
    HandlerThread mPreviewThread;
    PreviewHandler mPreviewHandler;
    HandlerThread mPlayThread;
    PlayHandler mPlayHandler;
    HandlerThread mAudioHandlerThread;
    AudioHandler mAudioHandler;

    static CameraEvent mCameraEvent;
    boolean isMediaPlayerStarted;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat();

    TextView mTvTitle;
    TextView mTvTime;

    public static void startActivity(Activity activity, String deviceId, CameraEvent cameraEvent){
        Intent intent = new Intent(activity, CloudFilePlayActivity.class);
        intent.putExtra("DEV_ID", deviceId);
        mCameraEvent = cameraEvent;
        activity.startActivity(intent);
    }


    @Override
    public void OnPlatformEvent(PlatResult platResult) {
        if(platResult.getPlatCmd() == PlatResult.PlatCmd.getCloudOssInfo){
            dismissLoading();
            if(PlatCode.SUCCESS == platResult.getResponseCode()){
                GetCloudOssInfoResult ossInfoResult = (GetCloudOssInfoResult) platResult;
                OssInfo ossInfo = ossInfoResult.data;

                OSSLog.disableLog();
                OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(ossInfo.getKey(), ossInfo.getSecret(), ossInfo.getToken());
                ClientConfiguration conf = new ClientConfiguration();
                conf.setConnectionTimeout(10 * 1000); // 连接超时，默认10秒
                conf.setSocketTimeout(10 * 1000); // socket超时，默认10秒
                conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
                conf.setMaxErrorRetry(5); // 失败后最大重试次数，默认5次
                mOssClient = new OSSClient(getApplicationContext(), ossInfo.getEndPoint(), credentialProvider, conf);

                findViewById(R.id.btn_play).setEnabled(true);
                findViewById(R.id.btn_stop).setEnabled(true);
                findViewById(R.id.btn_capture).setEnabled(true);
                findViewById(R.id.btn_get_preview).setEnabled(true);
            }else{
                showToast("Get Oss Info failed");
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMediaPlayer != null){
            mMediaPlayer.stopDecH264();
            mMediaPlayer.stop();
            mMediaPlayer.releasePort();
        }
        mGlRenderer.stopDisplay();

        mDownloadHandler.removeCallbacksAndMessages(null);
        mDownloadThread.quit();
        mPreviewHandler.removeCallbacksAndMessages(null);
        mPreviewThread.quit();
        mPlayHandler.removeCallbacksAndMessages(null);
        mPlayThread.quit();
        mAudioHandler.removeCallbacksAndMessages(null);
        mAudioHandlerThread.quit();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_cloud_file_play;
    }

    @Override
    public void onCreateData(@Nullable Bundle bundle) {
        mTvTitle = findViewById(R.id.text_title);
        mTvTitle.setText(R.string.file_play);
        mTvTime = findViewById(R.id.tv_time);

        GLFrameSurface gl = findViewById(R.id.glsurface);
        gl.setEGLContextClientVersion(2);
        mGlRenderer = new GlRenderer(gl);
        gl.setRenderer(mGlRenderer);

        mDeviceId = getIntent().getStringExtra("DEV_ID");

        findViewById(R.id.btn_play).setEnabled(false);
        findViewById(R.id.btn_stop).setEnabled(false);
        findViewById(R.id.btn_capture).setEnabled(false);
        findViewById(R.id.btn_get_preview).setEnabled(false);

        mDownloadThread = new HandlerThread("Download_cloud");
        mDownloadThread.start();
        mDownloadHandler = new DownloadHandler(mDownloadThread.getLooper());
        mPreviewThread = new HandlerThread("Preview_cloud");
        mPreviewThread.start();
        mPreviewHandler = new PreviewHandler(mPreviewThread.getLooper());
        mPlayThread = new HandlerThread("Play_cloud");
        mPlayThread.start();
        mPlayHandler = new PlayHandler(mPlayThread.getLooper());
        mAudioHandlerThread = new HandlerThread("audio");
        mAudioHandlerThread.start();
        mAudioHandler = new AudioHandler(mAudioHandlerThread.getLooper());

        mUser = GApplication.app.user;
        GosCloud.getCloud().addOnPlatformEventCallback(this);
        showLoading();
        GosCloud.getCloud().getCloudOssInfo(mDeviceId, mUser.getToken(), mUser.getUserName(), 0);
    }

    class AudioHandler extends Handler {
        public AudioHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] data = (byte[]) msg.obj;
            if(mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
                mAudioTrack.write(data,0,data.length);
        }
    }

    boolean isRecSeekTo = false;//时间回调是否触发
    boolean isSeekTo = false;//是否快进
    int seekToTime = 0;//快进的时间
    long mCameraEventRecStartTime;//播放录像开始时刻
    long mCameraEventRecTime;//录像显示的时刻
    List<CloudPlayInfo> mCloudPlayInfoAddedList = new ArrayList<>();
    class DownloadHandler extends Handler {

        public boolean isStopDownload;
        public DownloadHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            mCloudPlayInfoAddedList.clear();

            isStopDownload = false;
            String devId = mDeviceId;
            CameraEvent event = (CameraEvent) msg.obj;
            int seekTime = msg.arg1;

            HttpURLConnection httpURLConnection = null;
            FileOutputStream fos = null;
            try {
                CameraEvent cameraEvent = mCameraEvent;
                Object cloudPlayInfos = cameraEvent.getCloudPlayInfos();
                List<CloudPlayInfo> data = cloudPlayInfos != null ? (List<CloudPlayInfo>) cloudPlayInfos : null;
                dbg.D(Tag, "downloadFileFromNet >>> Temp CloudPlayInfos data=" + data);
                if(data == null){
                    //避免同一个告警消息重复请求
                    synchronized (cameraEvent.lock){
                        cloudPlayInfos = cameraEvent.getCloudPlayInfos();
                        data = cloudPlayInfos != null ? (List<CloudPlayInfo>) cloudPlayInfos : null;
                        dbg.D(Tag, "downloadFileFromNet >>> Temp... CloudPlayInfos data=" + data);
                        if(data == null){
                            GetCloudPlayFileListResult playFileList = GosCloud.getCloud().getPlayFileList(devId,
                                    cameraEvent.getStartTime(), cameraEvent.getEndTime(), mUser.getToken(), mUser.getUserName());
                            data = playFileList.getData();
                            cameraEvent.setCloudPlayInfos(data);//缓存事件视频文件信息
                            dbg.D(Tag, "downloadFileFromNet >>> CloudPlayInfos data=" + data);
                        }
                    }
                }

                //查询最近时间
                int x = 0;
                int seekTo = 0;
                for(int y = 0; data != null && y < data.size(); y++){
                    CloudPlayInfo cloudPlayInfo = data.get(y);
                    if(cloudPlayInfo.getStartTime() <= seekTime && cloudPlayInfo.getEndTime() >= seekTime){
                        x = y;
                        seekTo = (int) (seekTime - cloudPlayInfo.getStartTime());
                        break;
                    }
                }

                for(; !isStopDownload && data != null && x < data.size(); x++){
                    CloudPlayInfo cloudPlayInfo = data.get(x);
                    String savePath = getCameraEventCacheDir(event.getStartTime()) + File.separator + cloudPlayInfo.getStartTime();
                    cloudPlayInfo.setFilePath(savePath);
                    File file = new File(cloudPlayInfo.getFilePath());
                    String url = getCloudFileUrl(cloudPlayInfo.getKey(), cloudPlayInfo.getBucket());
                    cloudPlayInfo.setUrl(url);

                    //dbg.D(Tag, "downloadFileFromNet >>> cloudPlayInfo.getPicUrl()=" + getCloudFileUrl("7_A99F72000000003/202010231514490b.pic", cloudPlayInfo.getBucket()));
                    //文件不存在需要下载, 避免下载同一个文件
                    synchronized (cloudPlayInfo.lock){
                        if(!isStopDownload&&!file.exists()){
                            dbg.D(Tag, "downloadFileFromNet >>> cloudPlayInfo.getUrl()=" + url);
                            httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();
                            httpURLConnection.setRequestMethod("GET");
                            httpURLConnection.setConnectTimeout(10000);
                            httpURLConnection.setReadTimeout(10000);
                            httpURLConnection.setRequestProperty(GosCloud.HeaderKey, GosCloud.HeaderValue);
                            httpURLConnection.connect();
                            if(isStopDownload){break; }
                            InputStream inputStream = httpURLConnection.getInputStream();

                            int len;
                            byte buf[] = new byte[1024];
                            File tempFile = new File(savePath+".temp");
                            fos = new FileOutputStream(tempFile);
                            while(!isStopDownload&&(len=inputStream.read(buf))!=-1){
                                fos.write(buf,0,len);
                            }
                            httpURLConnection.disconnect();
                            httpURLConnection = null;
                            fos.close();
                            fos = null;
                            if(!isStopDownload)
                                tempFile.renameTo(file);
                        }
                    }

                    if(isStopDownload){break;}
                    cloudPlayInfo.setCameraEvent(cameraEvent);
                    mCloudPlayInfoAddedList.add(cloudPlayInfo);

                    dbg.D(Tag, "downloadFileFromNet["+x+"]="+cloudPlayInfo.toString());

                    if(isMediaPlayerStarted){
                        dbg.D(Tag, "downloadFileFromNet >>> mMediaPlayer add x="+x);
                        //mMediaPlayer.addDevH264(cloudPlayInfo.getFilePath());
                        //触发接着播放
                        Message message = mPlayHandler.obtainMessage();
                        message.what = ADD;
                        message.obj = cloudPlayInfo;
                        mPlayHandler.sendMessage(message);
                    }else{
                        mCameraEventRecStartTime = mCameraEventRecTime = cloudPlayInfo.getStartTime();
                        mSimpleDateFormat.applyPattern("yyyy_MM_dd_HH_mm_ss");
                        dbg.D(Tag+"_SeekTime", "downloadFileFromNet >>> mMediaPlayer start, mCameraEventRecTime=, " + mCameraEventRecTime + "," +
                                mSimpleDateFormat.format(new Date(mCameraEventRecTime*1000L)));
                        dbg.D(Tag, "downloadFileFromNet >>> mMediaPlayer start, " + seekTime + ",seekTo="+seekTo+",x="+x);
                        //需要进行快进
                        if(seekTo > 0){
                            isSeekTo = true;
                            seekToTime = seekTo;
                        }
                        isRecSeekTo = false;
                        mMediaPlayer.start(10);

                        Message message = mPlayHandler.obtainMessage();
                        message.what = START;
                        message.obj = cloudPlayInfo;
                        mPlayHandler.sendMessage(message);
                        isMediaPlayerStarted = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
                if(fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public final int START = 1;//开始播放
    public final int ADD = 2;//添加播放
    public final int FINISH = 3;//片段播放结束
    class PlayHandler extends Handler{
        private CloudPlayInfo currentCloudPlayInfo;//当前正在播放的
        private boolean isFinish;

        public PlayHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            CloudPlayInfo cloudPlayInfo = (CloudPlayInfo) msg.obj;
            if(mDownloadHandler.isStopDownload){return;}
            switch (msg.what){
                case START:
                    isFinish = false;
                    currentCloudPlayInfo = cloudPlayInfo;
                    mCameraEventRecStartTime = mCameraEventRecTime = cloudPlayInfo.getStartTime();
                    mMediaPlayer.startDecH264(cloudPlayInfo.getFilePath(), false, 10);
                    break;
                case ADD:
                    try {
                        if(isFinish){
                            //播放完成
                            int i = mCloudPlayInfoAddedList.indexOf(currentCloudPlayInfo);
                            //有新的视频已经下载
                            if(i < mCloudPlayInfoAddedList.size()-1){
                                currentCloudPlayInfo = mCloudPlayInfoAddedList.get(i+1);
                                mCameraEventRecStartTime = mCameraEventRecTime = currentCloudPlayInfo.getStartTime();
                                mMediaPlayer.startDecH264(cloudPlayInfo.getFilePath(), false, 10);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case FINISH:
                    isFinish = true;
                    mMediaPlayer.stopDecH264();
                    int i = mCloudPlayInfoAddedList.indexOf(currentCloudPlayInfo);
                    //播放下一个
                    if(i < mCloudPlayInfoAddedList.size()-1){
                        try {
                            currentCloudPlayInfo = mCloudPlayInfoAddedList.get(i+1);
                            mCameraEventRecStartTime = mCameraEventRecTime = currentCloudPlayInfo.getStartTime();
                            mMediaPlayer.startDecH264(currentCloudPlayInfo.getFilePath(), false, 10);
                            isFinish = false;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    class PreviewHandler extends Handler{
        public boolean isStopDownload;
        public PreviewHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            isStopDownload = false;
            String devId = mDeviceId;
            CameraEvent cameraEvent = (CameraEvent) msg.obj;
            final int seekTime = msg.arg1;//平移的时间
            HttpURLConnection httpURLConnection = null;
            FileOutputStream fos = null;
            try {
                //获取录像文件云存信息
                Object cloudPlayInfos = cameraEvent.getCloudPlayInfos();
                List<CloudPlayInfo> data = cloudPlayInfos != null ? (List<CloudPlayInfo>) cloudPlayInfos : null;
                dbg.D(Tag, "downloadFileFromNet Preview>>> Temp CloudPlayInfos data=" + data);
                if(data == null){
                    //避免同一个告警消息重复请求
                    synchronized (cameraEvent.lock){
                        cloudPlayInfos = cameraEvent.getCloudPlayInfos();
                        data = cloudPlayInfos != null ? (List<CloudPlayInfo>) cloudPlayInfos : null;
                        dbg.D(Tag, "downloadFileFromNet Preview>>> Temp... CloudPlayInfos data=" + data);
                        if(data == null){
                            GetCloudPlayFileListResult playFileList = GosCloud.getCloud().getPlayFileList(devId, cameraEvent.getStartTime(),
                                    cameraEvent.getEndTime(), mUser.getToken(), mUser.getUserName());
                            data = playFileList.getData();
                            cameraEvent.setCloudPlayInfos(data);//缓存事件视频文件信息
                            dbg.D(Tag, "downloadFileFromNet Preview>>> CloudPlayInfos data=" + data);
                        }
                    }
                }

                //获取最近一段录像
                CloudPlayInfo cloudPlayInfo = null;
                for(int x = 0; data != null && x < data.size(); x++){
                    CloudPlayInfo info = data.get(x);
                    dbg.D(Tag, "downloadFileFromNet Preview>>> seekTime=" + seekTime + ",CloudPlayInfo["+x+"]="+info);
                    if(info.getStartTime() <= seekTime && info.getEndTime() >= seekTime){
                        cloudPlayInfo = info;
                        break;
                    }
                }
                dbg.D(Tag, "downloadFileFromNet Preview>>> seekTime=" + seekTime + ",Selected CloudPlayInfo = " + cloudPlayInfo);
                if(cloudPlayInfo == null){
                    return;
                }

                //从云服务上下载录像
                String savePath = getCameraEventCacheDir(cameraEvent.getStartTime()) + File.separator + cloudPlayInfo.getStartTime();
                cloudPlayInfo.setFilePath(savePath);
                File file = new File(cloudPlayInfo.getFilePath());
                String url = getCloudFileUrl(cloudPlayInfo.getKey(), cloudPlayInfo.getBucket());
                cloudPlayInfo.setUrl(url);
                //避免两个线程下载同一个文件
                synchronized (cloudPlayInfo.lock){
                    if(!isStopDownload&&!file.exists()){
                        dbg.D(Tag, "downloadFileFromNet Preview>>> cloudPlayInfo.getUrl()=" + url);
                        httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.setConnectTimeout(10000);
                        httpURLConnection.setReadTimeout(10000);
                        httpURLConnection.setRequestProperty(GosCloud.HeaderKey, GosCloud.HeaderValue);
                        httpURLConnection.connect();
                        if(isStopDownload){
                            try{
                                httpURLConnection.disconnect();
                                httpURLConnection = null;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            return;
                        }
                        InputStream inputStream = httpURLConnection.getInputStream();

                        int len;
                        byte buf[] = new byte[1024];
                        File tempFile = new File(savePath+".temp");
                        fos = new FileOutputStream(tempFile);
                        while(!isStopDownload&&(len=inputStream.read(buf))!=-1){
                            fos.write(buf,0,len);
                        }
                        httpURLConnection.disconnect();
                        httpURLConnection = null;
                        fos.close();
                        fos = null;
                        if(isStopDownload){
                            try{
                                tempFile.delete();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            tempFile.renameTo(file);
                        }
                    }
                }

                if(!isStopDownload){
                    final String previewPath = savePath + ".jpg";
                    final int seek = (int) (seekTime - cloudPlayInfo.getStartTime());
                    final GosMediaPlayer mediaPlayer = new GosMediaPlayer();
                    mediaPlayer.getPort();
                    mediaPlayer.setDecodeType(DecType.YUV420);
                    mediaPlayer.setOnDecCallBack(new AvPlayerCodec.OnDecCallBack() {
                        boolean isGet;
                        @Override
                        public void decCallBack(DecType decType, byte[] bytes, int i, int i1, int i2, int i3, int i4, int i5, int i6, String s) {
                            if(DecType.YUV420 == decType && !isGet){
                                dbg.D(Tag, "downloadFileFromNet Preview>>> decCallBack seekPlayRec");
                                isGet = true;
                                mediaPlayer.seekPlayRec(seek, previewPath);
                            }
                            if(isStopDownload && !isFinishing()){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(mediaPlayer != null){
                                            mediaPlayer.setOnDecCallBack(null);
                                            mediaPlayer.setOnRecCallBack(null);
                                            mediaPlayer.stopDecH264();
                                            mediaPlayer.stop();
                                            mediaPlayer.releasePort();
                                        }
                                    }
                                }).start();
                            }
                        }
                    });
                    mediaPlayer.setOnRecCallBack(new AvPlayerCodec.OnRecCallBack() {
                        @Override
                        public void recCallBack(RecEventType recEventType, long l, long l1) {
                            dbg.D(Tag, "downloadFileFromNet Preview>>> recCallBack, recEventType="+recEventType);
                            if(RecEventType.AVRetPlayRecSeekCapture == recEventType){
                                if(!isStopDownload && !isFinishing()){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToast("Preview pic:"+previewPath);

                                        }
                                    });
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(mediaPlayer != null){
                                                mediaPlayer.setOnDecCallBack(null);
                                                mediaPlayer.setOnRecCallBack(null);
                                                mediaPlayer.stopDecH264();
                                                mediaPlayer.stop();
                                                mediaPlayer.releasePort();
                                            }
                                        }
                                    }).start();
                                }
                            }
                        }
                    });
                    dbg.D(Tag, "downloadFileFromNet Preview>>> start decode previewPath=" + previewPath + ",seek="+seek);
                    mediaPlayer.startDecH264(file.getAbsolutePath(), false, 10);
                    mediaPlayer.start(10);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
                if(fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void createMediaPlayer(){
        mMediaPlayer = new GosMediaPlayer();
        mMediaPlayer.getPort();
        mMediaPlayer.setDecodeType(DecType.YUV420);
        mMediaPlayer.setOnDecCallBack(this);
        mMediaPlayer.setOnRecCallBack(this);
        isMediaPlayerStarted = false;
    }

    public void releaseMediaPlayer(){
        if(mMediaPlayer != null){
            mMediaPlayer.setOnDecCallBack(null);
            mMediaPlayer.setOnRecCallBack(null);
            mMediaPlayer.stopDecH264();
            mMediaPlayer.stop();
            mMediaPlayer.releasePort();
        }
    }

    public void startPlay(View v){
        findViewById(R.id.btn_play).setEnabled(false);
        findViewById(R.id.btn_stop).setEnabled(true);
        findViewById(R.id.btn_capture).setEnabled(true);

        if(mAudioTrack == null){
            int sampleRate = 8000;
            int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
            int nMinBufSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    nMinBufSize, AudioTrack.MODE_STREAM);
        }
        try {
            mAudioTrack.play();
        }catch (Exception e){
            e.printStackTrace();
        }

        releaseMediaPlayer();
        createMediaPlayer();
        mDownloadHandler.isStopDownload = true;
        Message message = mDownloadHandler.obtainMessage();
        message.arg1 = (int)mCameraEvent.getStartTime();
        message.obj = mCameraEvent;
        mDownloadHandler.sendMessage(message);
    }

    public void stopPlay(View v){
        findViewById(R.id.btn_play).setEnabled(true);
        findViewById(R.id.btn_stop).setEnabled(false);
        findViewById(R.id.btn_capture).setEnabled(false);

        try {
            mAudioTrack.pause();
        }catch (Exception e){
            e.printStackTrace();
        }
        releaseMediaPlayer();
        mDownloadHandler.isStopDownload = true;
    }

    public void capturePic(View v){
        if(mMediaPlayer != null){
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/c.jpg";
            mMediaPlayer.capture(path);
            Toast.makeText(CloudFilePlayActivity.this, path, Toast.LENGTH_LONG).show();
        }
    }

    public void toParseMp4(final View v){
        if(mCloudPlayInfoAddedList.size() > 0){
            boolean isExist = true;
            for(CloudPlayInfo cloudPlayInfo : mCloudPlayInfoAddedList){
                String filePath = cloudPlayInfo.getFilePath();
                if(TextUtils.isEmpty(filePath) || !new File(filePath).exists()){
                    isExist = false;
                }
            }
            if(isExist){
                v.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp.data";
                        try {
                            FileOutputStream fos = new FileOutputStream(filePath);
                            for(CloudPlayInfo cloudPlayInfo : mCloudPlayInfoAddedList){
                                String path = cloudPlayInfo.getFilePath();
                                FileInputStream fis = new FileInputStream(path);
                                int len;
                                byte[] buf = new byte[1024];
                                while((len=fis.read(buf))!=-1){
                                    fos.write(buf,0,len);
                                }
                                fis.close();
                            }
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        final String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/d.mp4";
                        final GosMediaPlayer mediaPlayer = new GosMediaPlayer();
                        mediaPlayer.getPort();
                        mediaPlayer.setOnRecCallBack(new AvPlayerCodec.OnRecCallBack() {
                            @Override
                            public void recCallBack(RecEventType recEventType, long l, long l1) {
                                if(RecEventType.AVRetPlayRecRecordFinish == recEventType){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CloudFilePlayActivity.this, savePath, Toast.LENGTH_LONG).show();
                                            mediaPlayer.stopDecH264();
                                            mediaPlayer.stop();
                                            mediaPlayer.releasePort();
                                            v.setEnabled(true);
                                        }
                                    });
                                }
                            }
                        });
                        mediaPlayer.setH264FileRecParam(true, savePath, 0, -1);
                        mediaPlayer.startDecH264(filePath, false, 0);
                        mediaPlayer.start(0);
                    }
                }).start();
            }else{
                showToast("File not exist");
            }
        }else{
            showToast("File not exist");
        }
    }

    private String getCloudFileUrl(String keyUrl, String mBucket) {
        String url = null;
        if (mOssClient != null)
            try {
                url = mOssClient.presignConstrainedObjectURL(mBucket, keyUrl, 24 * 3600 * 30);
            } catch (ClientException e) {
                e.printStackTrace();
            }
        return url;
    }

    public String getCameraEventCacheDir(long startTime){
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yyyyMMdd = mSimpleDateFormat.format(new Date(startTime*1000));
        String sDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + mUser.getUserName()
                + File.separator + mDeviceId
                + File.separator + yyyyMMdd;
        File file = new File(sDir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return sDir;
    }

    public void getPreviewPic(View v){
        mPreviewHandler.removeCallbacksAndMessages(null);
        mPreviewHandler.isStopDownload = true;
        Message message = mPreviewHandler.obtainMessage();
        message.obj = mCameraEvent;
        message.arg1 = (int)mCameraEvent.getStartTime();
        mPreviewHandler.sendMessage(message);
    }

    @Override
    public void decCallBack(DecType type, byte[] data, int dataSize, int width, int height, int rate, int ch, int flag, int frameNo, String aiInfo) {
        if (DecType.YUV420 == type) {
            dbg.D(Tag+"_decCallBack","isSeekTo="+isSeekTo+",isRecSeekTo="+isRecSeekTo+",seekToTime="+seekToTime+",dataSize="+dataSize);
            if(isSeekTo){
                dbg.D(Tag+"_decCallBack","isSeekTo="+isSeekTo+",seekToTime="+seekToTime);
                //进行seek
                mMediaPlayer.seekPlayRec(seekToTime, null);
                isSeekTo = false;
                seekToTime = 0;
            }else {
                ByteBuffer buf = ByteBuffer.wrap(data, 0, dataSize);
                if (mGlRenderer != null)
                    mGlRenderer.update(buf, width, height);
            }
        } else if(DecType.AUDIO == type){
            if (mAudioTrack != null){
                byte[] buf = new byte[data.length];
                System.arraycopy(data, 0, buf, 0, data.length);
                mAudioHandler.obtainMessage(0, buf).sendToTarget();
            }
        }
    }

    @Override
    public void recCallBack(RecEventType type, final long data, long flag) {
        switch (type) {
            case AVRetPlayRecTime://录像进度
                dbg.D("CloudStoragePlay_recCallBack", "drivenScrollToTimePosition >>> type=" + type + " >>> time=" + data+" >>> curTime="+System.currentTimeMillis()/1000);
                if(isFinishing() || mDownloadHandler.isStopDownload){break;}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dbg.D("CloudStoragePlay_recCallBack","time=" + mSimpleDateFormat.format(new Date((mCameraEventRecTime)*1000)) + ",isSeekTo=" + isSeekTo);
                        dbg.D(Tag+"_SeekTime", "AVRetPlayRecTime >>> mCameraEventRecTime=, " + mCameraEventRecTime + "," +
                                mSimpleDateFormat.format(new Date(mCameraEventRecTime*1000L)));
                        if(isSeekTo){
                            isRecSeekTo = true;
                        }else{
                            isRecSeekTo = false;
                            mCameraEventRecTime = mCameraEventRecStartTime + data;
                            mTvTime.setText(mSimpleDateFormat.format(new Date(mCameraEventRecTime*1000L)));
                        }
                    }
                });
                break;
            case AVRetPlayRecFinish:
                dbg.D("CloudStoragePlay_recCallBack","type="+type+",data="+data+",flag="+flag);
                if(isFinishing()){break;}
                Message message = mPlayHandler.obtainMessage();
                message.what = FINISH;
                mPlayHandler.sendMessage(message);
                break;
        }
    }
}
