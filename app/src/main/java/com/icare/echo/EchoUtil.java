package com.icare.echo;

import static com.icare.echo.EchoCancel.agc;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.text.TextUtils;

import com.gocam.goscamdemopro.utils.dbg;

import java.util.Arrays;



public class EchoUtil {
    private static final String TAG = "EchoUtil";
    //    private static final String APIKEY = "7147164e6bd33e935c80966624637859c41c88355752fb2e40ce9053a153871f857cb71446d72a6f6fdac35fec9328e7";//test key
    private static final String APIKEY = "190389c8fbf61598af0f08a025b0541d97904f29898d47d5aa226c884da419ec5f849ac725d42cb98ac26248e3702e40";
    private static boolean isInitSuccess; // EchoCancel初始化成功
    private static int echoframe; //记录设备端声音帧数
    public static final int BUF_SIZE = 640; //录音每一帧的大小
    private static final int ECHO_FRAME_SIZE = 160; //回音消除采样帧大小
    private static final int SAMPLE_RATE = 8000;
    public static boolean isStartEcho;
    public static boolean isFullDuplex = false;//是否为全双工
    public static boolean isListen = false;//是否开启音频
    protected static int volumeLevel = 3;//半双工增益等级

    public static void volumeLevel(boolean isFullDuplex,String deviceSfwVer){
        if(!isFullDuplex && ! TextUtils.isEmpty(deviceSfwVer)){
            if(deviceSfwVer.contains("U5712HAA")){
                volumeLevel = 5;
            }if(deviceSfwVer.contains("T5886GAB")){
                volumeLevel = 2;
            }
        }else{
            if(!TextUtils.isEmpty(deviceSfwVer)){
                if(deviceSfwVer.contains("UP5912JDA"))
                    volumeLevel = 0;
            }
            volumeLevel = 3;
        }
    }

    public static int getVolumeLevel(){
        dbg.i("EchoInit","getVolumeLevel="+volumeLevel);
        return volumeLevel;
    }

    /**
     * 初始化
     */
    public static void init(Context context,String deviceSfwVer){
        if(isInitSuccess){return;}
        int ret = EchoCancel.init(context, APIKEY, null, SAMPLE_RATE);
        dbg.E("EchoInit","ret="+ret);
        if (ret == 0) {
            EchoCancel.setConfig(1);
            EchoCancel.enableAgc(1);
            if(TextUtils.isEmpty(deviceSfwVer)
                    ||!(deviceSfwVer.contains("T5810HAA")
                    ||deviceSfwVer.contains("U5808HCA")
                    ||deviceSfwVer.contains("U5825HAA"))){
                EchoCancel.AgcConfig(20);
            }
            isInitSuccess = true;
        }else{
            isInitSuccess = false;
        }
    }

    public static void start(){
        dbg.E("EchoInit","start");
        isStartEcho = true;
    }

    public static void stop(){
        dbg.E("EchoInit","stop");
        isStartEcho = false;
    }

    public static void destroy(Context context){
        isFullDuplex = false;
        isInitSuccess = false;
        EchoCancel.clearRecorder();
        int ret = EchoCancel.destroy();
        dbg.E("SYSEchoCancel","destroy,ret="+ret);

        modeNormal(context);
    }

    public static void modeNormal(Context context){
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        //注销蓝牙
        if(HeadsetPlugReceiver.isBluetoothOn()){
            audioManager.stopBluetoothSco();
            audioManager.setBluetoothScoOn(false);
        }
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
        dbg.E("SYSEchoCancel","modeNormal mode="+audioManager.getMode()+",isOn="+audioManager.isSpeakerphoneOn());
    }

    /**
     * 录音回音消除逻辑
     * @param arr
     */
    public static void echo(byte[] arr){
        if(arr==null || arr.length!=BUF_SIZE || !isStartEcho || !isListen){
            dbg.E("echo","echo error, buf == null or arr.length!="+BUF_SIZE+" or isStartEcho="+isStartEcho+",isListen="+isListen);
        }else{
            byte[] tempout=new byte[ECHO_FRAME_SIZE*2];
            byte[] clearbuf=new byte[ECHO_FRAME_SIZE*2];
            for(int i=0; i<BUF_SIZE/(ECHO_FRAME_SIZE*2); i++){
                dbg.E("echo","process, echoframe="+echoframe);
                if(echoframe>1){//回音消除采样
                    System.arraycopy(arr, i*ECHO_FRAME_SIZE*2, tempout, 0, ECHO_FRAME_SIZE*2);
                    int ret = EchoCancel.process(tempout, ECHO_FRAME_SIZE*2, clearbuf);
                    System.arraycopy(clearbuf,0,arr,i*ECHO_FRAME_SIZE*2,ECHO_FRAME_SIZE*2);
                    echoframe-=2;
                    dbg.E("echo","process,ret="+ret+",echoframe="+echoframe);
                }else{//降噪处理
                    System.arraycopy(arr, i*ECHO_FRAME_SIZE*2, tempout, 0, ECHO_FRAME_SIZE*2);
                    int ret=EchoCancel.ns(tempout, ECHO_FRAME_SIZE*2, clearbuf, 1);
                    System.arraycopy(clearbuf,0,arr,i*ECHO_FRAME_SIZE*2,ECHO_FRAME_SIZE*2);
                    dbg.E("echo","ns,ret="+ret);
                }
            }
        }
    }

    /**
     * 采集从设备端过来的音频数据
     * @param arr
     * @return
     */
    public static void track(byte[] arr, AudioTrack track){
        if(!isStartEcho){
            if(isFullDuplex){
                byte[] agcArr = new byte[arr.length];
                int ret = agc(arr,arr.length,agcArr);
                dbg.E("track","NOR ret = " + ret+",length="+arr.length);
                track.write(ret==0?agcArr:arr,0,arr.length);
                //track.write(arr,0,arr.length);
            }else{
                dbg.E("track","NOR length="+arr.length);
                track.write(arr,0,arr.length);
            }
        }else{
            if(arr.length != BUF_SIZE){
                dbg.E("track","track arr.length != " + BUF_SIZE);
                track.write(arr,0,arr.length);
                return;
            }
            int position=0;
            int ret;
            byte[] agcArr = new byte[ECHO_FRAME_SIZE];
            while(position<BUF_SIZE){
                byte[] tArr = new byte[ECHO_FRAME_SIZE];
                if((ret=EchoCancel.vad(Arrays.copyOfRange(arr,position,position+ECHO_FRAME_SIZE),ECHO_FRAME_SIZE)) == 1){//是否有声音
                    System.arraycopy(arr,position,tArr,0,ECHO_FRAME_SIZE);
                    agc(tArr,ECHO_FRAME_SIZE,agcArr);
                    EchoCancel.capture(agcArr, ECHO_FRAME_SIZE);//回声样本采集
                    track.write(agcArr,0,ECHO_FRAME_SIZE);
                    EchoUtil.isListen = true;
                    echoframe++;
                    dbg.E("track","echoframe= " + echoframe+",ret="+ret+",position="+position);
                }else{
                    dbg.E("track", "vad error, ret="+ret+",position="+position);
                    System.arraycopy(arr,position,tArr,0,ECHO_FRAME_SIZE);
                    //track.write(agcArr,0,ECHO_FRAME_SIZE);
                    track.write(tArr,0,ECHO_FRAME_SIZE);
                }
                position+=ECHO_FRAME_SIZE;
            }
        }
    }


    /**
     * 录音回音消除逻辑
     * @param arr
     */
    public static void echo_(byte[] arr){
        if(arr==null||arr.length!=BUF_SIZE || !isStartEcho){
            dbg.E("echo","echo error, buf == null or arr.length!="+BUF_SIZE+" or isStartEcho="+isStartEcho);
        }else{
            byte[] tempout=new byte[ECHO_FRAME_SIZE];
            byte[] clearbuf=new byte[ECHO_FRAME_SIZE];
            for(int i=0; i<BUF_SIZE/ECHO_FRAME_SIZE; i++)
                if(echoframe>0){//回音消除采样
                    System.arraycopy(arr, i*ECHO_FRAME_SIZE, tempout, 0, ECHO_FRAME_SIZE);
                    int ret = EchoCancel.process(tempout, ECHO_FRAME_SIZE, clearbuf);
                    System.arraycopy(clearbuf,0,arr,i*ECHO_FRAME_SIZE,ECHO_FRAME_SIZE);
                    echoframe-=1;
                    dbg.D("echo","process,ret="+ret+",echoframe="+echoframe);
                }else{//降噪处理
                    System.arraycopy(arr, i*ECHO_FRAME_SIZE, tempout, 0, ECHO_FRAME_SIZE);
                    int ret=EchoCancel.ns(tempout, ECHO_FRAME_SIZE, clearbuf, 1);
                    System.arraycopy(clearbuf,0,arr,i*ECHO_FRAME_SIZE,ECHO_FRAME_SIZE);
                    dbg.D("echo","ns,ret="+ret);
                }
        }
    }

    /**
     * 采集从设备端过来的音频数据
     * @param arr
     * @return
     */
    public static void track_(byte[] arr, AudioTrack track){
        if(!isStartEcho){
            dbg.E("track","NOR length = " + arr.length);
            track.write(arr,0,arr.length);
        }else{
            if(arr.length != BUF_SIZE){
                dbg.E("track","track arr.length != " + BUF_SIZE);
                track.write(arr,0,arr.length);
                return;
            }
            int position=0;
            int ret;
            while(position<BUF_SIZE){
                if((ret=EchoCancel.vad(Arrays.copyOfRange(arr,position,position+ECHO_FRAME_SIZE),ECHO_FRAME_SIZE)) == 1){
                    EchoCancel.capture(Arrays.copyOfRange(arr,position,position+ECHO_FRAME_SIZE), ECHO_FRAME_SIZE);//回声样本采集
                    track.write(arr,position,ECHO_FRAME_SIZE);
                    echoframe++;
                    dbg.E("track","echoframe= " + echoframe+",ret="+ret+",position="+position);
                }else{
                    dbg.E("track", "vad error, ret="+ret+",position="+position);
                    track.write(arr,position,ECHO_FRAME_SIZE);
                }
                position+=ECHO_FRAME_SIZE;
            }
        }
    }
}
