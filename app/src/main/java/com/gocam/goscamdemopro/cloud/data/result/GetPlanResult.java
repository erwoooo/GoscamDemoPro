package com.gocam.goscamdemopro.cloud.data.result;

import com.gocam.goscamdemopro.cloud.data.entity.PackageInfo;
import com.google.gson.Gson;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetPlanResult extends PlatResult {

    List<PackageInfo> packageInfos;

    public GetPlanResult(int code, String json) {
        super(PlatCmd.getPlan, 0, code, json);
    }

    @Override
    protected void response(String json) {
        if (responseCode == 200) {
            try {
                packageInfos = new ArrayList<>();
                JSONObject bodyJsonObject = new JSONObject(json);
                String code = bodyJsonObject.optString("code");
                if ("0".equals(code)) {
                    responseCode= PlatCode.SUCCESS;
                    JSONArray jsonArray = bodyJsonObject.optJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        Gson gson = new Gson();
                        PackageInfo packageInfo = gson.fromJson(jsonObject.toString(), PackageInfo.class);
                        packageInfos.add(packageInfo);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<PackageInfo> getData() {
        return packageInfos;
    }

}
