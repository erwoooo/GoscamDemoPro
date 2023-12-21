package com.icare.echo;

import android.content.Context;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;


public class EchoCancel {

    static {
        try {
            System.loadLibrary("IcareEchoCancel");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static AudioTrack createAudioTrack(int sampleRate, int channelConfig, int audioFormat,int nMinBufSize){
        // Log.i("SYSEchoCancel","m_AudioAec:"+m_AudioAec);
        return AudioAec.createAudioTrack(sampleRate,channelConfig,audioFormat,nMinBufSize);
    }

    public static AudioRecord createAudioRecorder(int sampleRate, int channelConfig, int audioFormat,int nMinBufSize){
        return AudioAec.createAudioRecorder(sampleRate,channelConfig,audioFormat,nMinBufSize);
    }

    public static void setplayertospeaker(Context m_Context){
        AudioAec.requestAudioFocus(m_Context);
    }

    public static void clearRecorder(){
        AudioAec.clearRecorder();
    }



    public static native int init(Context mContext,String apiKey,String license_key,int sample_rate);

    public static native int setConfig(int is_ns);

    public static native int capture(byte[] farend,int size);

    public static native int process(byte[] inBuffer,int size,byte[] outBuffer);

    public static native int ns(byte[] inbuf,int inSize,byte[] Clearbuf,int numofbands);

    public static native int vad(byte[] inbuf,int inSize);

    public static native int agc(byte[] inbuf,int inSize,byte outbuf[]);

    public static native int AgcConfig(int GaindB);

    public static native int destroy();

    public static native int enableAgc(int is_agc);

    public static native int getVersion();
}
