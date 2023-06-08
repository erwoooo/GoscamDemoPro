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
import com.gocam.goscamdemopro.cloud.data.entity.CameraEvent;
import com.gocam.goscamdemopro.cloud.data.entity.DayTime;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudAlarmVideoListResult;
import com.gocam.goscamdemopro.databinding.ActivityCloudDayBinding;
import com.gocam.goscamdemopro.entity.User;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.inter.OnPlatformEventCallback;
import com.gos.platform.api.result.PlatResult;


import java.util.ArrayList;
import java.util.List;

public class CloudDayFileActivity extends BaseBindActivity<ActivityCloudDayBinding> implements OnPlatformEventCallback {
    TextView mTvTitle;
    String mDeviceId;
    long mStartTime;
    long mEndTime;
    RecyclerView mRecycleView;
    CloudDayFileAdapter mCloudDayFileAdapter;
    ImageView ivBack;
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
        }
    }

    List<CameraEvent> cameraEventList = new ArrayList<>();

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

        User user = GApplication.app.user;
        GosCloud.getCloud().addOnPlatformEventCallback(this);
        showLoading();
        GosCloud.getCloud().getAllCloudAlarmVideoList(mDeviceId, mStartTime, mEndTime, user.getToken(), user.getUserName(), 0);
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
