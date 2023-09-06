package com.gocam.goscamdemopro.play;

import android.media.AudioRecord;

import com.gocam.goscamdemopro.utils.dbg;
import com.icare.echo.EchoCancel;
import com.icare.echo.EchoUtil;


public abstract class GAudioRecord {
    private final String TAG="GAudioRecord";
    private int mFrequency;//采样率
    private int mChannel;//声道
    private int mSampBit;//采样精度
    private AudioRecord mAudioRecord;
    private boolean recordTag;
    private int minBufferSize;
    private int bufSize; //read音频buffer的大小+
    private int talkType;//对讲方式

    public GAudioRecord(int frequency, int channel, int sampbit, int talkType){
        this.talkType = talkType;
        this.mFrequency = frequency;
        this.mChannel = channel;
        this.mSampBit = sampbit;
        minBufferSize = AudioRecord.getMinBufferSize(mFrequency, mChannel, mSampBit);
    }

    public void init(){
        try {
            dbg.D(TAG,"minBufferSize="+minBufferSize+",mFrequency="+mFrequency+",mChannel="+mChannel);
            mAudioRecord = EchoCancel.createAudioRecorder(mFrequency,mChannel,mSampBit,minBufferSize);
        } catch (Exception e) {
            dbg.E(TAG, "create audiorecord error");
        }
    }

    RecordThread mRecordThread;
    public void startRecord(){
        dbg.D("SYSEchoCancel","startRecord:"+mAudioRecord);
        mAudioRecord.startRecording();
        mRecordThread = new RecordThread();
        mRecordThread.setRecordTag(true);
        mRecordThread.start();
    }

    public void stopRecord(){
        dbg.D(TAG,"stopRecord:"+mRecordThread);
        if(mRecordThread!=null){
            mRecordThread.setRecordTag(false);
            //mAudioRecord.stop(); //三星手机兼容
        }
    }

    public void release(){
        dbg.D(TAG,"release");
        if(mAudioRecord != null){
            try {
                synchronized (GAudioRecord.this){
                    mAudioRecord.stop();
                    mAudioRecord.release();
                    EchoCancel.clearRecorder();
                }
            } catch (Exception e) {
            }finally{
                mAudioRecord = null;
                System.gc();
            }
        }
    }

    private class RecordThread extends Thread{
        private boolean recordTag;
        public void setRecordTag(boolean recordTag){
            this.recordTag = recordTag;
        }
        @Override
        public void run() {
            try {
                byte[] audioData = new byte[EchoUtil.BUF_SIZE];
                while (recordTag) {
                    if(mAudioRecord != null && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
                        int size = mAudioRecord.read(audioData, 0,audioData.length);
                        if (size == audioData.length) {
                            dataCallback(audioData,size);
                        }
                    }
                }
            } catch (Exception e) {
                dbg.E(TAG, "audio error::" + e.getMessage());
            }
        }
    }



    public abstract void dataCallback(byte[] data, int dataLen);
}
