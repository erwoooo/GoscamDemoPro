package com.gocam.goscamdemopro.cloud.data.result;

import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;

import org.json.JSONException;
import org.json.JSONObject;

public class GetPaypalTokenResult extends PlatResult {

    public String token;

    public GetPaypalTokenResult(int code, String json) {
        super(PlatCmd.getPaypalToken, 0, code, json);
    }
    @Override
    protected void response(String json) {
        if (responseCode == 200) {
            try {
                JSONObject bodyJsonObject = new JSONObject(json);
                String code = bodyJsonObject.optString("code");
                if ("0".equals(code)) {
                    responseCode = PlatCode.SUCCESS;
                    token = bodyJsonObject.optString("data");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
