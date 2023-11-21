package com.gocam.goscamdemopro.tf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.gocam.goscamdemopro.GApplication;
import com.gocam.goscamdemopro.R;
import com.gocam.goscamdemopro.base.BaseBindActivity;
import com.gocam.goscamdemopro.databinding.ActivityTfDayBinding;
import com.gocam.goscamdemopro.entity.Device;
import com.gocam.goscamdemopro.utils.DeviceManager;
import com.gocam.goscamdemopro.utils.FileUtils;
import com.gocam.goscamdemopro.utils.GlideRoundTransform;
import com.gocam.goscamdemopro.utils.Packet;
import com.gos.platform.api.contact.ResultCode;
import com.gos.platform.device.domain.AvFrame;
import com.gos.platform.device.domain.StRecordInfo;
import com.gos.platform.device.inter.IVideoPlay;
import com.gos.platform.device.inter.OnDevEventCallback;
import com.gos.platform.device.result.DevResult;
import com.gos.platform.device.result.GetRecDayEventRefreshResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TfDayFileActivity extends BaseBindActivity<ActivityTfDayBinding> implements OnDevEventCallback {
    TextView mTvTitle;
    ImageView ivBack;
    RecyclerView mRecycleView;
    String mDevId;
    Device mDevice;
    TfDayFileAdapter mTfDayFileAdapter;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String dayTime;
    private  int type = 0;
    public static void startActivity(Activity activity, String deviceId, String dayTime, int type){
        Intent intent = new Intent(activity, TfDayFileActivity.class);
        intent.putExtra("DEV_ID", deviceId);
        intent.putExtra("DAY_TIME", dayTime);
        intent.putExtra("TYPE", type);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tf_day;
    }

    @Override
    public void onCreateData(@Nullable Bundle bundle) {
        mTvTitle = findViewById(R.id.text_title);
        mRecycleView = findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);
        mTfDayFileAdapter = new TfDayFileAdapter();
        mRecycleView.setAdapter(mTfDayFileAdapter);
        ivBack = findViewById(R.id.back_img);
        ivBack.setOnClickListener(v->{
            finish();
        });
        mDevId = getIntent().getStringExtra("DEV_ID");
         dayTime = getIntent().getStringExtra("DAY_TIME");
        type = getIntent().getIntExtra("TYPE", 0);
        mTvTitle.setText(type == 0 ? R.string.record_file : R.string.alarm_file);

        mDevice = DeviceManager.getInstance().findDeviceById(mDevId);
        mDevice.getConnection().addOnEventCallbackListener(this);
//        showLoading();
//        mDevice.getConnection().connect(0);
        if (mDevice.getConnection().isConnected()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            long startTime = 0;
            try {
                startTime = dateFormat.parse(dayTime).getTime() / 1000;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long endTime = startTime + 24 * 3600;
            if(type == 0){
                mDevice.getConnection().getRecDayEventRefresh(0, (int) startTime, 0, 0);
            }else{
                mDevice.getConnection().getRecDayEventRefresh(0, (int) startTime, 1, 0);
            }
        }else {
            mDevice.getConnection().connect(0);
        }
        //获取预览图
        mPreHandlerThread = new HandlerThread("Pre_Thread");
        mPreHandlerThread.start();
        mPreHandler = new PreHandler(mPreHandlerThread.getLooper());
        openRecJpeg();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreHandler != null) {
            mPreHandler.setWait(true);
            mPreHandlerThread.quit();
            mPreHandler.removeCallbacksAndMessages(null);
        }
        closeRecJpeg();
        mDevice.getConnection().removeOnEventCallbackListener(this);
    }

    int mEventListSize = -1;
    List<StRecordInfo> mEventList = new ArrayList<>();
    @Override
    public void onDevEvent(String s, DevResult devResult) {
        DevResult.DevCmd cmd = devResult.getDevCmd();
        int code = devResult.getResponseCode();
        dismissLoading();
        switch (cmd){
            case getRecDayEventRefresh:
                if(ResultCode.SUCCESS == code){
                    GetRecDayEventRefreshResult getAllRecordListResult = (GetRecDayEventRefreshResult) devResult;
                    List<StRecordInfo> list = getAllRecordListResult.stInfo;
                    if (list.isEmpty()) {
                        mEventListSize = getAllRecordListResult.totalNum;
                    } else {
                        mEventList.addAll(list);
                        mTfDayFileAdapter.notifyDataSetChanged();
                    }
                    if (mEventListSize != -1 && mEventList.size() == mEventListSize) {
                        dismissLoading();
                    }
                }else{
                    dismissLoading();
                    showToast("error code="+code);
                }
                break;
            case connect:
                dismissLoading();
                try {
                    showLoading();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    long startTime = dateFormat.parse(dayTime).getTime() / 1000;
                    long endTime = startTime + 24 * 3600;
                    if(type == 0){
                        mDevice.getConnection().getRecDayEventRefresh(0, (int) startTime, 0, 0);
                    }else{
                        mDevice.getConnection().getRecDayEventRefresh(0, (int) startTime, 1, 0);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
    }



    class TfDayFileAdapter extends RecyclerView.Adapter<TfDayFileAdapter.Vh>{

        @NonNull
        @Override
        public TfDayFileAdapter.Vh onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_day_file, viewGroup, false);
            return new Vh(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TfDayFileAdapter.Vh vh, int i) {
            final StRecordInfo stRecordInfo = mEventList.get(i);
            vh.tv.setText(stRecordInfo.startTimeStamp+"--"+stRecordInfo.endTimeStamp
                    +",type="+stRecordInfo.alarmType
                    +",total="+(Integer.parseInt(stRecordInfo.endTimeStamp)-Integer.parseInt(stRecordInfo.startTimeStamp))+"\n"
                    +dateFormat.format(new Date(Integer.parseInt(stRecordInfo.startTimeStamp)*1000l))
                    +"-"+dateFormat.format(new Date(Integer.parseInt(stRecordInfo.endTimeStamp)*1000l)));

            String filePath = FileUtils.getTfPreviewPicPath(GApplication.app.user.getUserName(), mDevice.devId)
                    + File.separator + stRecordInfo.startTimeStamp + ".jpg";
            File file = new File(filePath);
            if (file.exists()) {
                Glide.with(GApplication.app).load(filePath).override(400, 200)
                        .placeholder(R.drawable.img_events_playback_default)
                        .transform(new FitCenter(vh.ivThumb.getContext()),
                                new GlideRoundTransform(vh.ivThumb.getContext(), 5))
                        .error(R.drawable.img_events_playback_default)
                        .into(vh.ivThumb);
            } else {
                vh.ivThumb.setImageResource(R.drawable.img_events_playback_default);
                mPreHandler.sendGet(stRecordInfo);
            }
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TfFilePlayActivity.startActivity(TfDayFileActivity.this, mDevId, stRecordInfo);
//                    TestThumbActivity.startActivity(TfDayFileActivity.this, mDevId,stRecordInfo.startTimeStamp);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mEventList.size();
        }

        class Vh extends RecyclerView.ViewHolder {
            TextView tv;
            ImageView ivThumb;
            public Vh(@NonNull View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv);
                ivThumb = itemView.findViewById(R.id.iv_thumb);
            }
        }
    }

    private void openRecJpeg() {
        int timestamp = (int) (System.currentTimeMillis() / 1000L);// IPC 为unsigned int, so +24, timezone > 0;
        int timezone = mDevice.getVerifyTimezone();
        mDevice.getConnection().openRecJpeg(0, mDevice.getStreamPsw(), timestamp, timezone, videoPlay);
    }

    private void closeRecJpeg() {
        mDevice.getConnection().closeRecJpeg(0, videoPlay);
    }

    Map<Long, Long> waitGetPreMap = new HashMap<>();
    HandlerThread mPreHandlerThread;
    PreHandler mPreHandler;

    class PreHandler extends Handler {
        LinkedList<Long> queue;
        List<Long> infos = new ArrayList<>();
        boolean isWait = false;

        public PreHandler(Looper looper) {
            super(looper);
            queue = new LinkedList<>();
        }

        public void sendGet(StRecordInfo event) {
            synchronized (this) {
                Long startTime = Long.parseLong(event.startTimeStamp);
                if (queue.contains(startTime)) {
                    queue.remove(startTime);
                }
                Long aLong = waitGetPreMap.get(startTime);
                //已请求未超时
                if (aLong != null && System.currentTimeMillis() - aLong < 7000) {
                    return;
                }
                waitGetPreMap.put(startTime, System.currentTimeMillis());
                queue.addFirst(startTime);
            }
            if (!isWait)
                sendEmptyMessageDelayed(100, 100);
        }

        private int[] removeFirst() {
            synchronized (this) {
                infos.clear();
                for (int i = 0; queue.size() > 0 && i < 10; i++) {
                    Long time = queue.removeFirst();
                    String deviceId = mDevice.devId;
                    String filePath = FileUtils.getTfPreviewPicPath(GApplication.app.user.getUserName(), deviceId)
                            + File.separator + time.longValue() + ".jpg";
                    File file = new File(filePath);
                    if (!file.exists()) {
                        infos.add(time);
                    } else {
                        waitGetPreMap.remove(time);
                    }
                }
                if (infos.size() == 0)
                    return null;
                int[] buf = new int[infos.size()];
                for (int i = 0; i < infos.size(); i++) {
                    buf[i] = (int) infos.get(i).longValue();
                }
                return buf;
            }
        }

        public void setWait(boolean isWait) {
            this.isWait = isWait;
            if (!isWait)
                sendEmptyMessage(100);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isFinishing()) {
                return;
            }
            int[] buf;
            while (!isWait && (buf = removeFirst()) != null) {
                mDevice.getConnection().getRecJpeg(0, buf, buf.length);
            }
        }
    }

    IVideoPlay videoPlay = new IVideoPlay() {

        @Override
        public void onVideoStream(String s, AvFrame avFrame) {
            //nFrameNo=1,nFrameType=102,nTimestamp=1592964007,nReserved=1,nDataSize=7035
            int nFrameType = Packet.byteArrayToInt_Little(avFrame.data, 4);//对应这个帧类型102
            final int nTimestamp = Packet.byteArrayToInt_Little(avFrame.data, 16);//对应视频时间戳
            int nDataSize = Packet.byteArrayToInt_Little(avFrame.data, 28);//数据长度
            Log.e("videoPlay", "onVideoStream: nFrameType= " + nFrameType );
            if (nFrameType != 102) {
                return;
            }

            String filePath = FileUtils.getTfPreviewPicPath(GApplication.app.user.getUserName(), mDevice.devId)
                    + File.separator + nTimestamp + ".jpg";
            File file = new File(filePath);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(avFrame.data, 32, nDataSize);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (isFinishing()) {
                return;
            }

            try {
                synchronized (mPreHandler) {
                    waitGetPreMap.remove(nTimestamp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFinishing()) {
                        return;
                    }

                    for (int i = 0; mEventList != null && i < mEventList.size(); i++) {
                        StRecordInfo event = mEventList.get(i);
                        if (TextUtils.equals(event.startTimeStamp , nTimestamp + "")) {
                            //TODO 获取到缩略图
                            if (mTfDayFileAdapter != null)
                                mTfDayFileAdapter.notifyDataSetChanged();

                            break;
                        }
                    }
                }
            });
        }
    };

}
