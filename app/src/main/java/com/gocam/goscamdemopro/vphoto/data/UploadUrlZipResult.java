package com.gocam.goscamdemopro.vphoto.data;

import static com.gos.platform.api.result.PlatResult.PlatCmd.presignedUrlZip;

import android.util.Log;

import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;

import org.json.JSONException;
import org.json.JSONObject;

public class UploadUrlZipResult extends PlatResult {
    private final String TAG = this.getClass().getSimpleName();

    private UploadBean uploadBean;

    public UploadUrlZipResult(int code, String json){
        super(presignedUrlZip, 0, code, json);
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
                    JSONObject dataArray = bodyJsonObject.getJSONObject("data");
                    uploadBean = gson.fromJson(dataArray.toString(), UploadBean.class);
                } else {
                    responseCode = Integer.parseInt(code);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public UploadBean getUploadBean() {
        return uploadBean;
    }

    public static class UploadBean {
        private String preSignedUrl;
        private String url;
        private String key;
        private String session_id;

        public String getPreSignedUrl() {
            return preSignedUrl;
        }

        public void setPreSignedUrl(String preSignedUrl) {
            this.preSignedUrl = preSignedUrl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getSession_id() {
            return session_id;
        }

        public void setSession_id(String session_id) {
            this.session_id = session_id;
        }
    }
}
