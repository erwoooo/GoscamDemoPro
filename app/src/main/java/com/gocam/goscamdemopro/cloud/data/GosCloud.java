package com.gocam.goscamdemopro.cloud.data;

import android.util.Log;

import com.gocam.goscamdemopro.cloud.data.result.CreateOrderResult;
import com.gocam.goscamdemopro.cloud.data.result.CreateWeixinOrderResult;
import com.gocam.goscamdemopro.cloud.data.result.FreeOrderCreateResult;
import com.gocam.goscamdemopro.cloud.data.result.FreePlanResult;
import com.gocam.goscamdemopro.cloud.data.result.GetAliOrderInfoResult;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudAlarmListResult;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudAlarmVideoListResult;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudCurrentServiceResult;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudOssInfoResult;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudPlayFileListResult;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudSetMenuInfoResult;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudVideoListResult;
import com.gocam.goscamdemopro.cloud.data.result.GetCloudstoreServiceResult;
import com.gocam.goscamdemopro.cloud.data.result.GetFaceAllCloudAlarmVideoListResult;
import com.gocam.goscamdemopro.cloud.data.result.GetLastCloudAlarmResult;
import com.gocam.goscamdemopro.cloud.data.result.GetLastCloudVideoResult;
import com.gocam.goscamdemopro.cloud.data.result.GetPaypalTokenResult;
import com.gocam.goscamdemopro.cloud.data.result.GetPlanResult;
import com.gocam.goscamdemopro.cloud.data.result.PaymentFreeResult;
import com.gocam.goscamdemopro.cloud.data.result.PaypalServerPayResult;
import com.gocam.goscamdemopro.cloud.data.result.QueryOrderResult;
import com.gocam.goscamdemopro.cloud.data.result.VerifyAliOrderResult;
import com.gos.platform.api.ConfigManager;
import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.contact.ServerType;
import com.gos.platform.api.inter.OnPlatformEventCallback;
import com.gos.platform.api.result.PlatResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class GosCloud {
    private static String URL_HEAD;
    private static String GET_ClOUD_SET_MENU_INFO_URL;
    private static String GET_OSS_INFO_URL;
    private static String GET_CLOUD_VIDEO_LIST_URL;
    private static String GET_FACE_CLOUD_VIDEO_LIST_URL;
    private static String GET_CLOUD_ALARM_VIDEO_LIST_URL_BY_TIME;
    private static String GET_PLAY_FILE_LIST_BY_PAGE;//分页获取云存储数据

    private static String GET_ALL_CLOUD_ALARM_VIDEO_LIST_URL;
    private static String GET_CLOUD_VIDEO_PIC_LIST_URL;
    private static String PLAY_URL;
    private static String GET_ALARM_LIST_URL;
    private static String GET_LAST_VIDEO;
    private static String GET_PLAY_FILE_LIST;
    private static String GET_PLAY_FILE_LIST_AFTER_TIME;
    private static String GET_PLAN;
    private static String CREATE_ORDER;
    private static String GET_ALI_ORDERINFO;
    private static String VERIFY_ALI_ORDER;
    private static String QUERY_ORDER;
    private static String CREATE_WEIXIN_ORDER;
    private static String GET_PAYPAL_TOKEN;
    private static String PAYPAL_SERVER_PAY;
    private static String GET_CLOUDSTORE_SERVICE;
    private static String GET_CLOUDSTORE_CURRENT_SERVICE;
    private static String GET_ALL_DEVICES_SET_MENU_INFO;
    private static String TRANSFER_CLOUD_SERVICE;
    private static String FREE_PLAN;
    private static String FREE_ORDER_CREATE;
    private static String PAYMENT_FREE;

    private static String CREATE_STORE_WEIXIN_ORDER;
    private static String GET_STORE_ALIORDER;
    private static String VERIFY_STORE_ALIORDER;

    private ConcurrentLinkedQueue<OnPlatformEventCallback> eventCallbacks = new ConcurrentLinkedQueue();
    private static volatile GosCloud mGosCloud;
    public static final String HeaderKey = "User-Agent";
    public static final String HeaderValue = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36";
    public static final String ServerVersion = "1.0";
    private static int mServerType = -1;

    private GosCloud() {
    }

    private static void checkServerType(int serverType) {
        if (serverType != mServerType) {
            mServerType = serverType;
            Log.e("mServerType", "checkServerType: mServerType " + mServerType );
            String ipAddress;
            switch (mServerType) {
                case ConfigManager.EN_SERVER:
                    ipAddress = "css.ulifecam.com:9998";
                    break;
                case ConfigManager.CN_SERVER:
                    ipAddress = "cn-css.ulifecam.com";
                    break;
                case ConfigManager.TEST_SERVER_NET_EN:
                    ipAddress = "css.security.mysmitch.com";//:9998
                    break;
                case ConfigManager.AUTO_SERVER:
                    if (ConfigManager.isChinese(Locale.getDefault())) {
                        ipAddress = "cn-css.ulifecam.com";
                    } else {
                        ipAddress = "css.ulifecam.com:9998";
                    }
                    break;
                default:
                    ipAddress = ConfigManager.getInstance().getServerInfo(ServerType.CMS).serverAddress + ":9998";
                    break;
            }
            //for test
//        URL_HEAD = "http://119.23.124.137:9998/api";

            Log.e("ipAddress", "checkServerType: ipAddress "+ ipAddress );
            URL_HEAD = "https://" + ipAddress + "/api";


            GET_ClOUD_SET_MENU_INFO_URL = URL_HEAD + "/cloudstore/cloudstore-service/service/data-valid";
            GET_OSS_INFO_URL = URL_HEAD + "/cloudstore/cloudstore-service/sts/check-token";
            GET_CLOUD_VIDEO_LIST_URL = URL_HEAD + "/cloudstore/cloudstore-service/file/time-line";
            GET_FACE_CLOUD_VIDEO_LIST_URL = URL_HEAD + "/cloudstore/cloudstore-service/face/move-video/time-line/details";
            GET_CLOUD_ALARM_VIDEO_LIST_URL_BY_TIME = URL_HEAD + "/cloudstore/cloudstore-service/move-video/time-line";
            GET_PLAY_FILE_LIST_BY_PAGE = URL_HEAD + "/cloudstore/cloudstore-service/move-video/time-line/details/pagination";

            GET_ALL_CLOUD_ALARM_VIDEO_LIST_URL = URL_HEAD + "/cloudstore/cloudstore-service/move-video/all";
            GET_CLOUD_VIDEO_PIC_LIST_URL = URL_HEAD + "/cloudstore/cloudstore-service/move-pic/time-line";
            PLAY_URL = URL_HEAD + "/cloudstore/cloudstore-service/video/play";
            GET_ALARM_LIST_URL = URL_HEAD + "/cloudstore/cloudstore-service/alarm/time-line";
            GET_LAST_VIDEO = URL_HEAD + "/cloudstore/cloudstore-service/video/time-line-refresh";
//        GET_PLAY_FILE_LIST = URL_HEAD + "/cloudstore/cloudstore-service/file/time-line/details";
            GET_PLAY_FILE_LIST = URL_HEAD + "/cloudstore/cloudstore-service/move-video/time-line/details";
//            GET_FACE_PLAY_FILE_LIST = URL_HEAD + "/face/move-video/time-line/details";
            GET_PLAY_FILE_LIST_AFTER_TIME = URL_HEAD + "/cloudstore/cloudstore-service/move-video/time-line/details/index";

            GET_PLAN = URL_HEAD + "/cloudstore/cloudstore-service/plan";
            CREATE_ORDER = URL_HEAD + "/pay/pay-service/inland/cloudstore/order/create";
            GET_ALI_ORDERINFO = URL_HEAD + "/pay/pay-service/alipay/order/sign";
            VERIFY_ALI_ORDER = URL_HEAD + "/pay/pay-service/alipay/payment/check";
            QUERY_ORDER = URL_HEAD + "/pay/pay-service/inland/cloudstore/payment/query";
            CREATE_WEIXIN_ORDER = URL_HEAD + "/pay/pay-service/wechat/order/prepare";
            GET_PAYPAL_TOKEN = URL_HEAD + "/pay/pay-service/paypal/check/client_token";
            PAYPAL_SERVER_PAY = URL_HEAD + "/pay/pay-service/paypal/check/payment";
            GET_CLOUDSTORE_SERVICE = URL_HEAD + "/cloudstore/cloudstore-service/service/list";

            GET_CLOUDSTORE_CURRENT_SERVICE = URL_HEAD + "/cloudstore/cloudstore-service/service/current";
            GET_ALL_DEVICES_SET_MENU_INFO = URL_HEAD + "/cloudstore/cloudstore-service/manage/service-list";
            TRANSFER_CLOUD_SERVICE = URL_HEAD + "/cloudstore/cloudstore-service/manage/device/migrate";
            FREE_PLAN = URL_HEAD + "/cloudstore/cloudstore-service/free-plan";
            FREE_ORDER_CREATE = URL_HEAD + "/pay/pay-service/inland/cloudstore/free-order/create";
            PAYMENT_FREE = URL_HEAD + "/pay/pay-service/inland/cloudstore/payment-free";

            CREATE_STORE_WEIXIN_ORDER = "http://119.23.124.137:9201/dashboard-store/pay/wechat/beforehand";
//            CREATE_STORE_WEIXIN_ORDER = "http://192.168.1.104:9201/dashboard-store/pay/wechat/beforehand";//TODO Test
            GET_STORE_ALIORDER = "http://119.23.124.137:9201/dashboard-store/pay/alipay/order/sign";
            VERIFY_STORE_ALIORDER = "http://119.23.124.137:9201/dashboard-store/pay/alipay/payment/check";
        }
    }

    public static GosCloud getCloud() {
        if (mGosCloud == null) {
            synchronized (GosCloud.class) {
                if (mGosCloud == null)
                    mGosCloud = new GosCloud();
            }
        }
        checkServerType(ConfigManager.serverType);
        return mGosCloud;
    }

    public void addOnPlatformEventCallback(OnPlatformEventCallback listener) {
        if (!this.eventCallbacks.contains(listener)) {
            this.eventCallbacks.add(listener);
        }
    }

    public void removeOnPlatformEventCallback(OnPlatformEventCallback listener) {
        this.eventCallbacks.remove(listener);
    }

    /**
     * 获取用户云套餐信息
     *
     * @param devId 设备ID
     * @param handleCode  请求标记
     * @return
     */
    public boolean getCloudSetMenuInfo(final String devId, String token, String username, final int handleCode) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("token", token);
        params.put("check", "novel");
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>get(GET_ClOUD_SET_MENU_INFO_URL).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
//                String message = response.message();
                post(new GetCloudSetMenuInfoResult(200, body, devId, handleCode));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetCloudSetMenuInfoResult(-1, null, devId, handleCode));
            }
        });
        return true;
    }



    public GetCloudSetMenuInfoResult getCloudSetMenuInfo(final String devId, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("token", token);
        params.put("check", "novel");
        params.put("username", username);
        params.put("version", ServerVersion);
        try {
            okhttp3.Response response = OkGo.<String>get(GET_ClOUD_SET_MENU_INFO_URL).tag(this).params(params).headers(HeaderKey, HeaderValue).execute();
            String body = response.body().string();
            return new GetCloudSetMenuInfoResult(200, body, devId, 0);
        } catch (IOException e) {
            e.printStackTrace();
            return new GetCloudSetMenuInfoResult(-1, null, devId, 0);
        }
    }

    /**
     * 查询设备当前的云存储服务状态详情
     *
     * @return
     */
    public boolean getCloudCurrentService(String devId, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("token", token);
        params.put("check", "novel");
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>get(GET_CLOUDSTORE_CURRENT_SERVICE).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
//                String message = response.message();
                post(new GetCloudCurrentServiceResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetCloudCurrentServiceResult(-1, null));
            }
        });
        return true;
    }

    /**
     * 获取OssInfo(Token等)信息
     *params.put("check", "novel");
     * @param devId 设备ID
     * @return
     */
    public boolean getCloudOssInfo(final String devId, String token, String username, final int handleCode) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("token", token);
        params.put("check", "novel");
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>post(GET_OSS_INFO_URL).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {
            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
            }

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetCloudOssInfoResult(200, body, devId, handleCode));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetCloudOssInfoResult(-1, null, devId, handleCode));
            }
        });
        return true;
    }
    public GetCloudOssInfoResult getCloudOssInfo(String devId, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        try {
            okhttp3.Response response = OkGo.<String>post(GET_OSS_INFO_URL).tag(this).params(params).headers(HeaderKey, HeaderValue).execute();
            String body = response.body().string();
            return new GetCloudOssInfoResult(200, body, devId, 0);
        } catch (IOException e) {
            e.printStackTrace();
            return new GetCloudOssInfoResult(-1, null, devId, 0);
        }
    }

    /**
     * 获取设备推流记录
     *
     * @param devId     设备ID
     * @param startTime 起始时间 类型：unix时间戳 1520121254
     * @param endTime   结束时间 类型：unix时间戳 1520121254
     * @return
     */
    public boolean getCloudVideoList(String devId, long startTime, long endTime, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>get(GET_CLOUD_VIDEO_LIST_URL).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                String message = response.message();
                post(new GetCloudVideoListResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetCloudVideoListResult(-1, null));
            }
        });
        return true;
    }


    /**
     * 获取设备告警事件记录
     *
     * @param devId     设备ID号
     * @param startTime 起始时间 类型：unix时间戳 1520121254
     * @param endTime   结束时间 类型：unix时间戳 1520121254
     * @return
     */
    public boolean getCloudAlarmList(String devId, long startTime, long endTime, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>get(GET_ALARM_LIST_URL).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetCloudAlarmListResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetCloudAlarmListResult(-1, null));
            }
        });
        return true;
    }

    /**
     * 获取设备告警视频记录
     *
     * @param devId     设备ID
     * @param startTime 起始时间 类型：unix时间戳 1520121254
     * @param endTime   结束时间 类型：unix时间戳 1520121254
     * @return
     */
    public boolean getCloudAlarmVideoListByTime(String devId, long startTime, long endTime, String token, String username,final int handleCode) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);

        OkGo.<String>get(GET_CLOUD_ALARM_VIDEO_LIST_URL_BY_TIME).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                String message = response.message();
                post(new GetCloudAlarmVideoListResult(PlatResult.PlatCmd.getCloudAlarmVideoListByTime, 200, body, handleCode));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetCloudAlarmVideoListResult(PlatResult.PlatCmd.getCloudAlarmVideoListByTime, -1, null, handleCode));
            }
        });
        return true;
    }

    public GetCloudAlarmVideoListResult getCloudAlarmVideoListByTime(String devId, long startTime, long endTime, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);

        try {
            okhttp3.Response response = OkGo.<String>get(GET_CLOUD_ALARM_VIDEO_LIST_URL_BY_TIME).tag(this).params(params).headers(HeaderKey, HeaderValue).execute();
            String body = response.body().string();
            return new GetCloudAlarmVideoListResult(PlatResult.PlatCmd.getCloudAlarmVideoListByTime, 200, body, 0);
        } catch (IOException e) {
            e.printStackTrace();
            return new GetCloudAlarmVideoListResult(PlatResult.PlatCmd.getCloudAlarmVideoListByTime, -1, null, 0);
        }
    }

    /**
     * 获取视频文件列表,带分页
     *
     * @param devId
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @param token
     * @param username
     * @param pageNum   页码 从1开始
     * @param size      每页的数量
     * @param sort      sort=asc降序,sort=desc升序
     *                  size=xxx&page_number=xxx
     * @return
     */
    public boolean getPlayFileListByPage(String devId, long startTime, long endTime, String token
            , String username, int size, int pageNum, String sort) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("token", token);
        params.put("check", "novel");
        params.put("username", username);
        params.put("version", ServerVersion);
        params.put("size", size);
        params.put("page_number", pageNum);
        params.put("sort", sort);
        OkGo.<String>get(GET_PLAY_FILE_LIST_BY_PAGE).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetCloudPlayFileListResult(200, body, 0,startTime, endTime, 0));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetCloudPlayFileListResult(-1, null, 0,startTime, endTime, 0));
            }
        });
        return true;
    }

    /**
     * 获取设备所有告警视频记录
     *
     * @param devId 设备ID
     * @return
     */
    public boolean getAllCloudAlarmVideoList(String devId, long startTime, long endTime, String token, String username,final int handleCode) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("token", token);
        params.put("check", "novel");
        params.put("username", username);
        params.put("version", ServerVersion);
//        OkGo.<String>get(GET_ALL_CLOUD_ALARM_VIDEO_LIST_URL).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {
        OkGo.<String>get(GET_CLOUD_ALARM_VIDEO_LIST_URL_BY_TIME).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetCloudAlarmVideoListResult(PlatResult.PlatCmd.getAllCloudAlarmVideoList, 200, body, handleCode));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetCloudAlarmVideoListResult(PlatResult.PlatCmd.getAllCloudAlarmVideoList, -1, null, handleCode));
            }
        });
        return true;
    }
    //同步方法获取
    public GetCloudAlarmVideoListResult getAllCloudAlarmVideoList(String devId, long startTime, long endTime, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
//        OkGo.<String>get(GET_ALL_CLOUD_ALARM_VIDEO_LIST_URL).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {
        try {
            okhttp3.Response response = OkGo.<String>get(GET_CLOUD_ALARM_VIDEO_LIST_URL_BY_TIME)
                    .tag(this)
                    .params(params)
                    .headers(HeaderKey, HeaderValue)
                    .execute();
            String body = response.body().string();
            return new GetCloudAlarmVideoListResult(PlatResult.PlatCmd.getAllCloudAlarmVideoList, 200, body, 0);
        } catch (IOException e) {
            e.printStackTrace();
            return new GetCloudAlarmVideoListResult(PlatResult.PlatCmd.getAllCloudAlarmVideoList, -1, null, 0);
        }
    }

    public GetFaceAllCloudAlarmVideoListResult getFaceAllCloudAlarmVideoList(String faceId, String shoulders, String devId, long startTime, long endTime, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("faces", faceId);
        params.put("shoulders", shoulders);
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        try {
            okhttp3.Response response = OkGo.<String>get(GET_FACE_CLOUD_VIDEO_LIST_URL)
                    .tag(this)
                    .params(params)
                    .headers(HeaderKey, HeaderValue)
                    .execute();
            String body = response.body().string();
            return new GetFaceAllCloudAlarmVideoListResult(PlatResult.PlatCmd.getFaceAllCloudAlarmVideoList, 200, body, 0);
        } catch (IOException e) {
            e.printStackTrace();
            return new GetFaceAllCloudAlarmVideoListResult(PlatResult.PlatCmd.getFaceAllCloudAlarmVideoList, -1, null, 0);
        }
    }

    /**
     * 获取设备人脸所有告警视频记录
     *
     * @param devId 设备ID
     * @return
     */
    public boolean getFaceAllCloudAlarmVideoList(String faceId,String shoulders,String devId, long startTime, long endTime, String token, String username,final int handleCode) {
        HttpParams params = new HttpParams();
        params.put("faces", faceId);
        params.put("shoulders", shoulders);
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
//        params.put("start_time", "1564588800");
//        params.put("end_time", "1564675199");
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
//        OkGo.<String>get(GET_ALL_CLOUD_ALARM_VIDEO_LIST_URL).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {
        OkGo.<String>get(GET_FACE_CLOUD_VIDEO_LIST_URL).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetFaceAllCloudAlarmVideoListResult(PlatResult.PlatCmd.getFaceAllCloudAlarmVideoList, 200, body, handleCode));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetFaceAllCloudAlarmVideoListResult(PlatResult.PlatCmd.getFaceAllCloudAlarmVideoList, -1, null, handleCode));
            }
        });
        return true;
    }

    /**
     * 获取设备最新推流记录
     *
     * @param devId     设备ID
     * @param startTime 起始时间 类型：unix时间戳 1520121254
     * @param endTime   结束时间 类型：unix时间戳 1520121254
     * @return
     */
    public boolean getLastCloudVideo(String devId, long startTime, long endTime, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>get(GET_CLOUD_VIDEO_LIST_URL).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                String message = response.message();
                post(new GetLastCloudVideoResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetLastCloudVideoResult(-1, null));
            }
        });
        return true;
    }

    /**
     * 获取设备最新告警事件记录
     *
     * @param devId     设备ID号
     * @param startTime 起始时间 类型：unix时间戳 1520121254
     * @param endTime   结束时间 类型：unix时间戳 1520121254
     * @return
     */
    public boolean getLastCloudAlarm(String devId, long startTime, long endTime, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>get(GET_ALARM_LIST_URL).tag(this).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetLastCloudAlarmResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetLastCloudAlarmResult(-1, null));
            }
        });
        return true;
    }

    public GetCloudPlayFileListResult getPlayFileList(String devId, final long startTime, final long endTime, String token, String username) throws IOException {
        OkGo.getInstance().cancelTag(String.valueOf(0));
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("token", token);
        params.put("check", "novel");
        params.put("username", username);
        params.put("version", ServerVersion);
        okhttp3.Response response = OkGo.<String>get(GET_PLAY_FILE_LIST).tag(String.valueOf(0)).params(params).headers(HeaderKey, HeaderValue).execute();
        String body = response.body().string();
        return new GetCloudPlayFileListResult(200, body, 0, startTime, endTime, 0);
    }

    /**
     * 获取视频文件列表
     *
     * @param devId     设备ID号
     * @param startTime 起始时间 类型：unix时间戳 1520121254
     * @param endTime   结束时间 类型：unix时间戳 1520121254
     * @return
     */
    public boolean getPlayFileList(String devId, final int actionCode, final long startTime, final long endTime, String token, String username, final int handleCode) {
        OkGo.getInstance().cancelTag(String.valueOf(actionCode));
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>get(GET_PLAY_FILE_LIST).tag(String.valueOf(actionCode)).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetCloudPlayFileListResult(200, body, actionCode, startTime, endTime, handleCode));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetCloudPlayFileListResult(-1, null, actionCode, startTime, endTime, handleCode));
            }
        });
        return true;
    }

    /**
     * 获取startTime之后的limit个视频文件列表
     *
     * @param devId     设备ID号
     * @param startTime 起始时间 类型：unix时间戳 1520121254
     * @param limit     返回文件个数
     * @return
     */
    public boolean getPlayFileListAfterTime(String devId, final int actionCode, final long startTime, final int limit, String token, String username,final int handleCode) {
        OkGo.getInstance().cancelTag(String.valueOf(actionCode));
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("start_time", startTime);
        params.put("limit", limit);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>get(GET_PLAY_FILE_LIST_AFTER_TIME).tag(String.valueOf(actionCode)).params(params).headers(HeaderKey, HeaderValue).execute(new StringCallback() {

            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetCloudPlayFileListResult(200, body, actionCode, startTime, -1, handleCode));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetCloudPlayFileListResult(-1, null, actionCode, startTime, -1, handleCode));
            }
        });
        return true;
    }

    /**
     * 查询云存储套餐列表
     */
    public boolean getPlan(String token, String username) {
        HttpParams params = new HttpParams();
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>post(GET_PLAN).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetPlanResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                int code = response.code();
                String body = response.body();
                post(new GetPlanResult(-1, null));
            }

        });
        return true;
    }

    /**
     * 查询当前可以享受的免费套餐
     */
    public boolean freePlan(String devId, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("token", token);
        params.put("username", username);
        params.put("device_id", devId);
        OkGo.<String>post(FREE_PLAN).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new FreePlanResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new FreePlanResult(-1, null));
            }

        });
        return true;
    }

    /**
     * 创建免费云存储套餐订单
     */
    public boolean freeOrderCreate(String planId, String devId, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("token", token);
        params.put("username", username);
        params.put("device_id", devId);
        params.put("plan_id", planId);
        OkGo.<String>post(FREE_ORDER_CREATE).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new FreeOrderCreateResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new FreeOrderCreateResult(-1, null));
            }

        });
        return true;
    }

    /**
     * 支付免费云存储套餐
     */
    public boolean paymentFree(String orderNo, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("order_no", orderNo);
        params.put("token", token);
        params.put("username", username);
        OkGo.<String>post(PAYMENT_FREE).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new PaymentFreeResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new PaymentFreeResult(-1, null));
            }

        });
        return true;
    }

    /**
     * 创建支付宝订单
     *
     * @param deviceId   设备id
     * @param planId     套餐id
     * @param count      套餐订购数量
     * @param totalPrice 订单总价格
     * @param token      登录验证
     */
    public boolean createOrder(String deviceId, String planId, String count, String totalPrice, String token, String username) {
//        String url = urlHead + "pay-service/inland/cloudstore/order/create";
        HttpParams params = new HttpParams();
        params.put("device_id", deviceId);
        params.put("plan_id", planId);
        params.put("count", count);
        params.put("total_price", totalPrice);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>post(CREATE_ORDER).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new CreateOrderResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new CreateOrderResult(-1, null));
            }

        });
        return true;
    }

    /**
     * 获取加签订单信息
     *
     * @param orderNo 订单号
     * @param token   用户token
     */
    public boolean getAliOrderInfo(String orderNo, String token, String username) {
//        String url = urlHead+"pay-service/alipay/order/sign";
        HttpParams params = new HttpParams();
        params.put("order_no", orderNo);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>post(GET_ALI_ORDERINFO).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetAliOrderInfoResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetAliOrderInfoResult(-1, null));
            }

        });
        return true;
    }

    /**
     * 获取商城支付宝加签信息
     * @param orderNo   订单号
     * @param token     用户token
     * @return
     */
    public boolean getStoreAliOrderInfo(String orderNo,String token){
        HttpParams params = new HttpParams();
        params.put("order_id",orderNo);
        params.put("token",token);
        OkGo.<String>post(GET_STORE_ALIORDER).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetAliOrderInfoResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetAliOrderInfoResult(-1, null));
            }
        });
        return true;
    }

    /**
     * 发送到服务器验证支付结果
     *
     * @param contentParams 支付宝返回的支付结果
     */
    public boolean verifyAliOrder(String contentParams, String memo, String resultStatus, String username, String token) {
        HttpParams params = new HttpParams();
        params.put("content-params", contentParams);
        params.put("memo", memo);
        params.put("resultStatus", resultStatus);
        params.put("username", username);
        params.put("token", token);
        params.put("version", ServerVersion);
        OkGo.<String>post(VERIFY_ALI_ORDER).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new VerifyAliOrderResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new VerifyAliOrderResult(-1, null));
            }

        });
        return true;
    }

    /**商城
     * 发送到服务器验证支付结果
     *
     * @param contentParams 支付宝返回的支付结果
     */
    public boolean verifyStoreAliOrder(String contentParams, String memo, String resultStatus, String token){
        HttpParams params = new HttpParams();
        params.put("content-params", contentParams);
        params.put("memo", memo);
        params.put("resultStatus", resultStatus);
        params.put("token", token);
        OkGo.<String>post(VERIFY_STORE_ALIORDER).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new VerifyAliOrderResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new VerifyAliOrderResult(-1, null));
            }

        });
        return true;
    }

    /**
     * 查询支付结果
     *
     * @param orderNo 订单号
     * @param token   用户token
     */
    public boolean queryOrder(String orderNo, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("order_no", orderNo);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>post(QUERY_ORDER).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new QueryOrderResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new QueryOrderResult(-1, null));
            }

        });
        return true;
    }


    /**
     * 微信支付预订单
     *
     * @param orderNo 订单号
     * @param version 当前版本：中性版：goscam ；门铃版： gosbell
     * @param token   用户token
     */
    public boolean createWeixinOrder(String orderNo, String token, String username, String version) {
        HttpParams params = new HttpParams();
        params.put("order_no", orderNo);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>post(CREATE_WEIXIN_ORDER + version).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new CreateWeixinOrderResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new CreateWeixinOrderResult(-1, null));
            }

        });
        return true;
    }

    /**
     * 商城微信支付预订单
     * @param orderNo   内部订单号
     * @param token     用户token
     * @param version   当前版本：中性版：goscam ；门铃版： gosbell
     * @return
     */
    public boolean createStoreWeixinOrder(String orderNo,String token,String version){
        HttpParams params = new HttpParams();
        params.put("order_id", orderNo);
        params.put("token", token);
        OkGo.<String>post(CREATE_STORE_WEIXIN_ORDER + version).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new CreateWeixinOrderResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new CreateWeixinOrderResult(-1, null));
            }

        });
        return true;
    }

    /**
     * 获取paypal客户端token
     *
     * @param orderNo 订单号
     * @param token   用户token
     */
    public boolean getPaypalToken(String orderNo, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("order_no", orderNo);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>post(GET_PAYPAL_TOKEN).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetPaypalTokenResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetPaypalTokenResult(-1, null));
            }

        });
        return true;
    }


    /**
     * 调用paypal后端支付
     *
     * @param nonce   支付码
     * @param amount  支付价格
     * @param orderNo 订单号
     * @param token   用户token
     */
    public boolean paypalServerPay(String nonce, String amount, String orderNo, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("payment_method_nonce", nonce);
        params.put("amount", amount);
        params.put("order_no", orderNo);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkHttpClient.Builder builder = OkGo.getInstance().getOkHttpClient().newBuilder();
        //因为paypal支付返回很慢，需要设置超时时间为10分钟
        builder.readTimeout(10 * 60 * 60, TimeUnit.MILLISECONDS);
        builder.writeTimeout(10 * 60 * 60, TimeUnit.MILLISECONDS);
        builder.connectTimeout(10 * 60 * 60, TimeUnit.MILLISECONDS);
        OkGo.<String>post(PAYPAL_SERVER_PAY).tag(this).client(builder.build()).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new PaypalServerPayResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new PaypalServerPayResult(-1, null));
            }

        });
        return true;
    }

    /**
     * 获取当前已开通套餐的列表
     *
     * @param devId 设备ID
     */
    public boolean getCloudstoreService(String devId, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("device_id", devId);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>get(GET_CLOUDSTORE_SERVICE).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new GetCloudstoreServiceResult(200, body));
            }

            @Override
            public void onError(Response<String> response) {
                post(new GetCloudstoreServiceResult(-1, null));
            }

        });
        return true;
    }

    /**
     * 将已解绑设备的套餐转移到新设备
     *
     * @param oriDevId    设备ID
     * @param destDevId 新设备ID
     */
    public boolean transferCloudService(String oriDevId, String destDevId, String token, String username) {
        HttpParams params = new HttpParams();
        params.put("ori_device", oriDevId);
        params.put("dist_device", destDevId);
        params.put("token", token);
        params.put("username", username);
        params.put("version", ServerVersion);
        OkGo.<String>post(TRANSFER_CLOUD_SERVICE).tag(this).params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String body = response.body();
                post(new PlatResult(PlatResult.PlatCmd.transferCloudService, 0, 200, body) {
                    @Override
                    protected void response(String json) {
                        if (responseCode == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                if (jsonObject.has("code")) {
                                    responseCode = jsonObject.getInt("code");
                                    if (responseCode == 0)
                                        responseCode = PlatCode.SUCCESS;
                                } else
                                    responseCode = -1;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Response<String> response) {
                post(new PlatResult(PlatResult.PlatCmd.transferCloudService, 0, -1, null));
            }

        });
        return true;
    }

    private void post(PlatResult result) {
        for (OnPlatformEventCallback callback : eventCallbacks) {
            callback.OnPlatformEvent(result);
        }
    }

}
