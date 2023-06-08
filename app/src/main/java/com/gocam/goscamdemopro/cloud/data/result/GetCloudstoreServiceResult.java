package com.gocam.goscamdemopro.cloud.data.result;

import com.gocam.goscamdemopro.cloud.data.entity.CloudSetMenuInfo;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GetCloudstoreServiceResult extends PlatResult {
    protected List<CloudSetMenuInfo> cloudSetMenuInfos;

    public GetCloudstoreServiceResult(int code, String json) {
        super(PlatCmd.getCloudstoreService, 0, code, json);
    }

    @Override
    protected void response(String json) {
        if (responseCode == 200) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                int code = -1;
                if (jsonObject.has("code")) {
                    code = jsonObject.getInt("code");
                }
                if (code == 0) {
                    responseCode = PlatCode.SUCCESS;
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    cloudSetMenuInfos=new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        CloudSetMenuInfo data = gson.fromJson(jsonObject1.toString(), CloudSetMenuInfo.class);

//                        long startTime = DateUtils.stringToTime(DateUtils.Pattern_yyyyMMddHHmmss, data.getStartTime());
//                        long invalidTime = DateUtils.stringToTime(DateUtils.Pattern_yyyyMMddHHmmss, data.getPreinvalidTime());
//                        long expiredTime = DateUtils.stringToTime(DateUtils.Pattern_yyyyMMddHHmmss, data.getDataExpiredTime());
//                        data.setServiceStartTime(startTime / 1000);
//                        data.setServiceInvalidTime(invalidTime / 1000);
//                        data.setServiceDataExpiredTime(expiredTime / 1000);
                        if(data!=null) {
                            cloudSetMenuInfos.add(data);
                        }

                    }

                } else {
                    responseCode = code;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<CloudSetMenuInfo> getData() {
        return this.cloudSetMenuInfos;
    }
}
