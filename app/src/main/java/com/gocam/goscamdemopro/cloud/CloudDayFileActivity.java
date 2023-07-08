package com.gocam.goscamdemopro.cloud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocam.goscamdemopro.GApplication;
import com.gocam.goscamdemopro.R;
import com.gocam.goscamdemopro.base.BaseBindActivity;
import com.gocam.goscamdemopro.cloud.data.GosCloud;
import com.gocam.goscamdemopro.cloud.data.entity.AlarmVideoEvent;
import com.gocam.goscamdemopro.cloud.data.entity.CameraEvent;
import com.gocam.goscamdemopro.cloud.data.entity.CloudPlayInfo;
import com.gocam.goscamdemopro.cloud.data.entity.DayTime;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudAlarmVideoListResult;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudPlayFileListResult;
import com.gocam.goscamdemopro.databinding.ActivityCloudDayBinding;
import com.gocam.goscamdemopro.entity.User;
import com.gocam.goscamdemopro.utils.dbg;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.inter.OnPlatformEventCallback;
import com.gos.platform.api.result.PlatResult;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CloudDayFileActivity extends BaseBindActivity<ActivityCloudDayBinding> implements OnPlatformEventCallback {
    private static final String TAG = "CloudDayFileActivity";
    TextView mTvTitle;
    String mDeviceId;
    long mStartTime;
    long mEndTime;
    RecyclerView mRecycleView;
    CloudDayFileAdapter mCloudDayFileAdapter;
    ImageView ivBack;
    int requestPage = 1;
    final String SORT = "desc";
    private boolean useRequestPage = false;
    User user = GApplication.app.user;
    public static void startActivity(Activity activity, String deviceId, DayTime dayTime){
        Intent intent = new Intent(activity, CloudDayFileActivity.class);
        intent.putExtra("DEV_ID", deviceId);
        intent.putExtra("START_TIME", dayTime.getStartTime());
        intent.putExtra("END_TIME", dayTime.getEndTime());
        activity.startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        GosCloud.getCloud().removeOnPlatformEventCallback(this);
    }

    @Override
    public void OnPlatformEvent(PlatResult platResult) {
        if(PlatResult.PlatCmd.getAllCloudAlarmVideoList == platResult.getPlatCmd()){
            dismissLoading();
            if(PlatCode.SUCCESS == platResult.getResponseCode()){
                GetCloudAlarmVideoListResult listResult = (GetCloudAlarmVideoListResult) platResult;
                cameraEventList.addAll(listResult.getData());
                mCloudDayFileAdapter.notifyDataSetChanged();
            }else{
                showToast("request failed, code="+platResult.getResponseCode());
            }
        }else if (PlatResult.PlatCmd.getCloudStreamUrlList == platResult.getPlatCmd()){

            if (PlatCode.SUCCESS == platResult.getRequestCode()){
                //分页加载
                GetCloudPlayFileListResult fileListResult = (GetCloudPlayFileListResult) platResult;
                dbg.D(TAG,"getPlayFileListByPage, st=" + mStartTime + ",et=" + mEndTime + ",code="
                        + fileListResult.getResponseCode() + ",requestPageNum=" + requestPage + "," + fileListResult.getData());
                List<CloudPlayInfo> resultData = fileListResult.getData();
                List<CameraEvent> cameraEvents = new ArrayList<>();

                for(int i = 0; resultData != null && i < resultData.size(); i++){
                    CloudPlayInfo cloudPlayInfo = resultData.get(i);
                    AlarmVideoEvent event = new AlarmVideoEvent(cloudPlayInfo.getStartTime(), cloudPlayInfo.getEndTime(), cloudPlayInfo.getAlarmType());
                    event.setObj(cloudPlayInfo);
                    cameraEvents.add(event);
                }
                if (requestPage == 1){  //first load
                    cameraEventList.clear();
                }
                Collections.sort(cameraEvents, new CloudComparator());
                cameraEventList.addAll(cameraEvents);
                Collections.sort(cameraEventList, new CloudComparator());

                if (resultData == null || resultData.size() == 0){
                    //get all data
                    dismissLoading();
                    mCloudDayFileAdapter.notifyDataSetChanged();
                }else {
                    requestPage ++;   // go on
                    GosCloud.getCloud().getPlayFileListByPage(mDeviceId, mStartTime, mEndTime, user.getToken(), user.getUserName(), 300,requestPage,SORT);
                }
            }
        }
    }

    List<CameraEvent> cameraEventList = new ArrayList<>();

    class CloudComparator implements Comparator<CameraEvent> {
        @Override
        public int compare(CameraEvent o1, CameraEvent o2) {
            long start1 = o1.getStartTime();
            long start2 = o2.getStartTime();
            if (start1 < start2) {
                return 1;
            } else if (start1 > start2) {
                return -1;
            }
            return 0;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_cloud_day;
    }

    @Override
    public void onCreateData(@Nullable Bundle bundle) {
        mTvTitle = findViewById(R.id.text_title);
        mTvTitle.setText(R.string.alarm_file);

        mRecycleView = findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);
        mCloudDayFileAdapter = new CloudDayFileAdapter();
        mRecycleView.setAdapter(mCloudDayFileAdapter);
        ivBack = findViewById(R.id.back_img);
        ivBack.setOnClickListener(v->{
            finish();
        });
        mDeviceId = getIntent().getStringExtra("DEV_ID");
        mStartTime = getIntent().getLongExtra("START_TIME", 0);
        mEndTime = getIntent().getLongExtra("END_TIME", 0);


        GosCloud.getCloud().addOnPlatformEventCallback(this);
        showLoading();

        if (useRequestPage){
            GosCloud.getCloud().getPlayFileListByPage(mDeviceId, mStartTime, mEndTime, user.getToken(), user.getUserName(), 300,requestPage,SORT);
        }else {
            GosCloud.getCloud().getAllCloudAlarmVideoList(mDeviceId, mStartTime, mEndTime, user.getToken(), user.getUserName(), 0);
        }


    }

    class CloudDayFileAdapter extends RecyclerView.Adapter<CloudDayFileAdapter.Vh>{

        @NonNull
        @Override
        public CloudDayFileAdapter.Vh onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cloud_day, viewGroup, false);
            return new Vh(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CloudDayFileAdapter.Vh vh, int i) {
            final CameraEvent cameraEvent = cameraEventList.get(i);
            vh.tv.setText("StartTime:"+cameraEvent.getStartTextTime()+"\n"
                    +"EndTime:"+cameraEvent.getEndTextTime()+"\n"
                    +"Type="+cameraEvent.getEventType());

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CloudFilePlayActivity.startActivity(CloudDayFileActivity.this, mDeviceId, cameraEvent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return cameraEventList.size();
        }

        class Vh extends RecyclerView.ViewHolder {
            TextView tv;
            public Vh(@NonNull View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv);
            }
        }
    }
}
