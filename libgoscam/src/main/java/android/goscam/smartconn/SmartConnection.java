package android.goscam.smartconn;

import android.content.Context;
import android.goscam.net.LanScan;
import android.goscam.net.LanScanCallback;
import android.goscam.utils.WifiUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.broadcom.cooee.Cooee;
import com.realtek.simpleconfiglib.SCLibrary;

import java.lang.ref.WeakReference;

import ulife.goscam.com.loglib.dbg;

/**
 * {@link SmartConnection#init()}<br/>
 * {@link SmartConnection#config(SmartConfig)}<br/>
 * {@link SmartConnection#stop()}<br/>
 * {@link SmartConnection#release()}<br/>
 *
 * @author Stephen, 253407292@qq.com
 * @version V1.0.001
 * @date 2015年5月14日 下午3:55:36
 * @since JDK1.7
 */
public enum SmartConnection implements LanScanCallback {
    _zlink {
        @Override
        protected void loadModule() {
        }

        @Override
        protected void internalStart(Context context, byte authMode) {

        }

        @Override
        protected void internalStop() {

        }

        @Override
        protected void unloadModule() {

        }
    },
    _7601 {

        private ElianNative elian = null;
        private boolean mModuleInited = false;
        private boolean mConnStarted = false;

        @Override
        protected void loadModule() {
            if (mModuleInited) {
                return;
            }
            if (elian == null) {
                elian = new ElianNative();
                elian.initBoth();
            }
            mModuleInited = true;
        }

        @Override
        public String getVersion() {
            int libVer = -1, protoVer = -1;
            if (elian != null) {
                libVer = elian.GetLibVersion();
                protoVer = elian.GetProtoVersion();
            }
            dbg.D("ADD","_7601 getVersion;"+"V" + libVer + "." + protoVer);
            return "V" + libVer + "." + protoVer;
        }

        @Override
        protected void internalStart(Context context, byte authMode) {
            if (!mModuleInited || mConnStarted) {
                // if(mConnStarted) {
                // if (mLocalService != null) {
                // mLocalService.release();
                // }
                // mLocalService = new LocalService(mSmartConfig.matchUid,
                // mSmartConfig.timeoutSec, mSmartConfig.callback);
                // mLocalService.restart();
                // }
                return;
            }
            if (elian != null && mSmartConfig != null) {

                dbg.D("ADD","_7601 internalStart; info="+mSmartConfig.getInfo()+",authMode="+authMode);
                elian.StartSmartConnection("", "", mSmartConfig.getInfo(), authMode);
            }
            mConnStarted = true;
        }

        @Override
        protected void internalStop() {
            if (!mModuleInited || !mConnStarted) {
                return;
            }
            if (elian != null) {
                elian.StopSmartConnection();
            }
            mConnStarted = false;
        }

        @Override
        protected void unloadModule() {
            // TODO Auto-generated method stub
            if (!mModuleInited) {
                return;
            }
            if (mConnStarted) {
                internalStop();
            }
            mModuleInited = false;
            elian = null;
        }
    },
    _8188 {
        private SCLibrary SCLib = new SCLibrary();

        @Override
        protected void loadModule() {

        }

        @Override
        protected void internalStart(Context context, byte authMode) {
            /** Simple Config 初始化 */
            SCLib.rtk_sc_init();

            /** 网络操作初始化 */
            SCLib.WifiInit(context);

            _8188ConnectParams params = new _8188ConnectParams();
            params.setSsid(mSmartConfig.ssid);
            params.setPasswd(mSmartConfig.pwd);
            params.setBssid(mSmartConfig.bssid);
            params.setPhoneMac(mSmartConfig.phoneMac);
            dbg.D("ADD","_8188 internalStart;ssid="+mSmartConfig.ssid
                    +",pwd="+mSmartConfig.pwd
                    +",bssid="+mSmartConfig.bssid
                    +",mac="+mSmartConfig.phoneMac);
            // SCLib.rtk_sc_start("", ""); // Generate profile and start simple
            // // configure

            SCLib.rtk_sc_start(params);
        }

        @Override
        protected void internalStop() {
            SCLib.rtk_sc_stop();
        }

        @Override
        protected void unloadModule() {

        }
    },
    _ap6212a {
        private boolean hasConnected;

        @Override
        protected void loadModule() {

        }

        @Override
        protected void internalStart(Context context, byte authMode) {
            Cooee.SetPacketInterval(20);
            while (!hasConnected) {
                if (mSmartConfig != null) {
                    dbg.D("ADD","_ap6212a internalStart;ssid=" +mSmartConfig.ssid
                            +",psd="+mSmartConfig.pwd+",ip="+mSmartConfig.localIp);
                    Cooee.send(mSmartConfig.ssid, mSmartConfig.pwd, mSmartConfig.localIp);
                } else {
                    break;
                }
                Thread.currentThread().getName();
                dbg.D("ADD","smart 发送中");
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        while (!hasConnected) {

//                        }
//                    }
//                }).start();

            }
        }

        @Override
        protected void internalStop() {
            hasConnected = true;
        }

        @Override
        protected void unloadModule() {

        }
    };

    private static final int MSG_LOAD_MODULE = 0x100;
    private static final int MSG_UNLOAD_MODULE = MSG_LOAD_MODULE + 1;
    private static final int MSG_START = MSG_UNLOAD_MODULE + 1;
    private static final int MSG_STOP = MSG_START + 1;
    private static final int MSG_CLEAR = MSG_STOP + 1;
    private static final int MSG_STOP_LANSCAN = MSG_CLEAR + 1;
    private static final int DEFAULT_DELAY = 1000;

    private HandlerThread mThread;
    private SmartHandler mHandler;

    protected SmartConfig mSmartConfig;

//	protected LocalService mLocalService;

    private static class SmartHandler extends Handler {
        private WeakReference<SmartConnection> mWeakConn;

        public SmartHandler(SmartConnection c, Looper l) {
            super(l);
            mWeakConn = new WeakReference<SmartConnection>(c);
        }

        @Override
        public void handleMessage(Message msg) {
            SmartConnection conn = null;
            if (mWeakConn == null || (conn = mWeakConn.get()) == null) {
                return;
            }
            switch (msg.what) {
                case MSG_LOAD_MODULE:
                    conn.loadModule();
                    break;
                case MSG_UNLOAD_MODULE:
                    conn.unloadModule();
                    sendEmptyMessageDelayed(MSG_CLEAR, DEFAULT_DELAY);
                    break;
                case MSG_CLEAR:
                    mWeakConn.clear();
                    mWeakConn = null;
                    conn.clear();
                    break;
                case MSG_START:
                    conn.internalStart((Context) msg.obj, (byte) msg.arg1);
                    conn.internalStartLocalService((Context) msg.obj, (byte) msg.arg1);
                    break;
                case MSG_STOP:
                    conn.internalStopLocalService();
                    conn.internalStop();
                    break;
                case MSG_STOP_LANSCAN:
//                    LanScan.getInstance().release();
                    SmartConfig cfg = conn.mSmartConfig;
                    if (cfg == null || cfg.callback == null) {
                        break;
                    }
                    if (!conn.mHasMatched) {
                        cfg.callback.onSmartConnectionTimeout(cfg.matchUid);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    SmartConnection() {
    }

    public void config(SmartConfig config) {
        dbg.D("ADD","CONFIG:" + config);
        mSmartConfig = config;
    }

    public SmartConfig getConfig() {
        return mSmartConfig;
    }

    public void init() {
        if (mThread == null) {
            mThread = new HandlerThread("smart-connection");
            mThread.start();
        }
        if (mHandler == null) {
            mHandler = new SmartHandler(this, mThread.getLooper());
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessage(MSG_LOAD_MODULE);
    }

    protected boolean isLocalServiceSupported() {
        return true;
    }

    @Override
    public void onLanScanCallback(String uid, String ip, long costMs, int ybind) {
        boolean matched = false;
        dbg.D("ADD","onLanScanCallback :: " +uid+ "::"+ip+"::"+costMs+"::ybind="+ybind);
        if (mSmartConfig == null) {
            return;
        }
        dbg.D("ADD","onLanScanCallback;uid="+ uid+";ip="+ip+";costMs="+costMs+";mSmartConfig.matchUid="+mSmartConfig.matchUid);
        if (mSmartConfig.callback == null) {
            return;
        }
        if (mSmartConfig.matchUid != null || uid != null) {
            matched = mSmartConfig.matchUid.equals(uid) || mSmartConfig.matchUid.contains(uid)||uid.contains(mSmartConfig.matchUid);
        }
        if (matched) {
            mHasMatched = true;
        }
        mSmartConfig.callback.onSmartConnectionMatched(uid, ip, matched, System.currentTimeMillis() - mTimeMsStarted, ybind);
    }

    private long mTimeMsStarted = 0;
    private boolean mHasMatched = false;

    private void internalStartLocalService(Context context, byte authMode) {
        dbg.D("ADD","internalStartLocalService ::" + mSmartConfig);
        if (mSmartConfig == null) {
            return;
        }
        mHasMatched = false;
        mTimeMsStarted = System.currentTimeMillis();
        LanScan.getInstance().addCallback(this);
        LanScan.getInstance().start();
        mHandler.removeMessages(MSG_STOP_LANSCAN);
        mHandler.sendEmptyMessageDelayed(MSG_STOP_LANSCAN, mSmartConfig.timeoutSec * 1000);
//		if(!isLocalServiceSupported()) {
//			return;
//		}
//		if (mLocalService != null) {
//			mLocalService.release();
//		}
//		mLocalService = new LocalService(context, mSmartConfig.matchUid, mSmartConfig.timeoutSec, mSmartConfig.callback);
//		mLocalService.restart();
    }

    private void internalStopLocalService() {
        dbg.D("ADD","internalStopLocalService");
        mHandler.removeMessages(MSG_STOP_LANSCAN);//TODO 移除超时监听
        LanScan.getInstance().removeCallback(this);
        LanScan.getInstance().release();
//		if(!isLocalServiceSupported()){
//			return;
//		}
//		if (mLocalService != null) {
//			mLocalService.release();
//		}
    }

    protected abstract void loadModule();

    public String getVersion() {
        return "V0.0.0";
    }

    protected abstract void internalStart(Context context, byte authMode);

    protected abstract void internalStop();


    protected abstract void unloadModule();

    public interface SmartConnectionCallback {
        void onSmartConnectionMatched(String p2pUid, String ip, boolean matched, long costInMillsec, int ybind);

        void onSmartConnectionTimeout(String p2pUid);
    }

    public void start(Context ctx, String matchUid, int timeoutSec, SmartConnectionCallback cb) {
        dbg.D("ADD","start");
        if (mThread == null || mHandler == null) {
            init();
        }
        byte authMode = -1;
        if (mSmartConfig != null) {
            authMode = WifiUtils.getAuthMode(ctx, mSmartConfig.ssid);
            mSmartConfig.autoMode = authMode;
            mSmartConfig.matchUid = matchUid;
            if (timeoutSec <= 10) {
                timeoutSec = 15;
            }
            mSmartConfig.timeoutSec = timeoutSec;
            mSmartConfig.callback = cb;
        }
        mHandler.removeMessages(MSG_STOP);
        mHandler.removeMessages(MSG_START);
        Message msg = Message.obtain();
        msg.what = MSG_START;
        msg.arg1 = authMode;
        msg.obj = ctx;
        mHandler.sendMessageDelayed(msg, DEFAULT_DELAY);
    }

    public void stop() {
        dbg.D("ADD","stop");
        if (mThread == null || mHandler == null) {
            return;
        }
        mHandler.removeMessages(MSG_STOP);
        mHandler.removeMessages(MSG_START);
        mHandler.sendEmptyMessageDelayed(MSG_STOP, DEFAULT_DELAY);
    }

    public void release() {
        dbg.D("ADD","release");
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_UNLOAD_MODULE);
        }
        if (mSmartConfig != null) {
            mSmartConfig.matchUid = null;
            mSmartConfig.callback = null;
        }
        mSmartConfig = null;
//        mHandler = null;  //release -> clear
    }

    protected void clear() {
        dbg.D("ADD","clear::"+mHandler);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    /*
     * 是否支持smart扫描
     * 0代表不支持
     * 1代表7601smart
     * 2代表8188smart
     * 3代表6212a smart
     * 9代表不支持二维码扫描
     * 10代表只支持扫描二维码连接wifi
     * 11代表支持扫描二维码连接wifi和7601smart
     * 12代表支持扫描二维码连接wifi和8188smart
     * 13代表支持扫描二维码连接wifi和6212asmart
     * 14AP模式(新增添加方式)
     * 15代表AP添加+8188smart
     * 16
     */
    /**
     * @param module 0: LAN scanning only; 1: 7601; 2:8188;3_ap6212a
     * @return
     */
    public static SmartConnection select(int module) {
        dbg.D("ADD","SmartConnection : module="+module);
        switch (module) {
            case 2:
            case 12:
            case 15:
                return _8188;
            case 3:
            case 13:
                return _ap6212a;
            case 4:
                return _zlink;
        }
        return _7601;

    }
    // public static void showDialog(Context context, String title, String msg,
    // int iconRes) {
    // AlertDialog.Builder builder = new AlertDialog.Builder(context);
    // builder.setIcon(iconRes);
    // builder.setTitle(title);
    // builder.setMessage(msg);
    // builder.setPositiveButton("exit",
    // new DialogInterface.OnClickListener() {
    // public void onClick(DialogInterface dialog, int whichButton) {
    // //
    // }
    // });
    //
    // builder.show();
    // }
    //
    // public static boolean checkLib(Context c, boolean needShowDlgOnFial,
    // int iconRes) {
    // boolean result = true;// ElianNative.LoadLib();
    // if (!result && needShowDlgOnFial) {
    // showDialog(c, "Error", "can't load elianjni lib", iconRes);
    // }
    // return result;
    // }
}
