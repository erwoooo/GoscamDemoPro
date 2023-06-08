package com.gocam.goscamdemopro.play;

import static com.gos.platform.api.devparam.DevParam.DevParamCmdType.CurrentTemperature;
import static com.gos.platform.api.devparam.DevParam.DevParamCmdType.DeviceSwitch;
import static com.gos.platform.api.devparam.DevParam.DevParamCmdType.LowpowerModeSetting;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gocam.goscamdemopro.R;
import com.gocam.goscamdemopro.cloud.data.GosCloud;
import com.gocam.goscamdemopro.entity.DevCap;
import com.gocam.goscamdemopro.entity.Device;
import com.gocam.goscamdemopro.utils.DeviceManager;
import com.gocam.goscamdemopro.utils.dbg;
import com.gos.avplayer.GosMediaPlayer;
import com.gos.avplayer.contact.BufferCacheType;
import com.gos.avplayer.contact.DecType;
import com.gos.avplayer.contact.RecEventType;
import com.gos.avplayer.jni.AvPlayerCodec;
import com.gos.avplayer.surface.GLFrameSurface;
import com.gos.avplayer.surface.GlRenderer;
import com.gos.platform.api.GosSession;
import com.gos.platform.api.contact.DeviceType;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.contact.ResultCode;
import com.gos.platform.api.devparam.CurTempParam;
import com.gos.platform.api.devparam.DevParam;
import com.gos.platform.api.devparam.DevParamResult;
import com.gos.platform.api.devparam.DeviceSwitchParam;
import com.gos.platform.api.devparam.LowpowerModeSettingParam;
import com.gos.platform.api.domain.DeviceStatus;
import com.gos.platform.api.inter.OnPlatformEventCallback;
import com.gos.platform.api.result.DeviceParamReportNotifyResult;
import com.gos.platform.api.result.NotifyDeviceParamResult;
import com.gos.platform.api.result.NotifyDeviceStatusResult;
import com.gos.platform.api.result.PlatResult;
import com.gos.platform.api.result.QueryDeviceOnlineStatusResult;
import com.gos.platform.device.base.Connection;
import com.gos.platform.device.contact.ConnectStatus;
import com.gos.platform.device.contact.OnOff;
import com.gos.platform.device.contact.StreamType;
import com.gos.platform.device.domain.AvFrame;
import com.gos.platform.device.inter.IVideoPlay;
import com.gos.platform.device.inter.OnDevEventCallback;
import com.gos.platform.device.result.ConnectResult;
import com.gos.platform.device.result.DevResult;
import com.gos.platform.device.result.KeepAliveResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PlayJavaActivity extends AppCompatActivity implements OnDevEventCallback, OnPlatformEventCallback, AvPlayerCodec.OnDecCallBack, AvPlayerCodec.OnRecCallBack, IVideoPlay {
    public static final String EXTRA_DEVICE_ID = "EXTRA_DEVICE_ID";
    TextView mTvTitle;
    ImageView ivBack;
    GLFrameSurface mGlFrameSurface;
    Button mBtnStartVideo;
    Button mBtnStopVideo;
    Button mBtnStartTalk;
    Button mBtnStopTalk;
    Button mBtnStartRecord;
    Button mBtnStopRecord;
    Button mBtnCapture;

    final int FILE_TYPE = 10;
    final int STREAM_TYPE = 11;
    int talkType = FILE_TYPE;
//    int talkType = STREAM_TYPE;

    Device mDevice;
    Connection mConnection;
    GlRenderer mGlRenderer;
    GosMediaPlayer mMediaPlayer;
    HandlerThread recordHandlerThread;
    HandlerThread audioHandlerThread;
    private String mDeviceId;
    public static void startActivity(Context context, String deviceId) {
        Intent intent = new Intent(context, PlayJavaActivity.class);
        intent.putExtra(EXTRA_DEVICE_ID, deviceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_play);
        mDeviceId = getIntent().getStringExtra(EXTRA_DEVICE_ID);
        mDevice = DeviceManager.getInstance().findDeviceById(mDeviceId);
        mConnection = mDevice.getConnection();
        mConnection.addOnEventCallbackListener(this);
        ivBack = findViewById(R.id.back_img);
        ivBack.setOnClickListener(v->{
            finish();
        });
        mTvTitle = findViewById(R.id.text_title);
        mTvTitle.setText(mDevice.devName);
        mGlFrameSurface = findViewById(R.id.gl_surface);
        mBtnStartVideo = findViewById(R.id.btn_OpenStream);
        mBtnStartVideo.setEnabled(false);
        mBtnStopVideo = findViewById(R.id.btn_CloseStream);
        mBtnStopVideo.setEnabled(false);
        mBtnStartTalk = findViewById(R.id.btn_StartTalk);
        mBtnStartTalk.setEnabled(false);
        mBtnStopTalk = findViewById(R.id.btn_Stop_Talk);
        mBtnStopTalk.setEnabled(false);
        mBtnStartRecord = findViewById(R.id.btn_Start_record);
        mBtnStartRecord.setEnabled(false);
        mBtnStopRecord = findViewById(R.id.btn_Stop_record);
        mBtnStopRecord.setEnabled(false);
        mBtnCapture = findViewById(R.id.btn_capture);
        mBtnCapture.setEnabled(false);

        mGlFrameSurface.setEGLContextClientVersion(2);
        mGlRenderer = new GlRenderer(mGlFrameSurface);
        mGlFrameSurface.setRenderer(mGlRenderer);
        mGlFrameSurface.setEnableZoom(true);

        mMediaPlayer = new GosMediaPlayer();
        mMediaPlayer.getPort();
        mMediaPlayer.setDecodeType(DecType.YUV420);
        mMediaPlayer.setBufferSize(BufferCacheType.StreamCache, 60, 200 * 1024);//60
        mMediaPlayer.start(100);
        mMediaPlayer.setOnDecCallBack(this);
        mMediaPlayer.setOnRecCallBack(this);

        int sampleRate = 8000;
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int nMinBufSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        sAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                sampleRate, AudioFormat.CHANNEL_IN_MONO, audioFormat, nMinBufSize);

        sAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,//STREAM_VOICE_CALL  STREAM_MUSIC
                sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                nMinBufSize, AudioTrack.MODE_STREAM, sAudioRecord.getAudioSessionId());//,

        sAudioTrack.play();

        recordHandlerThread = new HandlerThread("record");
        recordHandlerThread.start();
        sRecordHandler = new RecordHandler(recordHandlerThread.getLooper());
        audioHandlerThread = new HandlerThread("audio");
        audioHandlerThread.start();
        sAudioHandler = new AudioHandler(audioHandlerThread.getLooper());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GosCloud.getCloud().removeOnPlatformEventCallback(this);
        mConnection.removeOnEventCallbackListener(this);
        mGlRenderer.stopDisplay();
        mGlRenderer.release();

        mMediaPlayer.stop();
        mMediaPlayer.releasePort();
        mMediaPlayer.setOnDecCallBack(this);
        mMediaPlayer.setOnRecCallBack(this);

        recordHandlerThread.quitSafely();
        audioHandlerThread.quitSafely();
        sAudioHandler.removeCallbacksAndMessages(null);
        mConnection.stopTalk(0);
        mConnection.stopVideo(0, this);

        sAudioRecord.stop();
        sAudioRecord.release();
        sAudioTrack.stop();
        sAudioTrack.release();
        GosCloud.getCloud().addOnPlatformEventCallback(this);
    }

    AudioTrack sAudioTrack;
    AudioHandler sAudioHandler;

    @Override
    public void OnPlatformEvent(PlatResult platResult) {
        PlatResult.PlatCmd cmd = platResult.getPlatCmd();
        switch (cmd) {
            case NotifyDeviceStatus:
                NotifyDeviceStatusResult result = (NotifyDeviceStatusResult) platResult;
                for (int i = 0; i < result.getDeviceStatusList().size(); i++) {
                    DeviceStatus deviceStatus = result.getDeviceStatusList().get(i);
                    if (deviceStatus.deviceId.equals(mDeviceId) && mDevice != null &&
                            (mDevice.devType == DeviceType.DOOR_BELL || mDevice.devType == DeviceType.BATTERY_IPC)) {
                        //重新出发唤醒
                        //if(keepAliveDisposable == null || keepAliveDisposable.isDisposed())
                        if ( deviceStatus.deviceStatus != DeviceStatus.OFFLINE)
                            handleDoorbellWakeup();
                    }
                }
                break;
            case getDeviceList:
            case getDeviceListEx:
                Device device = DeviceManager.getInstance().findDeviceById(mDeviceId);
                if (device != null && (device.devType == DeviceType.DOOR_BELL || mDevice.devType == DeviceType.BATTERY_IPC)) {
                    mDevice = device;
                    //重新出发唤醒
                    //if(keepAliveDisposable == null || keepAliveDisposable.isDisposed())
                    if ( device.deviceStatus != DeviceStatus.OFFLINE)
                        handleDoorbellWakeup();
                }
                break;
            case NotifyDeviceParam:
                NotifyDeviceParamResult result1 = (NotifyDeviceParamResult) platResult;
                String paramJson = result1.paramJson;
                try {
                    JSONObject jsonObject = new JSONObject(paramJson);
                    JSONArray jsonArray = jsonObject.optJSONArray("SubStatus");
                    List<Integer> statusList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Object opt = jsonArray.opt(i);
                        statusList.add((Integer) opt);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case DeviceParamReportNotify:

                DeviceParamReportNotifyResult reportNotifyResult = (DeviceParamReportNotifyResult) platResult;
                if (TextUtils.equals(reportNotifyResult.deviceId, mDeviceId)) {
                    for (int i = 0; reportNotifyResult.getResponseCode() == 0 &&
                            reportNotifyResult.devParamList != null && i < reportNotifyResult.devParamList.size(); i++) {
                        DevParam devParam = reportNotifyResult.devParamList.get(i);
                        switch (devParam.CMDType) {
                            case CurrentTemperature:

                                break;
                            case DeviceSwitch:

                                break;
                        }
                    }

                }
                break;
        }
    }

    Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            isOfflineTimeOut = true;
        }
    };
    boolean isOfflineTimeOut;
    Disposable keepAliveDisposable;
    protected  Handler mHandler = new Handler();
    private void handleDoorbellWakeup() {
        if (mDevice.devType == DeviceType.DOOR_BELL || mDevice.devType == DeviceType.BATTERY_IPC) {
            if (keepAliveDisposable != null) {
                keepAliveDisposable.dispose();
                keepAliveDisposable = null;
            }
            mHandler.removeCallbacks(timeOutRunnable);
            isOfflineTimeOut = false;
            mHandler.postDelayed(timeOutRunnable, 20000);
            Observable<Integer> obser = Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                    DevCap devCap = mDevice.getDevCap();
                    if (mDevice.deviceStatus == DeviceStatus.OFFLINE && devCap.isSupDoorbellLowpower) {
                        DevParamResult devParamResult = GosSession.getSession().AppGetDeviceParam(mDeviceId, LowpowerModeSetting);  //查询设备省电模式
                        if (devParamResult.code == 0 && devParamResult.devParams != null && devParamResult.devParams.size() > 0) {
                            LowpowerModeSettingParam devParam = (LowpowerModeSettingParam) devParamResult.devParams.get(0);
                            mDevice.lowpowerSwitch = devParam.lowpowerSwitch;   //省电模式开关
                            Log.e("handleDoorbellWakeup", "subscribe: " + devParam.lowpowerSwitch);  //省电模式开关
                        }
                    }

                    //省电模式下，不能观看视频
                    final boolean isSleep = mDevice.deviceStatus == DeviceStatus.OFFLINE && mDevice.lowpowerSwitch == OnOff.On;  //离线并且省电模式
                    while (!isSleep && !isFinishing() &&
                            (mDevice.deviceStatus != DeviceStatus.OFFLINE || !isOfflineTimeOut)
                            && !e.isDisposed()) {  //没有离线
                        //唤醒设备
                        GosSession.getSession().wakeUpDeviceSyn(mDeviceId);

                        SystemClock.sleep(1000);
                        //查询设备在线状态
                        QueryDeviceOnlineStatusResult statusResult = GosSession.getSession().queryDeviceOnlineStatusSyn(mDeviceId);
                        if (statusResult.getResponseCode() == PlatCode.SUCCESS) {   //查询成功？
                            Log.e("handleDoorbellWakeup", "subscribe 状态: " + statusResult.isOnline );
                            mDevice.deviceStatus = statusResult.isOnline;  //赋值状态是否在线
                            mDevice.setPlatDevOnline(mDevice.deviceStatus == DeviceStatus.ONLINE || mDevice.deviceStatus == DeviceStatus.SLEEP);  //在线或者休眠
                        }
                        dbg.D("handleDoorbellWakeup", "deviceStatus=" + mDevice.deviceStatus
                                + ",code=" + statusResult.getResponseCode() + ",isDisposed=" + e.isDisposed() + "," + e);

                        if (mDevice.deviceStatus != DeviceStatus.ONLINE)   //如果不是在线状态，继续环形查询
                            continue;
                        Log.e("handleDoorbellWakeup", "subscribe: 已经在线了" );
                        //移除唤醒超时
                        mHandler.removeCallbacks(timeOutRunnable);
                        //只有当在线的时候才会到这里
                        mDevice.getConnection().connect(0);


                        while (!isFinishing() && !e.isDisposed()) {
                            KeepAliveResult keepAliveResult = mDevice.getConnection().keepAliveSyn(0);  //心跳包
                            if (keepAliveResult == null || keepAliveResult.getResponseCode() == ResultCode.SUCCESS) {
                                SystemClock.sleep(5000);
                            } else {
                                break;
                            }
                        }
                    }

                    mHandler.removeCallbacks(timeOutRunnable);
                    e.onNext(0);
                    e.onComplete();
                }
            });
            obser = obser.subscribeOn(Schedulers.newThread());//.observeOn(AndroidSchedulers.mainThread())
            keepAliveDisposable = obser.subscribe();
        }
    }


    class AudioHandler extends Handler {
        public AudioHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] data = (byte[]) msg.obj;
            if (sAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
                sAudioTrack.play();
            sAudioTrack.write(data, 0, data.length);
        }
    }

    AudioRecord sAudioRecord;
    RecordHandler sRecordHandler;

    class RecordHandler extends Handler {
        public boolean isStartRecord;

        public RecordHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] audioData = new byte[640];
            if (sAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                sAudioRecord.startRecording();
            }

            String fileTalkDataPath = null;
            FileOutputStream fos = null;
            if (FILE_TYPE == talkType) {
                fileTalkDataPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.g711";
                try {
                    fos = new FileOutputStream(fileTalkDataPath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            while (isStartRecord) {
                int size = sAudioRecord.read(audioData, 0, audioData.length);
                if (size == audioData.length) {
                    byte[] g711Buf = new byte[320];
                    int len = AvPlayerCodec.nativeEncodePCMtoG711A(8000, 1, audioData, audioData.length, g711Buf);
                    if (len > 0) {
                        if (STREAM_TYPE == talkType) {
                            mConnection.sendTalkData(0, 53, 8000, 0, g711Buf, g711Buf.length);
                        } else {
                            try {
                                fos.write(g711Buf, 0, g711Buf.length);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            if (STREAM_TYPE == talkType) {
                mConnection.stopTalk(0);
            } else if (FILE_TYPE == talkType) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mConnection.sendSpeakFile(0, fileTalkDataPath, 0);
            }
        }
    }

    public void connect(View view) {
        if (mConnection.isConnected()) {
            showToast("connect success");
            mBtnStartVideo.setEnabled(true);
            mBtnStartTalk.setEnabled(true);
        } else {
            mConnection.connect(0);
        }
    }

    public void openStream(View view) {
        if (mConnection.isConnected()) {
            mBtnStartRecord.setEnabled(true);
            mBtnCapture.setEnabled(true);

            int timestamp = (int) (System.currentTimeMillis() / 1000L);
            int timezone = (TimeZone.getDefault().getRawOffset() / 3600000) + 24;// on the IPC side, -24,
            if (TimeZone.getDefault().inDaylightTime(new Date())) {
                timezone++;
            }
            mConnection.startVideo(0, StreamType.VIDEO_AUDIO, mDevice.getStreamPsw(), timestamp, timezone, this);
        }
    }

    public void stopStream(View view) {
        mConnection.stopVideo(0, this);

    }

    public void startTalk(View view) {
        if (mConnection.isConnected()) {
            mConnection.startTalk(0, mDevice == null ? "" : mDevice.getStreamPsw());
            mBtnStopTalk.setEnabled(true);
        }
    }

    public void stopTalk(View view) {
        sRecordHandler.isStartRecord = false;
    }

    String recordPath;

    public void startRecord(View view) {
        mBtnStartRecord.setEnabled(false);
        mBtnStopRecord.setEnabled(true);

        recordPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp4";
        mMediaPlayer.startRecord(recordPath, 0);
    }

    public void stopRecord(View view) {
        mBtnStartRecord.setEnabled(true);
        mBtnStopRecord.setEnabled(false);

        mMediaPlayer.stopRecord();
        showToast("record save path : " + recordPath);
        dbg.D("PlayActivity", recordPath);
    }

    public void capture(View view) {
        String capturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
        mMediaPlayer.capture(capturePath);
        showToast("pic save path : " + capturePath);
        dbg.D("PlayActivity", capturePath);
    }

    @Override
    public void onDevEvent(String s, DevResult devResult) {
        if (!TextUtils.equals(s, mDevice.devId)) {
            return;
        }

        DevResult.DevCmd devCmd = devResult.getDevCmd();
        int code = devResult.getResponseCode();
        switch (devCmd) {
            case connect:
                ConnectResult connectResult = (ConnectResult) devResult;
                if (connectResult.getConnectStatus() == ConnectStatus.CONNECT_SUCCESS) {
                    mBtnStartVideo.setEnabled(true);
                    mBtnStartTalk.setEnabled(true);
                    mBtnStartRecord.setEnabled(true);
                    mBtnCapture.setEnabled(true);
                    showToast("connect success");
                }
                break;
            case startVideo:
                if (ResultCode.SUCCESS == code) {
                    mBtnStopVideo.setEnabled(true);
                    showToast("start video success");
                }
                break;
            case stopVideo:
                break;
            case startTalk:
                if (ResultCode.SUCCESS == code) {
                    sRecordHandler.isStartRecord = true;
                    sRecordHandler.sendEmptyMessage(0);
                    showToast("start talk success");
                } else {
                    mConnection.stopTalk(0);
                }
                break;
            case sendSpeakFile:
                mConnection.stopTalk(0);
                break;
            case stopTalk:
                break;

        }
    }

    @Override
    public void decCallBack(DecType type, byte[] data, int dataSize, int width, int height, int rate, int ch, int flag, int frameNo, String aiInfo) {
        if (DecType.YUV420 == type) {
            ByteBuffer buf = ByteBuffer.wrap(data);
            mGlRenderer.update(buf, width, height);
        } else if (DecType.AUDIO == type) {
            byte[] t = new byte[dataSize];
            System.arraycopy(data, 0, t, 0, dataSize);
            Message obtain = Message.obtain();
            obtain.obj = t;
            sAudioHandler.sendMessage(obtain);
        }
    }

    @Override
    public void recCallBack(RecEventType type, long data, long flag) {

    }

    @Override
    public void onVideoStream(String s, AvFrame avFrame) {
        byte[] temp = new byte[avFrame.data.length];
        System.arraycopy(avFrame.data, 0, temp, 0, avFrame.data.length);
        mMediaPlayer.putFrame(temp, temp.length, 1);
    }


    protected void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}