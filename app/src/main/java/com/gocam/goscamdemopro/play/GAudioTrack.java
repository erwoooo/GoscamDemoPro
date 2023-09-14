package com.gocam.goscamdemopro.play;


import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Build;

import com.gocam.goscamdemopro.utils.dbg;
import com.icare.echo.AudioAec;
import com.icare.echo.EchoCancel;
import com.icare.echo.EchoUtil;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;



public class GAudioTrack implements Runnable{
	private int samplerate = 16000;// 设置音频数据的采样率  //32000
	private int channel;
	private int bitRate;
	private int minBuffSize;
	private AudioTrack audioTrack;
	private int PLAYSTATE = -1;
	private AudioData cacheData; //不过一个audio min size 的数据缓存
	private ArrayBlockingQueue<AudioData> queue = new ArrayBlockingQueue<>(60);

	
	public GAudioTrack(){
		this(16000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
		//this(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
	}

	public GAudioTrack(int sample, int channel, int bitRate){
		this.samplerate = sample;
		this.channel = channel;// 设置输出声道为双声道立体声 //AudioFormat.CHANNEL_CONFIGURATION_STEREO
		this.bitRate = bitRate;          // 设置音频数据块是8位还是16位  
		 // 声音文件一秒钟buffer的大小  
		//EchoCancel.setplayertospeaker(UlifeplusApp.app);
	}

	private void createTrack(){
		minBuffSize = AudioTrack.getMinBufferSize(samplerate, this.channel, this.bitRate);

			EchoCancel.clearRecorder(); // 每次创建audio前都要清除一下
			audioTrack = EchoCancel.createAudioTrack(samplerate, channel, bitRate, minBuffSize * 4);
			if(audioTrack!=null){
				audioTrack.play();
				if(isAdvanceStartRecord() && EchoUtil.isFullDuplex){
					AudioAec.recorder.startRecording();
				}
			}

		//audioTrack.setStereoVolume(0.3f,0.3f);
		dbg.I("audio","minBuffSize="+minBuffSize);
	}

	Thread bufThread;
	public synchronized void play(){
		if(PLAYSTATE != AudioTrack.PLAYSTATE_PLAYING){//audioTrack != null &&

			dbg.I("SYSEchoCancel","TRACK play");
			createTrack();
			PLAYSTATE = AudioTrack.PLAYSTATE_PLAYING;
			bufThread = new Thread(this);
			bufThread.start();
		}
	}

	@Override
	public void run() {
		while(audioTrack != null && PLAYSTATE == AudioTrack.PLAYSTATE_PLAYING && !Thread.currentThread().isInterrupted()){
			//synchronized (GAudioTrack.this) {
				if(audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
					try {
						AudioData audioData = queue.poll(5, TimeUnit.MILLISECONDS);
						if(audioData != null && audioData.data != null){
//							long time = System.currentTimeMillis();
							//AvPlayerCodec.nativePcmGain(audioData.data,5);
							EchoUtil.track(audioData.data, audioTrack);
//							dbg.D("audio_time", "write time = " + (System.currentTimeMillis() - time) + ";size="+queue.size());
						}else{
							byte[] emptydata=new byte[640];
							audioTrack.write(emptydata,0,emptydata.length);
							//dbg.I("audio", "queue is empty");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					dbg.I("audio", "play state is not playing");
				}
			//}
		}
		dbg.I("audio","audio thread exit..."+Thread.currentThread().getId());
	}
	
	public void write(byte[] data,final int dataLen){
		if(audioTrack != null && PLAYSTATE == AudioTrack.PLAYSTATE_PLAYING){
			AudioData audioData = new AudioData();
			audioData.data = new byte[dataLen];
			System.arraycopy(data, 0, audioData.data, 0, dataLen);
			audioData.len = dataLen;
			try {
//				dbg.I("audio_time", "put size = " + queue.size()+"; dataLen= " + dataLen);
				queue.put(audioData);
//				LogUtil.D("audio", "no cache buffer put : " + System.currentTimeMillis());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void pause(){
		PLAYSTATE = AudioTrack.PLAYSTATE_PAUSED;
		try {
			bufThread.interrupt();
		}catch (Exception e){
			e.printStackTrace();
		}
		if(audioTrack != null){
			queue.clear();
			EchoUtil.isListen = false;
			synchronized (this) {
				dbg.I("SYSEchoCancel","TRACK pause");

				try {
					audioTrack.stop();
				}catch (Exception e){
					e.printStackTrace();
				}
				try {
					audioTrack.release();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public void pauseback(){
		PLAYSTATE = AudioTrack.PLAYSTATE_PAUSED;
		if(audioTrack != null){
			synchronized (this) {
				dbg.I("SYSEchoCancel","TRACK pause");
				EchoUtil.isListen = false;
				queue.clear();
//				audioTrack.pause();//三星手机兼容
//				audioTrack.flush();
			}
		}
	}
	
	public void stop(){
		queue.clear();
		PLAYSTATE = AudioTrack.PLAYSTATE_STOPPED;
		try {
			if(bufThread != null)
				bufThread.interrupt();
		}catch (Exception e){
			e.printStackTrace();
		}
		if(audioTrack != null){
			synchronized (this) {
				try {
					dbg.I("SYSEchoCancel","TRACK stop");
					EchoUtil.isListen = false;
					audioTrack.stop();
				}catch (Exception e){e.printStackTrace();}
			}
		}
	}
	
	public void release(){
		queue.clear();
		try {
			if(bufThread != null)
				bufThread.interrupt();
		}catch (Exception e){
			e.printStackTrace();
		}
		if(audioTrack != null){
			synchronized (this) {
				try {
					dbg.I("SYSEchoCancel","TRACK release");
					EchoUtil.isListen = false;
					audioTrack.release();
				}catch (Exception e){e.printStackTrace();}
				PLAYSTATE = -1;
				audioTrack = null;
			}
		}
	}
	
	public int getMinBufferSize(){
		return minBuffSize;
	}
	
	public int getSamplerate(){
		return samplerate;
	}
	
	class AudioData{
		public byte[] data;
		public int len;
	}


	public static boolean isAdvanceStartRecord() {//播放声音，开启录像和不开启录音，播放响度不一致问题
		String model = Build.MODEL;
		boolean val = "PLK-TL01H".equals(model) || "SM-N9100".equals(model);
		dbg.D("Adcance", "model=" + model + ",val=" + val);
		return val;
	}

}
