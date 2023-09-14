package com.gocam.goscamdemopro.entity;

import android.text.TextUtils;

import com.gocam.goscamdemopro.GApplication;
import com.gos.platform.api.contact.DeviceType;
import com.gos.platform.api.domain.DeviceStatus;
import com.gos.platform.api.response.GetDeviceListResponse;
import com.gos.platform.device.base.Connection;
import com.gos.platform.device.contact.ConnType;
import com.gos.platform.device.contact.ConnectStatus;
import com.gos.platform.device.ulife.UlifeConnection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

public class Device {
    public String devName;
    public String devId;
    public boolean isOwn;
    public boolean isOnline;
    public int devType;
    private Connection connection;
    public String streamUser;
    public String streamPassword;
    private DevCap devCap;
    public int timezoneVerifyType = 0;   //0或者空：需要从设备能力集获取， 1：整点时区  2：半点时区   3：精确分钟时区（秒值）
    public int videoStreamBitRate = 0;
    public String deviceHdType;
    public String deviceHdwVer;
    public String deviceSfwVer;//设备固件版本
    public boolean hasDevNewUpdate;//是否有固件更新包

    public static final int STREAM_TUTK = 1;
    public static final int STREAM_P2P = 2;
    public static final int STREAM_TCP = 3;
    private int streamType = STREAM_TUTK;
    public static final int TALK_HALF_DUPLEX = 0;//半双工
    public static final int TALK_FULL_DUPLEX = 1;//全双工
    public static final int TALK_ALL_SUPPORT = 2;//都支持
    private int talkType = TALK_HALF_DUPLEX; //默认半双工，获取不到能力集时，也能保证通话功能正常
    public static final int AUDIO_ENC_AAC = 0;
    public static final int AUDIO_ENC_G711A = 1;
    public static final int AUDIO_ENC_UNKNOW = -1;
    public int audioEncType = AUDIO_ENC_UNKNOW;
    public int getEventListType = 0;//获取TF事件列表 0-Ulife, 1-P2P
    public boolean supportP2pPaging = false;//是否支持分页
    public boolean isSupportTF;//是否支持进度条样式的卡回放
    //设备参数信息
    public int deviceStatus;//0不在线，1在线，2休眠，休眠需要持续发唤醒指令
    public int charging;//是否充电中,1在充电  0未在充电
    public int battery = -1;//电量,-1未获取到电量
    public int lowpowerSwitch; //省电模式开关
    public int sdCardStatus;//-2卡异常，-1无卡， 0正常
    public boolean isSuport5G;//是否支持5G WIFI配网
    public boolean isSuport2p4G = true;//是否支持2.4G WIFI配网
    public boolean isSuportCloud;//是否支持云储存
    public int OSS_TYPE;
    public static final int OSS_aliyun = 1;//阿里云存储
    public static final int OSS_aws = 2;//亚马逊云存储
    public static final int OSS_liantong = 3;//联通云存储

    public int subScreen;//判断是否是1对多摄像头，4代表一个设备对应4个摄像头
    public Device(String devName, String devId, boolean isOnline, int devType){
        this.devName = devName;
        this.devId = devId;
        this.isOnline = isOnline;
        this.devType = devType;
    }

    /*streamUser streamPassword*/
    public Device(String devName, String devId, boolean isOnline, int devType, String streamUser, String streamPassword, GetDeviceListResponse.Cap cap,
                  String deviceHdType, String deviceSfwVer, String deviceHdwVer,boolean isOwn) {
        this.devName = devName;
        this.devId = devId;
        this.isOnline = isOnline;
        this.devType = devType;
        this.streamUser = streamUser;
        this.streamPassword = streamPassword;
        this.deviceHdType = deviceHdType;
        this.deviceSfwVer = deviceSfwVer;
        this.deviceHdwVer = deviceHdwVer;
        this.isOwn = isOwn;
        parseDap(cap);
    }


    public void setOnline(boolean online) {
        isOnline = online;
    }

    public synchronized Connection getConnection(){
        if(connection == null){
            connection = ConnectionManager.createConnectionIfNoExist(devId,streamPassword);
        }
        return connection;
    }

    public synchronized void release(){
        connection = null;
        ConnectionManager.deleteConnection(devId);
    }

    public static class ConnectionManager{
        private static List<Connection> connectionList = new ArrayList<>();
        public synchronized static Connection createConnectionIfNoExist(String deviceId,String psw){
            for(Connection conn : connectionList){
                if(TextUtils.equals(conn.getDeviceID(), deviceId)){
                    return conn;
                }
            }
            Connection connection = new UlifeConnection(deviceId, GApplication.app.user.getUserName(), psw,
                    ConnType.TYPE_P2P, true);
            connectionList.add(connection);
            return connection;
        }

        public synchronized static void deleteConnection(String deviceId){
            Iterator<Connection> iterator = connectionList.iterator();
            while(iterator.hasNext()){
                Connection next = iterator.next();
                if(TextUtils.equals(next.getDeviceID(), deviceId)){
                    iterator.remove();
                    next.release();
                }
            }
        }
    }

    public String getStreamPsw() {
        if(TextUtils.isEmpty(streamUser) && TextUtils.isEmpty(streamPassword)){
            return "user@123";
        }else{
            return streamUser + "@" + streamPassword;
        }
    }
    public DevCap getDevCap() {
        if (devCap == null) {
            devCap = new DevCap();
        }

        return devCap;
    }

    //时间校时，时区
    public int getVerifyTimezone(){
        DevCap devCap = getDevCap();
        if(timezoneVerifyType == 0 && devCap != null){
            timezoneVerifyType = devCap.timeZoneType;
        }

        int timeZone = 0;
        if(timezoneVerifyType == 2){
            timeZone = TimeZone.getDefault().getRawOffset() / 360000;//5:30->5.5时区， 改为55显示
            boolean isLightTime = TimeZone.getDefault().inDaylightTime(new Date());
            if (isLightTime) {
                timeZone+=10;
            }
        }else if(timezoneVerifyType == 3){
            //支持4:45时区
            Calendar calendar = Calendar.getInstance();
            // 夏令时时间，比标准时间快1小时，即3600000毫秒，
            // 根据系统时间计算，如果不在夏令时生效范围内，则为0毫秒，反之为3600000毫秒
            int dstOffset = calendar.get(Calendar.DST_OFFSET);
            // 取得与GMT之间的时间偏移量，例如罗马属于东1区，则时间偏移量为3600000毫秒
            int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
            timeZone = (zoneOffset + dstOffset) / 1000;
        }else{
            timeZone = TimeZone.getDefault().getRawOffset() / 3600000;
            boolean isLightTime = TimeZone.getDefault().inDaylightTime(new Date());
            if (isLightTime) {
                timeZone++;
            }
            if(devType != DeviceType.DOOR_BELL_GATWAY_1){
                if (timeZone < -12) {
                    timeZone = -12;
                }
                if (timeZone > 11) {
                    timeZone = 11;
                }
            }
        }
        return timeZone;
    }


    /**
     * 平台端设备在线状态
     */
    public boolean isPlatDevOnline() {
        return isOnline;
    }

    public void setPlatDevOnline(boolean isOnline) {
        this.isOnline = isOnline;
        if (connection != null) {
            //统一在此处使用这个方法
            if(deviceStatus == DeviceStatus.ONLINE){
                connection.setPlatDevOnline(true);
            }else{
                //deviceStatus == DeviceStatus.OFFLINE || deviceStatus == DeviceStatus.SLEEP
                //如果设备不现在，设置离线，
                connection.setPlatDevOnline(false);
                ////清楚连接
                connection.setConnectStatus(ConnectStatus.CONNECT_LOST);
            }
        }
    }


    private void parseDap(GetDeviceListResponse.Cap cap){
        if(cap != null){
            devCap = new DevCap();
            devCap.deviceID = devId;
            devCap.capType = DevCap.CAP_D;
            devCap.devType = cap.cap1;
            devCap.hasIc = cap.cap5 == 1;
            devCap.hasPir = cap.cap6 == 1;
            //0-无  1-支持云台控制  2-支持云台控制和预置位
            //3-支持云台控制/预置位和巡航
            //4-支持云台控制/预置位和隐私模式
            //5-支持云台控制/预设位和自检
            //32-全功能云台控制
            devCap.ptzType = cap.cap7;
            //devCap.ptzType = 4;
            devCap.hasPtz = cap.cap7 >= 1;
            if(cap.cap7 == 2 ||cap.cap7 == 3 ||cap.cap7 == 4 ||cap.cap7 == 5 ||cap.cap7 == 32){
                devCap.hasPreset = true;
            }

            devCap.hasMic = cap.cap8 == 1;
            devCap.hasSpeaker = cap.cap9 == 1;

            devCap.hasSdCard = cap.cap10 == 1;
            devCap.hasTemp = cap.cap11 == 1;
            devCap.isSupportTimeZone = cap.cap12 > 0;
            devCap.timeZoneType = cap.cap12;
            devCap.hasNightVison = cap.cap13 > 0;
            devCap.nightVisonType = cap.cap13;
            //devCap.nightVisonType = 2;//TODO TEST

            devCap.ethernetType = cap.cap14;
            devCap.smartType = cap.cap15;
            devCap.hasMotionDetection = cap.cap16 > 0;
            devCap.motionType = cap.cap16;
            // 是否有设置录像录像时长
            devCap.isSupportRecord = cap.cap17 == 1;
            devCap.hasLightFlag = cap.cap18 == 1;
            devCap.hasVoiceDetection = cap.cap19 == 1;

            devCap.isSupportLullaby = cap.cap20 > 0;
            devCap.lullabyType = cap.cap20;


            devCap.hasBattery = cap.cap21 == 1;      //是否有电池
            devCap.isSupportWakeOn = cap.cap22 == 1; //远程唤醒
            devCap.isSupportLedSw = cap.cap23 == 1; //状态灯开关
            devCap.isSupportCamSw = cap.cap24 > 0; //摄像头开关
            devCap.camSwitchType = cap.cap24; //0不支持开关，1，支持开关 2，支持开关和计划
            devCap.isSupportMicSw = cap.cap25 == 1; //麦克风开关
            devCap.isSupportCloud = cap.cap26 > 0; //是否支持云存储


            //0 - 不支持
            //1 - 支持阿里云
            //2 - 支持亚马逊云
            //3 - 支持联通云
            isSuportCloud = cap.cap26 > 0;
            if(isSuportCloud){
                OSS_TYPE = cap.cap26;
            }
            devCap.isSupportStreamPsw = cap.cap27 == 1;

            //TODO cap28

            devCap.isSupportBatteryLevel = cap.cap29 == 1;
            devCap.isSupportNetlinkSignal = cap.cap30 == 1;

            //cap.cap31 = 3;//TODO test
            devCap.isSupportAlexaSkills = cap.cap31 == 1 || cap.cap31 == 3;
            //devCap.isSupportAlexaVoice = cap.isSupportAlexaVoice;
            devCap.isSupportEhco = devCap.isSupportAlexaSkills;
            devCap.isSupportShow = devCap.isSupportAlexaSkills;
            devCap.isSupportGoogleHome = cap.cap31 == 2 || cap.cap31 == 3;

            devCap.isSupportDoorbellRing = cap.cap35 == 1;
            devCap.isSupportPirDistance = cap.cap36 == 1;
            devCap.isSupportCameraSetting = cap.cap37 == 1;

            devCap.isSupportIotSensor = cap.cap38 > 0;
            devCap.supportIotSensorTypes = cap.cap38;
            devCap.isSupportCryAlarm = cap.cap39 == 1;

            //devCap.isSupportSoundLight = cap.cap40 > 0;
            devCap.isSupportWarnSoundLight = cap.cap40 > 0;
            devCap.smartMotionDetection = cap.cap50;
            devCap.smartObjMotionDetection = cap.cap51;

            devCap.httpsSupport = cap.cap43;
            devCap.humanMotionDetection = cap.cap52;
            devCap.isSupportSpeakerVolume = cap.cap53;

            devCap.isSupportPushInterval = cap.cap54;

            devCap.p2pEncryption = cap.cap55;
            devCap.isSupDoorbellLed = cap.cap45 > 0;
            devCap.isSupDoorbellRemoveAlarm = cap.cap47 == 1;
            devCap.isSupDoorbellLowpower = cap.cap48 == 1;
            devCap.doorbellBandwidth = cap.cap42;
            devCap.devResetType = cap.cap41;

//            devCap.mainStream = cap.cap2;//主码率
//            if(devCap.mainStream > 0){
//                devCap.mainWidth = devCap.mainStream >> 16;
//                devCap.mainHeight = devCap.mainStream - (devCap.mainWidth << 16);
//            }
            //插值设备
            devCap.interpolationType = cap.cap2;


            devCap.recordAlarmAllType = cap.cap63;
            //1 180°翻转   2.支持水平，竖直，180°
            devCap.mirrorMode = cap.cap32;


            //服务器能力集
            //1. TUTK   2. 4.0_P2P   3. 4.0_TCP  4. P2P_TCP(都支持)
            streamType = cap.cap56;

            //支持的对讲类型
            talkType = cap.cap57;



            //音频格式 0：AAC 1：G711A
            audioEncType = cap.cap59;




            //获取TF事件列表 0-Ulife, 1-P2P;  2-支持P2P获取列表，分页；  3-支持P2P&分页&全天录像事件录像切换
            //getEventListType = val == 1 || val == 2 ? 1 : 0;
            //supportP2pPaging = val == 2;
            getEventListType = cap.cap61 == 1 || cap.cap61 == 2 || cap.cap61 == 3 ? 1 : 0;
            supportP2pPaging = cap.cap61 == 2 || cap.cap61 == 3;
            //TODO 全天录像与告警录像分开请求
            isSupportTF = cap.cap28 > 0;

            //0或者空：需要从设备能力集获取， 1：整点时区  2：半点时区   3：精确分钟时区（秒值）
            //timezoneVerifyType = Integer.parseInt(bytCap[i] + "");
            timezoneVerifyType = devCap.timeZoneType;

            //0--无     1--100万   2-200万（1920*1080） 3-300万（2304*1296）  4-400万
            videoStreamBitRate = cap.cap62;

            //3或4支持5G
            isSuport5G = cap.cap14 == 3 || cap.cap14 == 4 || cap.cap14 == 6;

            //0,2,4支持2.4G
            isSuport2p4G = cap.cap14 == 0 || cap.cap14 == 2 || cap.cap14 == 4 || cap.cap14 == 6;

            subScreen = cap.cap64;
            if(cap.cap64 > 0){
                //如果是4，代表有4个画面，大于0就属于套装设备
                //isPackageDevice = true;
            }

        }
    }


}
