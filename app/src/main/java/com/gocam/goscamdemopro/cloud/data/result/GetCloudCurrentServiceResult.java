package com.gocam.goscamdemopro.cloud.data.result;

import android.text.TextUtils;

import com.gocam.goscamdemopro.cloud.data.entity.CloudSetMenuInfo;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;


import org.json.JSONException;
import org.json.JSONObject;


public class GetCloudCurrentServiceResult extends PlatResult {
    protected CloudSetMenuInfo data;
    public GetCloudCurrentServiceResult(int code, String json) {
        super(PlatCmd.getCloudCurrentService, 0, code, json);
    }
    @Override
    protected void response(String json) {
        if (responseCode == 200) {
            if (TextUtils.isEmpty(json))
                return;
            try {
                JSONObject jsonObject = new JSONObject(json);
                int code = -1;
                if (jsonObject.has("code"))
                    code = jsonObject.getInt("code");
                if (code == 0) {
                    responseCode = PlatCode.SUCCESS;
                    JSONObject dataJsonObject = null;
                    if (jsonObject.has("data")) {
                        dataJsonObject = jsonObject.getJSONObject("data");
                    }
                    if (dataJsonObject != null) {

                        data = gson.fromJson(dataJsonObject.toString(), CloudSetMenuInfo.class);
//                        long startTime = DateUtils.stringToTime(DateUtils.Pattern_yyyyMMddHHmmss, data.getStartTime());
//                        long invalidTime = DateUtils.stringToTime(DateUtils.Pattern_yyyyMMddHHmmss, data.getPreinvalidTime());
//                        long expiredTime = DateUtils.stringToTime(DateUtils.Pattern_yyyyMMddHHmmss, data.getDataExpiredTime());
//                        data.setServiceStartTime(startTime / 1000);
//                        data.setServiceInvalidTime(invalidTime / 1000);
//                        data.setServiceDataExpiredTime(expiredTime / 1000);
                    }
                } else {
                    responseCode = code;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public CloudSetMenuInfo getData() {
        return this.data;
    }
}
