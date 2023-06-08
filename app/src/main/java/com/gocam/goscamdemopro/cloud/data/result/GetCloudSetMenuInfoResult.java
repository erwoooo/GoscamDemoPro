package com.gocam.goscamdemopro.cloud.data.result;

import android.text.TextUtils;

import com.gocam.goscamdemopro.cloud.data.entity.CloudSetMenuInfo;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetCloudSetMenuInfoResult extends PlatResult {
    public CloudSetMenuInfo data;
    public int handleCode;
    public String deviceId;

    public GetCloudSetMenuInfoResult(int code, String json, String deviceId, int handleCode) {
        super(PlatCmd.getCloudSetMenuInfo, 0, code, json);
        this.deviceId = deviceId;
        this.handleCode = handleCode;
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
                    JSONArray jarr = null;
                    if (jsonObject.has("data")) {
                        jarr = jsonObject.getJSONArray("data");
                    }
                    if (jarr != null) {
                        data = gson.fromJson(jarr.getJSONObject(jarr.length() - 1).toString(), CloudSetMenuInfo.class);
//                        dbg.D("GetCloudSetMenuInfoResult", "response >>> mFlag=" + mFlag);
                        //if (!this.mFlag.equals(Tag) && jarr.length() > 1) {
                        if (jarr.length() > 1) {
                            CloudSetMenuInfo oldMenuInfo = gson.fromJson(jarr.getJSONObject(0).toString(), CloudSetMenuInfo.class);
                            data.setDateLife(oldMenuInfo.getDateLife());
                            data.setStartTime(oldMenuInfo.getStartTime());
                        }
//                        long startTime = DateUtils.stringToTime(DateUtils.Pattern_yyyyMMddHHmmss, data.getStartTime());
//                        long invalidTime = DateUtils.stringToTime(DateUtils.Pattern_yyyyMMddHHmmss, data.getPreinvalidTime());
//                        long expiredTime = DateUtils.stringToTime(DateUtils.Pattern_yyyyMMddHHmmss, data.getDataExpiredTime());
//                        data.setServiceStartTime(startTime / 1000);
//                        data.setServiceInvalidTime(invalidTime / 1000);
//                        data.setServiceDataExpiredTime(expiredTime / 1000);

                        long startTime = Long.parseLong(data.getStartTime());
                        long invalidTime = Long.parseLong(data.getPreinvalidTime());
                        long expiredTime = Long.parseLong(data.getDataExpiredTime());
                        data.setServiceStartTime(startTime);
                        data.setServiceInvalidTime(invalidTime);
                        data.setServiceDataExpiredTime(expiredTime);
                    }
                } else {
                    responseCode = code;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
