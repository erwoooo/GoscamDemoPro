package com.gocam.goscamdemopro.vphoto.data;

import static com.gos.platform.api.result.PlatResult.PlatCmd.deleteVPhotoDevice;

import android.util.Log;

import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;

import org.json.JSONException;
import org.json.JSONObject;

public class DeleteDeviceResult extends PlatResult {
    private final String TAG = this.getClass().getSimpleName();

    private DeleteBean deleteBean;

    public DeleteDeviceResult(int code, String json){
        super(deleteVPhotoDevice, 0, code, json);
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
                }
                JSONObject dataArray = bodyJsonObject.getJSONObject("data");
                deleteBean = gson.fromJson(dataArray.toString(), DeleteBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public DeleteBean getDeleteBean() {
        return deleteBean;
    }

    public static class DeleteBean {
        private String message;
        private String nowstatus;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getNowstatus() {
            return nowstatus;
        }

        public void setNowstatus(String nowstatus) {
            this.nowstatus = nowstatus;
        }
    }
}
