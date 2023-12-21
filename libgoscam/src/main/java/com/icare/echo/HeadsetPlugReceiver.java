package com.icare.echo;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;
import ulife.goscam.com.loglib.dbg;

public class HeadsetPlugReceiver extends BroadcastReceiver {
    public static final String ACTION_PLUG = "android.intent.action.HEADSET_PLUG";
    public static final String ACTION_NOISY = "android.media.AUDIO_BECOMING_NOISY";
    private static boolean wiredheadOn;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(TextUtils.equals(intent.getAction(),ACTION_PLUG)){
            if(intent.hasExtra("state")){
                int state = intent.getIntExtra("state", -1);
                dbg.D("HeadsetPlugReceiver","插入:::"+state);
                if(state==1){
                    //wiredheadOn = true;
                    //dbg.D("HeadsetPlugReceiver","插入");
                    setSpeakerphoneOn(context,false);
                }
            }
        }else if(TextUtils.equals(intent.getAction(),ACTION_NOISY)){
            //wiredheadOn = false;
            dbg.D("HeadsetPlugReceiver","拔出");
            //拔出判断蓝牙
            if(!isBluetoothOn()){
                setSpeakerphoneOn(context,true);
            }else{
                setSpeakerphoneOn(context,false);
            }


        }else if(TextUtils.equals(intent.getAction(), BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)){
            int bluetoothHeadsetState = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE,
                    BluetoothHeadset.STATE_DISCONNECTED);
            dbg.D("HeadsetPlugReceiver","蓝牙 ACTION, bluetoothHeadsetState="+ bluetoothHeadsetState);
            if (bluetoothHeadsetState == BluetoothProfile.STATE_CONNECTED) {
                dbg.D("HeadsetPlugReceiver","蓝牙 CONNECTED");
                setSpeakerphoneOn(context,false);
            } else if (bluetoothHeadsetState == BluetoothProfile.STATE_DISCONNECTED) {
                dbg.D("HeadsetPlugReceiver","蓝牙 DISCONNECTED"); //关闭
                if(!isWiredHeadsetOn(context)){
                    setSpeakerphoneOn(context,true);
                }
            }
        }
    }

    //audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    public static void setSpeakerphoneOn(Context context, boolean isOn){
        try{
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            boolean isSpeakerphoneOn = audioManager.isSpeakerphoneOn();
            dbg.D("SYSEchoCancel","isSpeakerphoneOn="+isSpeakerphoneOn+
                    ",isOn="+isOn+",isFullDuplex="+EchoUtil.isFullDuplex+",mode="+audioManager.getMode());
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            //audioManager.setMode(EchoUtil.isFullDuplex?AudioManager.MODE_IN_COMMUNICATION:AudioManager.MODE_NORMAL);
            dbg.D("SYSEchoCancel","mode=="+audioManager.getMode());
            if(isBluetoothOn()){
                audioManager.setBluetoothScoOn(true);
                audioManager.startBluetoothSco();
            } else{
                //audioManager.setMode(isOn?AudioManager.MODE_NORMAL:AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setBluetoothScoOn(false);
                audioManager.stopBluetoothSco();
            }
            if(isSpeakerphoneOn!=isOn){
                audioManager.setSpeakerphoneOn(isOn);
            }
           // audioManager.setSpeakerphoneOn(false);

//            if(EchoUtil.isFullDuplex&&isSpeakerphoneOn){
//                audioManager.setSpeakerphoneOn(false);//不开启扬声器
//            }else if(!EchoUtil.isFullDuplex&&!isSpeakerphoneOn){
//                audioManager.setSpeakerphoneOn(true);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isWiredHeadsetOn(Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        return audioManager.isWiredHeadsetOn();
    }

    public static boolean isBluetoothOn(){
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
//      int isBlueCon;//蓝牙适配器是否存在，即是否发生了错误
        if (ba == null){
            return false;
        }
        else if(ba.isEnabled()) {
            int a2dp = ba.getProfileConnectionState(BluetoothProfile.A2DP);              //可操控蓝牙设备，如带播放暂停功能的蓝牙耳机
            int headset = ba.getProfileConnectionState(BluetoothProfile.HEADSET);        //蓝牙头戴式耳机，支持语音输入输出
            int health = ba.getProfileConnectionState(BluetoothProfile.HEALTH);          //蓝牙穿戴式设备

            //查看是否蓝牙是否连接到三种设备的一种，以此来判断是否处于连接状态还是打开并没有连接的状态
            int flag = -1;
            if (a2dp == BluetoothProfile.STATE_CONNECTED) {
                flag = a2dp;
            } else if (headset == BluetoothProfile.STATE_CONNECTED) {
                flag = headset;
            } else if (health == BluetoothProfile.STATE_CONNECTED) {
                flag = health;
            }
            //说明连接上了三种设备的一种
            if (flag != -1) {
                return true;
            }
        }
        return false;
    }

    private static int getheadsetStatsu(AudioManager audoManager){
        if(audoManager.isWiredHeadsetOn()){
            return 1;
        }
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
//      int isBlueCon;//蓝牙适配器是否存在，即是否发生了错误
        if (ba == null){
            return -1;
        }
        else if(ba.isEnabled()) {
            int a2dp = ba.getProfileConnectionState(BluetoothProfile.A2DP);              //可操控蓝牙设备，如带播放暂停功能的蓝牙耳机
            int headset = ba.getProfileConnectionState(BluetoothProfile.HEADSET);        //蓝牙头戴式耳机，支持语音输入输出
            int health = ba.getProfileConnectionState(BluetoothProfile.HEALTH);          //蓝牙穿戴式设备

            //查看是否蓝牙是否连接到三种设备的一种，以此来判断是否处于连接状态还是打开并没有连接的状态
            int flag = -1;
            if (a2dp == BluetoothProfile.STATE_CONNECTED) {
                flag = a2dp;
            } else if (headset == BluetoothProfile.STATE_CONNECTED) {
                flag = headset;
            } else if (health == BluetoothProfile.STATE_CONNECTED) {
                flag = health;
            }
            //说明连接上了三种设备的一种
            if (flag != -1) {
                return 2;
            }
        }
        return -2;
    }

}