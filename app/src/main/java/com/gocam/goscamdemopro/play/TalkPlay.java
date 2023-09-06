package com.gocam.goscamdemopro.play;

import android.media.AudioFormat;
import android.text.TextUtils;

import com.gocam.goscamdemopro.entity.Device;
import com.gocam.goscamdemopro.utils.DeviceManager;
import com.gocam.goscamdemopro.utils.dbg;
import com.gos.avplayer.GosMediaPlayer;
import com.gos.avplayer.jni.AvPlayerCodec;
import com.gos.platform.device.base.Connection;
import com.gos.platform.device.contact.ConnectStatus;
import com.gos.platform.device.inter.OnDevEventCallback;
import com.gos.platform.device.result.ConnectResult;
import com.gos.platform.device.result.DevResult;
import com.icare.echo.EchoUtil;

public abstract class TalkPlay implements AvPlayerCodec.OnEncCallBack, OnDevEventCallback {
    public static final int STREAM_TYPE = 10;
    public static final int FILE_TYPE = 11;

    public static final int AUDIO_ACC = 20;
    public static final int AUDIO_G711A = 21;
    public static final int AUDIO_PCM = 22;

    private int frequency = 16000;
    private int channel = 1;
    private int channelIn = AudioFormat.CHANNEL_IN_MONO;
    private int sampleRate = AudioFormat.ENCODING_PCM_16BIT;

    private GAudioRecord mAudioRecord;
    private int param;
    protected Connection conn;
    private String deviceID;
    protected boolean isTalk;
    protected boolean isTalkConnected;
    private int talkType;
    private int audioType;

    protected int devChn;

    public TalkPlay(int channel, Connection conn, int par, int talkType){
        dbg.D("TalkPlay","init,talkType="+talkType);
        this.param = par;
        this.conn = conn;
        this.talkType = talkType;
        this.devChn = channel;
        conn.addOnEventCallbackListener(this);
        this.deviceID = conn.getDeviceID();
    }

    public TalkPlay(Connection conn, int par, int talkType, int audioType){
        this(0,conn,par,talkType);
        intiParams(audioType,0);
    }

    public void intiParams(int type, int rate){
        if(mAudioRecord != null){return;}
        switch (type){
            case AUDIO_ACC:
                this.audioType = AUDIO_ACC;
                this.frequency = 16000;
            break;
            case AUDIO_G711A:
                this.audioType = AUDIO_G711A;
                this.frequency = 8000;
            break;
            case AUDIO_PCM:
                this.audioType = AUDIO_PCM;
                this.frequency = 16000;
                this.sampleRate = AudioFormat.ENCODING_PCM_8BIT;
                break;
        }

        mAudioRecord = new GAudioRecord(frequency,channelIn,sampleRate,talkType){
            @Override
            public void dataCallback(byte[] data, int dataLen) {//子线程回调
                synchronized (TalkPlay.this){
                    if(isTalk&&isTalkConnected){
                        if(audioType == AUDIO_ACC){
                            dbg.D("TalkPlay","aac="+data.length);
                            GosMediaPlayer.nativeAACPutBuffer(data,data.length);
                        }else if(audioType == AUDIO_G711A){
                            byte[] g711Buf = new byte[dataLen/2];
                            dbg.D("TalkPlay","g711bug_len="+g711Buf.length+",dataLen="+data.length+":"+dataLen+",frequency="+frequency+",channel="+channel);
                            if(talkType == TalkPlay.STREAM_TYPE){
                                EchoUtil.echo(data);
                            }

                            int len = GosMediaPlayer.nativeEncodePCMtoG711A(frequency,channel,data,dataLen,g711Buf);
                            dbg.D("TalkPlay","len="+len);
                            if(len>0){
                                aacData(g711Buf,len,param);
                            }
                        }else if(audioType == AUDIO_PCM){
                            aacData(data,dataLen,param);
                        }
                        //System.gc();
                    }
                }
            }
        };
    }





    public boolean startTalk(){
        isTalk = true;
        if(mAudioRecord!=null) {
            EchoUtil.start();
            mAudioRecord.init();
            GosMediaPlayer.setOnEncCallBack(this);
            GosMediaPlayer.nativeEncodeAACStart(frequency, channel, param);
            if (!isTalkConnected) {
                Device device = DeviceManager.getInstance().findDeviceById(conn.getDeviceID());
                conn.startTalk(devChn, device == null ? "" : device.getStreamPsw());
                return false; //不能直接录音
            } else {
                try {
                    mAudioRecord.startRecord(); //可以直接录音
                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    public boolean isTalkConnected(){
        return isTalkConnected;
    }

    public synchronized boolean stopTalk(){
        isTalk = false;
        if(mAudioRecord!=null) {
            mAudioRecord.stopRecord();
        }
        GosMediaPlayer.setOnEncCallBack(null);
        GosMediaPlayer.nativeEncodeAACStop();
        return true;
    }

    public synchronized boolean changeTalkTypeStopTalk(){
        isTalk = false;
        if(mAudioRecord != null){
            mAudioRecord.stopRecord();
            mAudioRecord.release();
        }
        GosMediaPlayer.setOnEncCallBack(null);
        GosMediaPlayer.nativeEncodeAACStop();
        return true;
    }

    public void release(){
        if(conn != null){
            if(isTalkConnected){
                conn.stopTalk(devChn);
            }
            conn.removeOnEventCallbackListener(this);
        }
        if(mAudioRecord!=null){
            mAudioRecord.release();
        }
        isTalkConnected = false;
    }

    @Override
    public void encCallBack(byte[] data, int dataLen, int param) { // 编码后的aac数据
        synchronized (TalkPlay.this){
            if(isTalk){
                aacData(data,dataLen,param);
            }
        }
    }

    public abstract void aacData(byte[] data, int dataLen, int param);
    public abstract void onStartTalk(DevResult baseResult);

    @Override
    public void onDevEvent(String s, DevResult baseResult) {
        if(TextUtils.equals(s,conn.getDeviceID())){
            DevResult.DevCmd cmd = baseResult.getDevCmd();
            if(DevResult.DevCmd.startTalk == cmd ){//开启成功
                if(baseResult.getResponseCode() == 0){
                    isTalkConnected = true;
                    if(!isTalk&&talkType==TalkPlay.STREAM_TYPE){//全双工异步避免
                        isTalkConnected = false;
                        stopTalk();
                    }
                    if(isTalk){
                        try{
                            mAudioRecord.startRecord();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                onStartTalk(baseResult);

            }else if(DevResult.DevCmd.connect == cmd){
                ConnectResult res = (ConnectResult) baseResult;
                if(res.getConnectStatus() == ConnectStatus.CONNECT_ERROR || res.getConnectStatus() == ConnectStatus.CONNECT_LOST){
                    synchronized (TalkPlay.this){
                        if(isTalkConnected){
                            conn.stopTalk(devChn);//如果掉线，就需要释放
                        }

                        if(FILE_TYPE == talkType){
                            isTalkConnected = false;
                        }else if(STREAM_TYPE == talkType){
                            isTalkConnected = false;
                        }
                    }
                }
            }else if(DevResult.DevCmd.sendSpeakFile == cmd){
                onStartTalk(baseResult);
            }
        }
    }
}
