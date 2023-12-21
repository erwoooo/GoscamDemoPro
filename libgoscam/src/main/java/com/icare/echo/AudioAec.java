package com.icare.echo;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioAec
{
	public static AudioRecord recorder = null;
	public static AudioTrack m_audioTrack=null;


	public static int requestAudioFocus(Context _context) {
		if(!HeadsetPlugReceiver.isWiredHeadsetOn(_context) && !HeadsetPlugReceiver.isBluetoothOn()){
			HeadsetPlugReceiver.setSpeakerphoneOn(_context,true);
		}else if(HeadsetPlugReceiver.isBluetoothOn()){
			HeadsetPlugReceiver.setSpeakerphoneOn(_context,false);
		}else if(HeadsetPlugReceiver.isWiredHeadsetOn(_context)){
			HeadsetPlugReceiver.setSpeakerphoneOn(_context,false);
		}
		return 0;
	}

	public  static AudioRecord createAudioRecorder(int sampleRate, int channelConfig, int audioFormat,int nMinBufSize){
		if(!EchoUtil.isFullDuplex){
			if(recorder==null){
				//int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
				int minBufferSize = nMinBufSize;
				recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, sampleRate, channelConfig, audioFormat, minBufferSize);
				//recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, channelConfig, audioFormat, minBufferSize);
				Log.i("SYSEchoCancel","NO_FULL minBufferSize="+minBufferSize+"::nMinBufSize="+nMinBufSize);
			}
			Log.i("SYSEchoCancel","NO_FULL recorder:"+recorder);
			return recorder;
		}else{
			if ((sampleRate!=8000 &&sampleRate!=16000)||(channelConfig!=AudioFormat.CHANNEL_IN_MONO&&channelConfig!=AudioFormat.CHANNEL_IN_STEREO)||audioFormat!=AudioFormat.ENCODING_PCM_16BIT||nMinBufSize==0){
				return null;
			}
			initRecorder(sampleRate,channelConfig,audioFormat,nMinBufSize);
			Log.i("SYSEchoCancel","recorder:"+recorder);
			return recorder;
		}
	}

	public static AudioTrack createAudioTrack(int sampleRate, int channelConfig, int audioFormat,int nMinBufSize){
		if(!EchoUtil.isFullDuplex){
			Log.i("SYSEchoCancel","NO_FULL sampleRate:"+sampleRate+"--channelConfig:"+channelConfig+"--audioFormat:"+audioFormat+"--nMinBufSize:"+nMinBufSize+"--recorder:"+recorder);
			createAudioRecorder(sampleRate,AudioFormat.CHANNEL_IN_MONO,audioFormat,nMinBufSize);
			m_audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRate, channelConfig, audioFormat, nMinBufSize, AudioTrack.MODE_STREAM, recorder.getAudioSessionId());
			//m_audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRate, channelConfig, audioFormat, nMinBufSize, AudioTrack.MODE_STREAM);
			//m_audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelConfig, audioFormat, nMinBufSize, AudioTrack.MODE_STREAM);
			return m_audioTrack;
		}else{
			if ((sampleRate!=8000 &&sampleRate!=16000)||(channelConfig!=AudioFormat.CHANNEL_OUT_MONO&&channelConfig!=AudioFormat.CHANNEL_OUT_STEREO)||audioFormat!=AudioFormat.ENCODING_PCM_16BIT||nMinBufSize==0){
				return null;
			}
			int rchannelConfig =0;
			if (channelConfig==AudioFormat.CHANNEL_OUT_MONO){
				rchannelConfig=AudioFormat.CHANNEL_IN_MONO;
			}else {
				rchannelConfig=AudioFormat.CHANNEL_IN_STEREO;
			}
			initRecorder(sampleRate,rchannelConfig,audioFormat,nMinBufSize);

			if (m_audioTrack == null) {
				m_audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRate, channelConfig, audioFormat, nMinBufSize, AudioTrack.MODE_STREAM, recorder.getAudioSessionId());
				Log.i("SYSEchoCancel","sampleRate:"+sampleRate+"--channelConfig:"+channelConfig+"--audioFormat:"+audioFormat+"--nMinBufSize:"+nMinBufSize+"--recorder:"+recorder+",track="+m_audioTrack);
			}
			//m_audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRate, channelConfig, audioFormat, nMinBufSize, AudioTrack.MODE_STREAM);
			return m_audioTrack;
		}
	}
	private static void initRecorder(int sampleRate, int channelConfig, int audioFormat,int nMinBufSize){
		if (recorder!=null){
			Log.i("SYSEchoCancel","initRecorder full has,"+recorder);
			return;
		}
		recorder=new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, sampleRate, channelConfig, audioFormat, nMinBufSize);
//		recorder=new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, nMinBufSize);
		Log.i("SYSEchoCancel","initRecorder full,"+recorder);
	}
	public static void clearRecorder(){
		if (recorder != null) {
			recorder=null;
		}
		if (m_audioTrack != null) {
			m_audioTrack = null;
		}
	}
}
