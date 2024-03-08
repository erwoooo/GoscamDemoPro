package com.gocam.goscamdemopro.vphoto.data;

import static com.gos.platform.api.result.PlatResult.PlatCmd.signinByUuid;

import android.util.Log;

import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;

import org.json.JSONException;
import org.json.JSONObject;

public class SigninByUuidResult extends PlatResult {
    private final String TAG = this.getClass().getSimpleName();

    private SigninBean signinBean;

    public SigninByUuidResult(int code, String json) {
        super(signinByUuid, 0, code, json);
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
                signinBean = gson.fromJson(dataArray.toString(), SigninBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public SigninBean getSigninBean() {
        return signinBean;
    }

    public static class SigninBean {
        private int user_id; // 用户id
        private int user_account;
        private String user_name; // 注册时传的uuid
        private String user_nickname; // 用户名称
        private String profileImageUrl;
        private String user_system_token; // 获取已绑定设备时的token
        private String userVideocallType;
        private String access_token; // 接口的token
        private String userVideoCallUsername;
        private String userVideoCallPassword;
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

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public String getUser_system_token() {
            return user_system_token;
        }

        public void setUser_system_token(String user_system_token) {
            this.user_system_token = user_system_token;
        }

        public String getUserVideocallType() {
            return userVideocallType;
        }

        public void setUserVideocallType(String userVideocallType) {
            this.userVideocallType = userVideocallType;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getUserVideoCallUsername() {
            return userVideoCallUsername;
        }

        public void setUserVideoCallUsername(String userVideoCallUsername) {
            this.userVideoCallUsername = userVideoCallUsername;
        }

        public String getUserVideoCallPassword() {
            return userVideoCallPassword;
        }

        public void setUserVideoCallPassword(String userVideoCallPassword) {
            this.userVideoCallPassword = userVideoCallPassword;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
