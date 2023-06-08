package com.gocam.goscamdemopro.cloud.data.result;

import android.text.TextUtils;

import com.gocam.goscamdemopro.cloud.data.entity.AlarmVideoEvent;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetCloudAlarmVideoListResult extends PlatResult {
    protected List<AlarmVideoEvent> data;
    public int handleCode;

    public GetCloudAlarmVideoListResult(PlatCmd platCmd, int code, String json, int handleCode) {
        super(platCmd, 0, code, json);
        this.handleCode = handleCode;
    }

    @Override
    protected void response(String json) {
        data = new ArrayList<>();
        if (responseCode == 200) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int code = -1;
                if (jsonObject.has("code"))
                    code = jsonObject.getInt("code");
                if (code == 0) {
                    responseCode = PlatCode.SUCCESS;
                    JSONArray jarr = jsonObject.getJSONArray("data");
                    if (jarr != null)
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
                            if (!data.isEmpty()) {
                                preAlarmVideoEvent = data.get(data.size() - 1);
                                preEndTime = preAlarmVideoEvent.getEndTime();
                                preAlarmType = preAlarmVideoEvent.getEventTypeNum();
                            }
                            if (startTime == preEndTime) {
                                if (alarmType == preAlarmType)
                                    preAlarmVideoEvent.setEndTime(endTime);
                                else
                                    data.add(new AlarmVideoEvent(startTime, endTime, alarmType));
                            } else if (startTime > preEndTime) {
                                data.add(new AlarmVideoEvent(startTime, endTime, alarmType));
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

    public List<AlarmVideoEvent> getData() {
        return data;
    }


}
