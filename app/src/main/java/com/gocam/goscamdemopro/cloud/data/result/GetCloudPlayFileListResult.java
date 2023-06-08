package com.gocam.goscamdemopro.cloud.data.result;

import com.gocam.goscamdemopro.cloud.data.entity.CloudPlayInfo;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GetCloudPlayFileListResult extends PlatResult {

    protected List<CloudPlayInfo> data;
    protected long startTime = -1;
    protected long endTime = -1;
    protected int actionCode;

    public int handleCode;

    public GetCloudPlayFileListResult(int code, String json, int actionCode, long startTime, long endTime, int handleCode) {
        super(PlatCmd.getCloudStreamUrlList, 0, code, json);
        this.actionCode = actionCode;
        this.startTime = startTime;
        this.endTime = endTime;
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
                    if (jarr != null) {
                        CloudPlayInfo preCloudInfo = null;
                        for (int i = 0; i < jarr.length(); i++) {
                            JSONObject jTemp = jarr.getJSONObject(i);
                            CloudPlayInfo cloudPlayInfo = gson.fromJson(jTemp.toString(), CloudPlayInfo.class);
                            if (preCloudInfo != null && cloudPlayInfo.getKey().equals(preCloudInfo.getKey()))//去重
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
            } catch(Exception e){
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
}
