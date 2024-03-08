package com.gocam.goscamdemopro.vphoto.data;

import static com.gos.platform.api.result.PlatResult.PlatCmd.userRegisterByUuid;

import android.util.Log;

import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRegisterResult extends PlatResult {
    private final String TAG = this.getClass().getSimpleName();

    private UserRegisterBean userRegisterBean;

    public UserRegisterResult(int code, String json) {
        super(userRegisterByUuid, 0, code, json);
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
                userRegisterBean = gson.fromJson(dataArray.toString(), UserRegisterBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public UserRegisterBean getUserRegisterBean() {
        return userRegisterBean;
    }

    public static class UserRegisterBean {
        private int user_id; // 用户id
        private int user_account;
        private String user_nickname; // 用户名称
        private String user_uuid; // 注册时传的uuid
        private String message; // 错误信息

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getUser_account() {
            return user_account;
        }

        public void setUser_account(int user_account) {
            this.user_account = user_account;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public String getUser_uuid() {
            return user_uuid;
        }

        public void setUser_uuid(String user_uuid) {
            this.user_uuid = user_uuid;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
