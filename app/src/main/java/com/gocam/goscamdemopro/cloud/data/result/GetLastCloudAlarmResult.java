package com.gocam.goscamdemopro.cloud.data.result;

import com.gocam.goscamdemopro.cloud.data.entity.AlarmEvent;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetLastCloudAlarmResult extends PlatResult {
    protected List<AlarmEvent> data;

    public GetLastCloudAlarmResult(int code, String json) {
        super(PlatCmd.getLastCloudAlarm, 0, code, json);
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
                            data.add(new AlarmEvent(jTemp.getLong("timeStamp"), jTemp.getLong("timeStamp") + 20, jTemp.getInt("eventType"), jTemp.getString("eventDesc")));
                        }

                } else {
                    responseCode = code;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<AlarmEvent> getData() {
        return data;
    }


}
