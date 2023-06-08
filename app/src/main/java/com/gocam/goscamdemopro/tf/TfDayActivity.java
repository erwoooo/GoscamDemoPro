package com.gocam.goscamdemopro.tf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocam.goscamdemopro.R;
import com.gocam.goscamdemopro.base.BaseActivity;
import com.gocam.goscamdemopro.base.BaseBindActivity;
import com.gocam.goscamdemopro.databinding.ActivityTfDayBinding;
import com.gocam.goscamdemopro.entity.Device;
import com.gocam.goscamdemopro.utils.DeviceManager;
import com.gos.platform.api.contact.ResultCode;
import com.gos.platform.device.domain.FileForMonth;
import com.gos.platform.device.inter.OnDevEventCallback;
import com.gos.platform.device.result.DevResult;
import com.gos.platform.device.result.GetFileForMonthResult;

import java.util.List;

public class TfDayActivity extends BaseBindActivity<ActivityTfDayBinding> implements OnDevEventCallback {
    TextView mTvTitle;
    String mDevId;
    Device mDevice;
    RecyclerView mRecycleView;
    TfDayAdapter mTfDayAdapter;
    private static final int GET_FILE_LIST_FAILED_RESP_CODE = 1;
    private static final int FILE_LIST_IS_EMPTY_RESP_CODE = 2;
    private static final int HAS_NO_TF_CARD_RESP_CODE = 3;

    public static void startActivity(Context activity, String deviceId){
        Intent intent = new Intent(activity, TfDayActivity.class);
        intent.putExtra("DEV_ID", deviceId);
        activity.startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDevice.getConnection().removeOnEventCallbackListener(this);
    }

    @Override
    public void onDevEvent(String s, DevResult devResult) {
        if(devResult.getDevCmd() == DevResult.DevCmd.getFileForMonth){
            dismissLoading();
            if(ResultCode.SUCCESS == devResult.getResponseCode()){
                GetFileForMonthResult result = (GetFileForMonthResult) devResult;
                forMonthList = result.fileForMoth.mothFile;
                mTfDayAdapter.notifyDataSetChanged();
            }else {
                switch (devResult.getResponseCode()) {
                    case GET_FILE_LIST_FAILED_RESP_CODE:
                        showToast(getResources().getString(R.string.list_failed));
                        break;
                    case FILE_LIST_IS_EMPTY_RESP_CODE:
                        showToast(getResources().getString(R.string.no_video_data));
                        break;
                    case HAS_NO_TF_CARD_RESP_CODE:
                        showToast(getResources().getString(R.string.no_sd_title));
                        break;
                    default:
                        showToast("File for month failed:"+devResult.getResponseCode());
                        break;
                }
            }
        }
    }

    List<FileForMonth.ForMonth> forMonthList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_tf_day;
    }

    @Override
    public void onCreateData(@Nullable Bundle bundle) {
        mTvTitle = findViewById(R.id.text_title);
        mTvTitle.setText(R.string.day_time);
        mRecycleView = findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);
        mTfDayAdapter = new TfDayAdapter();
        mRecycleView.setAdapter(mTfDayAdapter);

        mDevId = getIntent().getStringExtra("DEV_ID");
        mDevice = DeviceManager.getInstance().findDeviceById(mDevId);
        mDevice.getConnection().addOnEventCallbackListener(this);
        showLoading();
        mDevice.getConnection().getFileForMonth(0, 0);
    }

    class TfDayAdapter extends RecyclerView.Adapter<TfDayAdapter.Vh>{

        @NonNull
        @Override
        public TfDayAdapter.Vh onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tv, viewGroup, false);
            return new Vh(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TfDayAdapter.Vh vh, int i) {
            final FileForMonth.ForMonth forMonth = forMonthList.get(i);
            vh.tv.setText(forMonth.monthTime+"("+forMonth.fileNum+")");
            vh.btnRecords.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TfDayFileActivity.startActivity(TfDayActivity.this, mDevId, forMonth.monthTime, 0);
                }
            });
            vh.btnAlarms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TfDayFileActivity.startActivity(TfDayActivity.this, mDevId, forMonth.monthTime, 1);
                }
            });
        }

        @Override
        public int getItemCount() {
            return forMonthList == null ? 0 : forMonthList.size();
        }

        class Vh extends RecyclerView.ViewHolder {
            TextView tv;
            Button btnRecords;
            Button btnAlarms;
            public Vh(@NonNull View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv);
                btnRecords = itemView.findViewById(R.id.btn_records);
                btnAlarms = itemView.findViewById(R.id.btn_alarms);
            }
        }
    }
}
