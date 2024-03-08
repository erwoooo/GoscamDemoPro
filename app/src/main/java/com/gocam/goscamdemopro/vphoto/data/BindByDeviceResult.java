package com.gocam.goscamdemopro.vphoto.data;

import static com.gos.platform.api.result.PlatResult.PlatCmd.bindByDeviceConnection;

import android.util.Log;

import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;

import org.json.JSONException;
import org.json.JSONObject;

public class BindByDeviceResult extends PlatResult {
    private final String TAG = this.getClass().getSimpleName();

    private BindByDeviceBean deviceBean;

    public BindByDeviceResult(int code, String json) {
        super(bindByDeviceConnection, 0, code, json);
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
                deviceBean = gson.fromJson(dataArray.toString(), BindByDeviceBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public BindByDeviceBean getDeviceBean() {
        return deviceBean;
    }

    public static class BindByDeviceBean {
        private int device_id;
        private String device_connection_code;
        private String device_fcm_token;
        private String device_name;
        private String isAccepted;
        private String isAcceptNewUsers;
        private String isAdmin;
        private String message; // 错误信息

        public int getDevice_id() {
            return device_id;
        }

        public void setDevice_id(int device_id) {
            this.device_id = device_id;
        }

        public String getDevice_connection_code() {
            return device_connection_code;
        }

        public void setDevice_connection_code(String device_connection_code) {
            this.device_connection_code = device_connection_code;
        }

        public String getDevice_fcm_token() {
            return device_fcm_token;
        }

        public void setDevice_fcm_token(String device_fcm_token) {
            this.device_fcm_token = device_fcm_token;
        }

        public String getDevice_name() {
            return device_name;
        }

        public void setDevice_name(String device_name) {
            this.device_name = device_name;
        }

        public String getIsAccepted() {
            return isAccepted;
        }

        public void setIsAccepted(String isAccepted) {
            this.isAccepted = isAccepted;
        }

        public String getIsAcceptNewUsers() {
            return isAcceptNewUsers;
        }

        public void setIsAcceptNewUsers(String isAcceptNewUsers) {
            this.isAcceptNewUsers = isAcceptNewUsers;
        }

        public String getIsAdmin() {
            return isAdmin;
        }

        public void setIsAdmin(String isAdmin) {
            this.isAdmin = isAdmin;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
