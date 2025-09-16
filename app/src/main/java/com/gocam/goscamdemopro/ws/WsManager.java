package com.gocam.goscamdemopro.ws;

import static com.gos.platform.api.BaseParser.NETSDK_EVENT_CONN_SUCCESS;
import static com.gos.platform.api.BaseParser.NETSDK_EVENT_GOS_RECV;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.gocam.goscamdemopro.GApplication;
import com.gocam.goscamdemopro.utils.dbg;
import com.gos.platform.api.ConfigManager;
import com.gos.platform.api.PlatformApiParser;
import com.gos.platform.api.UlifeResultParser;
import com.gos.platform.api.contact.PushType;
import com.gos.platform.api.request.AppHeartRequest;
import com.gos.platform.api.request.Request;
import com.gos.platform.api.result.AppHeartResult;
import com.gos.platform.api.result.DeviceParamReportNotifyResult;
import com.gos.platform.api.result.PlatResult;
import com.gos.platform.api.result.PlatResult.PlatCmd;
import com.gos.platform.api.result.PushMsgResult;
import com.gos.platform.device.domain.DevAlarmInfo;
import com.gos.platform.device.receiver.PushMessageBroadReceiver;
import com.gos.platform.device.result.DevResult;
import com.gos.platform.device.result.DevResult.DevCmd;
import com.gos.platform.device.ulife.request.FormatDevSdCardRequest;
import com.gos.platform.device.ulife.request.GetAllAlarmListRequest;
import com.gos.platform.device.ulife.request.GetAllRecordListRequest;
import com.gos.platform.device.ulife.request.UlifeDevRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class WsManager extends WebSocketListener {
    private final static String TAG = "WsManager";
    private static WsManager mInstance;
    private WsStatus mStatus;
    private WebSocket mWebSocket;
    private OkHttpClient mOkHttpClient;
    private int reconnectCount = 0;//Number of reconnections
    private final long minInterval = 3000;//Minimum reconnection interval
    private final long maxInterval = 60000;//Maximum reconnection interval

    //Domestic official
    private final static String WS_URL_CN_TEST = "ws://119.23.124.137:8000";
    private final static String WS_URL_CN = "wss://cnapp-open.ulifecam.com/websocketMessage";
    //Foreign official
    private final static String WS_URL_EN = "wss://usapp-open.ulifecam.com/websocketMessage";



    private String mWsUrl;

    private NetStatusReceiver mNetStatusReceiver;
    public static final String DEFAULT_TOKEN = "DEFAULT_SESSION";//This command is used to send heartbeat packets when the user is not logged in
    private String mAccessToken = DEFAULT_TOKEN;
    private String mUserName;
    private boolean isInit;

    private ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private static final int CODE_TIMEOUT = -101;
    private static final int PLAT_HANDLER = 101;
    private static final int DEV_HANDLER = 102;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            dbg.D(TAG, "handleMessage="+msg.obj);
            int what = msg.what;
            //heartbeat
            if(PLAT_HANDLER == what && ((PlatResult)msg.obj).getPlatCmd() == PlatCmd.appHeart){
                AppHeartResult result = (AppHeartResult) msg.obj;
                int code = result.getResponseCode();
                if(code == CODE_TIMEOUT){
                    heartbeatFailCount++;
                    if (heartbeatFailCount >= 3) {
                        reconnect();
                    }
                }else {
                    heartbeatFailCount = 0;
                }
                //Avoid repetition
                mHandler.removeCallbacks(heartbeatTask);
                mHandler.postDelayed(heartbeatTask, HEARTBEAT_INTERVAL);
            }

            for(WeakReference<ICallback> w : eventCallbacks){
                ICallback callback = w.get();
                if(callback != null){
                    if(PLAT_HANDLER == what){
                        callback.OnEvent((PlatResult) msg.obj,null);
                    }else if(DEV_HANDLER == what){
                        callback.OnEvent(null, (DevResult) msg.obj);
                    }
                }
            }
        }
    };

    public static WsManager getInstance(){
        if(mInstance == null){
            synchronized (WsManager.class){
                if(mInstance == null){
                    mInstance = new WsManager();
                }
            }
        }
        return mInstance;
    }

    private WsManager(){

    }

    public void init(){
        dbg.D(TAG, "init,isInit="+isInit);
        if(!isInit){
            isInit = true;

            switch(ConfigManager.serverType){
                case ConfigManager.EN_SERVER:
                    mWsUrl = WS_URL_EN;
                    break;
                case ConfigManager.CN_SERVER:
                    mWsUrl = WS_URL_CN;
                    break;
                case ConfigManager.TEST_SERVER_NET_CN:
                    mWsUrl = WS_URL_CN_TEST;
                    break;
                default:
                    throw new RuntimeException("ws url is no matched,serverType="+ ConfigManager.serverType);
            }

            mOkHttpClient = new OkHttpClient.Builder()
                    .readTimeout(3, TimeUnit.SECONDS)//Set the read timeout period
                    .writeTimeout(3, TimeUnit.SECONDS)//Set the write timeout period
                    .connectTimeout(3, TimeUnit.SECONDS)//Set the connection timeout period
                    .build();
            okhttp3.Request request = new okhttp3.Request.Builder().url(mWsUrl).build();
            setStatus(WsStatus.CONNECTING);
            mWebSocket = mOkHttpClient.newWebSocket(request, this);
            dbg.D(TAG, "init, mWsUrl = " + mWsUrl);
            //Registered network monitoring
            mNetStatusReceiver = new NetStatusReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            GApplication.app.registerReceiver(mNetStatusReceiver, intentFilter);
        }
    }

    public void unInit(){
        dbg.D(TAG, "unInit,isInit="+isInit);
        if(isInit){
            isInit = false;
            cancelHeartbeat();
            cancelReconnect();
            mWebSocket.cancel();
            GApplication.app.unregisterReceiver(mNetStatusReceiver);
        }
    }

    private void cancelReconnect() {
        dbg.D(TAG, "cancelReconnect");
        reconnectCount = 0;
        mHandler.removeCallbacks(mReconnectTask);
    }

    public void reconnect(){
        dbg.D(TAG, "reconnect,isInit="+isInit+",mStatus="+mStatus+",mWebSocket="+mWebSocket);
        if (!isNetConnect()) {
            reconnectCount = 0;
            return;
        }

        //Not reconnecting
        if (mWebSocket != null && isInit &&
                getStatus() != WsStatus.CONNECT_SUCCESS &&
                getStatus() != WsStatus.CONNECTING) {
            reconnectCount++;
            setStatus(WsStatus.CONNECTING);
            //Cancel heartbeat
            cancelHeartbeat();

            long reconnectTime = minInterval;
            if (reconnectCount > 3) {
                long temp = minInterval * (reconnectCount - 2);
                reconnectTime = temp > maxInterval ? maxInterval : temp;
            }
            dbg.D(TAG, "reconnect, reconnectCount="+reconnectCount+",reconnectTime="+reconnectTime);
            mHandler.postDelayed(mReconnectTask, reconnectTime);
        }
    }

    private Runnable mReconnectTask = new Runnable() {

        @Override
        public void run() {
            try {
                dbg.D(TAG, "mReconnectTask, run,mWsUrl="+mWsUrl);
                okhttp3.Request request = new okhttp3.Request.Builder().url(mWsUrl).build();
                mWebSocket = mOkHttpClient.newWebSocket(request, WsManager.this);
            } catch (Exception e) {
                e.printStackTrace();
                dbg.D(TAG, "mReconnectTask, run,ex="+e.getLocalizedMessage());
            }
        }
    };

    public void updateToken(String userName, String accessToken){
        this.mUserName = userName;
        this.mAccessToken = accessToken;
        cancelHeartbeat();
        startHeartbeat(0);
    }

    private void startHeartbeat(long delay){
        dbg.D(TAG, "startHeartbeat");
        mHandler.postDelayed(heartbeatTask, delay);
    }

    private void cancelHeartbeat(){
        dbg.D(TAG, "cancelHeartbeat");
        heartbeatFailCount = 0;
        mHandler.removeCallbacks(heartbeatTask);
    }

    private static final long HEARTBEAT_INTERVAL = 30000;//Heartbeat interval
    private static final int REQUEST_TIMEOUT = 10000;//Request timeout
    private int heartbeatFailCount = 0;
    private Runnable heartbeatTask = new Runnable() {
        @Override
        public void run() {
            String userName = mUserName;
            String accessToken = mAccessToken;
            AppHeartRequest request = new AppHeartRequest(userName, accessToken);
            dbg.D(TAG, "heartbeatTask run, data=" + request.toJSON());
            sendPlat(PlatCmd.appHeart, request, REQUEST_TIMEOUT, 1);
        }
    };

    private void setStatus(WsStatus status){
        dbg.D(TAG, "setStatus, status=" + status);
        mStatus = status;
    }


    private WsStatus getStatus() {
        dbg.D(TAG, "getStatus, status=" + mStatus);
        return mStatus;
    }


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        dbg.D(TAG, "onOpen, code="+response.code());
        setStatus(WsStatus.CONNECT_SUCCESS);
        cancelReconnect();
        PlatResult platResult = PlatformApiParser.parser(null, NETSDK_EVENT_CONN_SUCCESS, 0, null, 0, null);
        mHandler.obtainMessage(PLAT_HANDLER, platResult).sendToTarget();
        startHeartbeat(HEARTBEAT_INTERVAL);
    }


    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        dbg.D(TAG, "onClosed, code="+code+",reason="+reason);
        setStatus(WsStatus.CONNECT_FAIL);
        reconnect();
    }


    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        dbg.D(TAG, "onClosing, code="+code+",reason="+reason);
        setStatus(WsStatus.CONNECT_FAIL);
    }


    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        dbg.D(TAG, "onFailure, code="+(response!=null?response.code():20000)+",ex="+t.getLocalizedMessage());
        setStatus(WsStatus.CONNECT_FAIL);
        reconnect();
    }


    @Override
    public void onMessage(WebSocket webSocket, String text) {
        dbg.D(TAG, "onMessage, text="+text);
        String MessageType = null;
        JSONObject root = null;
        try {
            root = new JSONObject(text);
            MessageType = root.getString("MessageType");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        if(!TextUtils.isEmpty(MessageType)){
            if(TextUtils.equals(MessageType, Request.MsgType.PushMsgRequest)){
                //Push a long-connected device
                PushMsgResult res = new PushMsgResult(NETSDK_EVENT_GOS_RECV, 0, text);
                DevAlarmInfo alarmInfo = res.getAlarmInfo();
                alarmInfo.pushType = PushType.MPS;
                //Notifications are coming in


            } else if(TextUtils.equals(MessageType, Request.MsgType.NotifyDeviceStatus)){
                //Device online status field Link device notification
                PlatResult platResult = PlatformApiParser.parser(null, NETSDK_EVENT_GOS_RECV, 0, text, 0, null);
                mHandler.obtainMessage(PLAT_HANDLER, platResult).sendToTarget();

            } else if(TextUtils.equals(MessageType, Request.MsgType.NotifyDeviceParam)){
                //Device online status field Link device notification
                PlatResult platResult = PlatformApiParser.parser(null, NETSDK_EVENT_GOS_RECV, 0, text, 0, null);
                mHandler.obtainMessage(PLAT_HANDLER, platResult).sendToTarget();

            } else if(TextUtils.equals(MessageType, Request.MsgType.DeviceParamReportNotify)){
                DeviceParamReportNotifyResult notifyResult = new DeviceParamReportNotifyResult(0, 0, text);
                mHandler.obtainMessage(PLAT_HANDLER, notifyResult).sendToTarget();

            }else if(TextUtils.equals(MessageType, Request.MsgType.BypassParamResponse)){
                //Device platform data
                DevResult devResult = UlifeResultParser.parser(NETSDK_EVENT_GOS_RECV, 0, text, 0, null);
                if(devResult != null){
                    String DeviceId = null;
                    try {
                        JSONObject body = root.getJSONObject("Body");
                        DeviceId = body.getString("DeviceId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    devResult.setDeviceId(DeviceId);
                    DevCmdI cmdI = DevCmdI.create(devResult.getDevCmd(), DeviceId);
                    CallbackWrapper callbackWrapper = devCallbacks.remove(cmdI);
                    if(callbackWrapper != null){
                        callbackWrapper.getTimeoutTask().cancel(true);
                        callbackWrapper.getTempCallback().onSuccess(devResult);
                    }else{
                        if(devResult.getDevCmd() == DevCmd.getAllAlarmList
                                || devResult.getDevCmd() == DevCmd.getAllRecordList){
                            mHandler.obtainMessage(DEV_HANDLER, devResult).sendToTarget();
                        }
                    }
                }

            } else {
                //Platform data
                PlatResult platResult = PlatformApiParser.parser(null, NETSDK_EVENT_GOS_RECV, 0, text, 0, null);
                if(platResult != null){
                    CallbackWrapper callbackWrapper = platCallbacks.remove(platResult.getPlatCmd());
                    if(callbackWrapper != null){
                        callbackWrapper.getTimeoutTask().cancel(true);
                        callbackWrapper.getTempCallback().onSuccess(platResult);
                    }
                }
            }
        }
    }


    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        dbg.D(TAG, "onMessage, bytes="+bytes);
    }

    public enum WsStatus {
        CONNECT_SUCCESS,//连接成功
        CONNECT_FAIL,//连接失败
        CONNECTING;//正在连接
    }

    private boolean isNetConnect() {
        ConnectivityManager connectivity = (ConnectivityManager) GApplication.app.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    protected ConcurrentLinkedQueue<WeakReference<ICallback>> eventCallbacks = new ConcurrentLinkedQueue<>();
    public void addICallback(ICallback listener) {
        for(WeakReference<ICallback> w : eventCallbacks){
            ICallback callback = w.get();
            if(callback == listener){
                return;
            }
        }
        if(listener != null)
            eventCallbacks.add(new WeakReference<>(listener));
    }
    public void removeICallback(ICallback listener) {
        for(WeakReference<ICallback> w : eventCallbacks){
            ICallback callback = w.get();
            if(callback == listener || callback == null){
                eventCallbacks.remove(w);
            }
        }
    }

    public interface ICallback{
        void OnEvent(PlatResult platResult, DevResult devResult);
    }


    private Map<PlatCmd, CallbackWrapper<PlatCmd>> platCallbacks = new HashMap<>();
    //平台命令发送
    public int sendPlat(final PlatCmd cmd, Request request, final long timeOut, int reqCount){
        dbg.D(TAG, "sendPlat, cmd="+cmd+",request="+request.toJSON()+",timeOut="+timeOut+",reqCount="+reqCount);
        if(!isNetConnect()){
            PlatResult platResult = PlatformApiParser.parser(null, NETSDK_EVENT_GOS_RECV, CODE_TIMEOUT, request.toJSON(), 0, null);
            mHandler.obtainMessage(PLAT_HANDLER, platResult).sendToTarget();
            return -1;
        }

        //Only one timeout is set for the same request
        CallbackWrapper callbackWrapper = platCallbacks.get(cmd);
        dbg.D(TAG, "sendPlat, callbackWrapper=" + callbackWrapper);
        if(callbackWrapper != null){
            //Cancel timeout task
            callbackWrapper.timeoutTask.cancel(true);
            platCallbacks.remove(cmd);
        }

        //Set the timeout callback
        ScheduledFuture scheduledFuture = mExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                CallbackWrapper wrapper = platCallbacks.remove(cmd);
                dbg.D(TAG, "scheduledFuture run, wrapper=" + wrapper);
                if (wrapper != null) {
                    dbg.D(TAG, "scheduledFuture run, data=" + wrapper.getRequest().toJSON());
                    wrapper.getTempCallback().onTimeout(wrapper.getRequest(), wrapper.getCmd());
                }
            }
        }, timeOut, TimeUnit.MILLISECONDS);

        IWsCallback temp = new IWsCallback<PlatResult, PlatCmd>() {
            @Override
            public void onSuccess(PlatResult data) {
                mHandler.obtainMessage(PLAT_HANDLER, data).sendToTarget();
            }

            @Override
            public void onTimeout(Request request, PlatCmd cmd) {
                dbg.D(TAG, "IWsCallback onTimeout, request=" + request.toJSON() + ",requestCount="+request.requestCount);
                if (request.requestCount > 3) {
                    //-101请求超时
                    PlatResult platResult = PlatformApiParser.parser(null, NETSDK_EVENT_GOS_RECV, CODE_TIMEOUT, request.toJSON(), 0, null);
                    onSuccess(platResult);
                } else {
                    sendPlat(cmd, request, timeOut, request.requestCount + 1);
                }
            }
        };

        dbg.D(TAG, "sendPlat send, cmd="+cmd);
        request.requestCount = reqCount;
        platCallbacks.put(cmd, new CallbackWrapper<>(cmd, temp, scheduledFuture, request));
        mWebSocket.send(request.toJSON());
        return 0;
    }

    public interface IWsCallback<T, A>{
        void onSuccess(T data);
        void onTimeout(Request request, A cmd);
    }

    public class CallbackWrapper<T> {

        private T cmd;
        private final IWsCallback tempCallback;
        private final ScheduledFuture timeoutTask;
        private final Request request;

        public CallbackWrapper(T cmd, IWsCallback tempCallback, ScheduledFuture timeoutTask, Request request) {
            this.cmd = cmd;
            this.tempCallback = tempCallback;
            this.timeoutTask = timeoutTask;
            this.request = request;
        }

        public IWsCallback getTempCallback() {
            return tempCallback;
        }

        public ScheduledFuture getTimeoutTask() {
            return timeoutTask;
        }

        public Request getRequest() {
            return request;
        }

        public T getCmd(){
            return cmd;
        }
    }

    private static class DevCmdI{
        public DevCmd cmd;
        public String deviceID;

        public static DevCmdI create(DevCmd cmd, String deviceID){
            DevCmdI cmdI = new DevCmdI();
            cmdI.cmd = cmd;
            cmdI.deviceID = deviceID;
            return cmdI;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DevCmdI devCmdI = (DevCmdI) o;
            return cmd == devCmdI.cmd &&
                    deviceID.equals(devCmdI.deviceID);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cmd, deviceID);
        }
    }

    private Map<DevCmdI, CallbackWrapper> devCallbacks = new HashMap<>();



    public class NetStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) GApplication.app
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    dbg.D(TAG, "NetStatusReceiver onReceive, NET CHANGE");
                    WsManager.getInstance().reconnect();//wify 4g切换重连websocket
                }
            }
        }
    }

}
