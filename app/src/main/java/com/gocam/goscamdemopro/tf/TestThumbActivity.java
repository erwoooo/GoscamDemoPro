package com.gocam.goscamdemopro.tf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.gocam.goscamdemopro.GApplication;
import com.gocam.goscamdemopro.R;
import com.gocam.goscamdemopro.entity.Device;
import com.gocam.goscamdemopro.utils.DeviceManager;
import com.gocam.goscamdemopro.utils.FileUtils;
import com.gocam.goscamdemopro.utils.GlideRoundTransform;
import com.gocam.goscamdemopro.utils.Packet;
import com.gos.platform.device.domain.AvFrame;
import com.gos.platform.device.inter.IVideoPlay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestThumbActivity extends AppCompatActivity {
    private Device mDevice;
    private ImageView ivThumb;
    private EditText etTime;
    private String startTime;

    public static void startActivity(Context activity, String deviceId,String startTime ) {
        Intent intent = new Intent(activity, TestThumbActivity.class);
        intent.putExtra("devId", deviceId);
        intent.putExtra("time",startTime);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumb);
        Intent intent = getIntent();
        String deviceId = intent.getStringExtra("devId");
        startTime = intent.getStringExtra("time");
        mDevice = DeviceManager.getInstance().findDeviceById(deviceId);
        if (null == mDevice)
            finish();

        ivThumb = findViewById(R.id.iv_thumb);
        etTime = findViewById(R.id.et_time);

        mDevice.getConnection().connect(0);
        openRecJpeg();
        int buf[] = new int[1];
        findViewById(R.id.btn_thumb).setOnClickListener(v->{
            buf[0] = Integer.parseInt(etTime.getText().toString());  //get the start timestamp of a playback file
            mDevice.getConnection().getRecJpeg(0, buf, buf.length);
        });
    }
    private void openRecJpeg() {
        int timestamp = (int) (System.currentTimeMillis() / 1000L);// IPC 为unsigned int, so +24, timezone > 0;
        int timezone = mDevice.getVerifyTimezone();
        mDevice.getConnection().openRecJpeg(0, mDevice.getStreamPsw(), timestamp, timezone, new IVideoPlay() {
            @Override
            public void onVideoStream(String devId, AvFrame avFrame) {
                int nFrameType = Packet.byteArrayToInt_Little(avFrame.data, 4);//thumb = 102
                final int nTimestamp = Packet.byteArrayToInt_Little(avFrame.data, 16);//Corresponding video timestamp
                int nDataSize = Packet.byteArrayToInt_Little(avFrame.data, 28);//Data length
                Log.e("videoPlay", "onVideoStream: nFrameType= " + nFrameType + " nTimestamp= "+ nTimestamp + " nDataSize= "+ nDataSize);

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
                ivThumb.post(()->{
                    Glide.with(GApplication.app).load(filePath).override(400, 200)
                            .placeholder(R.drawable.img_events_playback_default)
                            .transform(new FitCenter(ivThumb.getContext()),
                                    new GlideRoundTransform(ivThumb.getContext(), 5))
                            .error(R.drawable.img_events_playback_default)
                            .into(ivThumb);
                });

            }
        });
    }
}
