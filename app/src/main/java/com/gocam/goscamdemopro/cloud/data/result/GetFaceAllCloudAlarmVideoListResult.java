package com.gocam.goscamdemopro.cloud.data.result;

import android.text.TextUtils;

import com.gocam.goscamdemopro.cloud.data.entity.AlarmVideoEvent;
import com.gocam.goscamdemopro.cloud.data.entity.CloudPlayInfo;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GetFaceAllCloudAlarmVideoListResult extends PlatResult {

    protected List<CloudPlayInfo> data;
    protected List<AlarmVideoEvent> alarmData;
    protected long startTime = -1;
    protected long endTime = -1;
    protected int actionCode;

    public int handleCode;

//    public GetFaceAllCloudAlarmVideoListResult(int code, String json, int actionCode, long startTime, long endTime, int handleCode) {
//        super(PlatCmd.getFaceAllCloudAlarmVideoList, 0, code, json);
//        this.actionCode = actionCode;
//        this.startTime = startTime;
//        this.endTime = endTime;
//        this.handleCode = handleCode;
//    }

    public GetFaceAllCloudAlarmVideoListResult(PlatCmd platCmd, int code, String json, int handleCode) {
        super(platCmd, 0, code, json);
        this.handleCode = handleCode;
    }

    @Override
    protected void response(String json) {
        data = new ArrayList<>();
        alarmData=new ArrayList<>();
        if (responseCode == 200) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int code = -1;
                if (jsonObject.has("code"))
                    code = jsonObject.getInt("code");
                if (code == 0) {
                    responseCode = PlatCode.SUCCESS;
                    JSONObject jso = jsonObject.getJSONObject("data");
                    JSONArray jarr = jso.getJSONArray("list");
                    if (jarr != null) {
                        CloudPlayInfo preCloudInfo = null;
                        for (int i = 0; i < jarr.length(); i++) {
                            JSONObject jTemp = jarr.getJSONObject(i);
                            long startTime = jTemp.getLong("startTime");
                            long endTime = jTemp.getLong("endTime");
                            int alarmType = 1;
                            if (jTemp.has("alarmType") && !TextUtils.isEmpty(jTemp.getString("alarmType")))
                                alarmType = jTemp.getInt("alarmType");
                            long preEndTime = -1;
                            long preAlarmType = -1;
                            AlarmVideoEvent preAlarmVideoEvent = null;
                            if (!alarmData.isEmpty()) {
                                preAlarmVideoEvent = alarmData.get(alarmData.size() - 1);
                                preEndTime = preAlarmVideoEvent.getEndTime();
                                preAlarmType = preAlarmVideoEvent.getEventTypeNum();
                            }
                            if (startTime == preEndTime) {
                                if (alarmType == preAlarmType)
                                    preAlarmVideoEvent.setEndTime(endTime);
                                else
                                    alarmData.add(new AlarmVideoEvent(startTime, endTime, alarmType));
                            } else if (startTime > preEndTime) {
                                alarmData.add(new AlarmVideoEvent(startTime, endTime, alarmType));
                            }

                            CloudPlayInfo cloudPlayInfo = gson.fromJson(jTemp.toString(), CloudPlayInfo.class);
                            String key = cloudPlayInfo.getKey();
                            cloudPlayInfo.setPicKey(key.replace(".H264",".pic"));

                            if (preCloudInfo != null && key.equals(preCloudInfo.getKey()))//去重
                                continue;
                            data.add(cloudPlayInfo);
                            preCloudInfo = cloudPlayInfo;
                        }
                    }
                } else {
                    responseCode = code;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<CloudPlayInfo> getData() {
        return data;
    }

    public int getActionCode() {
        return actionCode;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public List<AlarmVideoEvent> getAlarmData() {
        return alarmData;
    }
}
