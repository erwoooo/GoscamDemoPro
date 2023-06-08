package com.gocam.goscamdemopro.cloud.data.result;

import com.gocam.goscamdemopro.cloud.data.entity.OssInfo;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;


import org.json.JSONException;
import org.json.JSONObject;

public class GetCloudOssInfoResult extends PlatResult {
    public OssInfo data;
    public String deviceId;
    public int handleCode;

    public GetCloudOssInfoResult(int code, String json, String deviceId, int handleCode) {
        super(PlatCmd.getCloudOssInfo, 0, code, json);
        this.deviceId = deviceId;
        this.handleCode = handleCode;
    }

    @Override
    protected void response(String json) {
        if (responseCode == 200) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int code = -1;
                if (jsonObject.has("code"))
                    code = jsonObject.getInt("code");
                String jsonData = null;
                if (jsonObject.has("data")) {
                    jsonData = jsonObject.getJSONObject("data").toString();
                }
                if (code == 0) {
                    responseCode = PlatCode.SUCCESS;
                    data = gson.fromJson(jsonData, OssInfo.class);

                } else {
                    responseCode = code;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
