package com.gocam.goscamdemopro.vphoto.data;

import static com.gos.platform.api.result.PlatResult.PlatCmd.deviceCamVPhoto;

import android.util.Log;

import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceCamResult extends PlatResult {
    private final String TAG = this.getClass().getSimpleName();

    private String message;

    public DeviceCamResult(int code, String json) {
        super(deviceCamVPhoto, 0, code, json);
    }

    @Override
    protected void response(String json) {
        super.response(json);
        Log.e(TAG, "response: " + json );
        if (responseCode == 200){
            try {
                JSONObject bodyJsonObject = new JSONObject(json);
                String code = bodyJsonObject.optString("status");
                if ("200".equals(code)) {
                    responseCode = PlatCode.SUCCESS;
                } else {
                    responseCode = Integer.parseInt(code);
                }
                JSONObject dataArray = bodyJsonObject.getJSONObject("data");
                message = dataArray.optString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getMessage() {
        return message;
    }
}
