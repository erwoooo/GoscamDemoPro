package com.gocam.goscamdemopro.cloud.data.result;

import com.gocam.goscamdemopro.cloud.data.entity.PackageInfo;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;


import org.json.JSONException;
import org.json.JSONObject;

public class FreePlanResult extends PlatResult {

    PackageInfo packageInfo;

    public FreePlanResult(int code, String json) {
        super(PlatCmd.freePlan, 0, code, json);
    }

    @Override
    protected void response(String json) {
        if (responseCode == 200) {
            try {
                JSONObject bodyJsonObject = new JSONObject(json);
                String code = bodyJsonObject.optString("code");
                if ("0".equals(code)) {
                    responseCode = PlatCode.SUCCESS;
                    JSONObject jsonObject = bodyJsonObject.optJSONObject("data");
                    if (jsonObject != null) {
                        packageInfo = gson.fromJson(jsonObject.toString(), PackageInfo.class);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public PackageInfo getData() {
        return packageInfo;
    }

}
