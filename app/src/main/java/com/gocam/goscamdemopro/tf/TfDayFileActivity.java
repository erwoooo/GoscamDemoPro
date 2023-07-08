package com.gocam.goscamdemopro.tf;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocam.goscamdemopro.R;
import com.gocam.goscamdemopro.base.BaseActivity;
import com.gocam.goscamdemopro.base.BaseBindActivity;
import com.gocam.goscamdemopro.databinding.ActivityTfDayBinding;
import com.gocam.goscamdemopro.entity.Device;
import com.gocam.goscamdemopro.utils.DeviceManager;
import com.gos.platform.api.contact.ResultCode;
import com.gos.platform.device.domain.StRecordInfo;
import com.gos.platform.device.inter.OnDevEventCallback;
import com.gos.platform.device.result.DevResult;
import com.gos.platform.device.result.GetRecDayEventRefreshResult;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        showLoading();
        mDevice.getConnection().connect(0);




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TfFilePlayActivity.startActivity(TfDayFileActivity.this, mDevId, stRecordInfo);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mEventList.size();
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
