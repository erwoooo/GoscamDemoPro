package com.gocam.goscamdemopro.talk;

import com.gocam.goscamdemopro.utils.dbg;
import com.gos.platform.device.base.Connection;
import com.gocam.goscamdemopro.play.TalkPlay;


/**
 * 流的方式实现对讲
 */

public abstract class StreamTalkPlay extends TalkPlay {
    public StreamTalkPlay(Connection conn, int param) {
        super(0, conn, param,TalkPlay.STREAM_TYPE);
    }

    public StreamTalkPlay(int channel , Connection conn, int param) {
        super(channel, conn, param,TalkPlay.STREAM_TYPE);
    }

    @Override
    public boolean startTalk() {
        return super.startTalk();
    }

    @Override
    public void aacData(byte[] data, int dataLen, int param) {
        //conn.sendTalkData(devChn,data,dataLen);
        dbg.d("StreamTalkPlay","data="+data+"   datalen="+dataLen+"   param"+param);
        conn.sendTalkData(devChn,53,8000,0,data,dataLen);
    }

    @Override
    public synchronized boolean stopTalk() {
        super.stopTalk();
        //if(isTalkConnected){
            isTalkConnected = false;
            conn.stopTalk(0);
        //}
        return true;
    }
}
