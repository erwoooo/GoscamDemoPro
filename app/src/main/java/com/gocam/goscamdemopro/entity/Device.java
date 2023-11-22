package com.gocam.goscamdemopro.entity;

import static com.gos.platform.device.contact.ConnType.TYPE_TUTK;

import android.text.TextUtils;

import com.gocam.goscamdemopro.utils.dbg;
import com.gos.platform.api.contact.DeviceType;
import com.gos.platform.api.domain.DevVideoQuality;
import com.gos.platform.api.domain.DeviceStatus;
import com.gos.platform.api.domain.QueryVersionInfo;
import com.gos.platform.api.response.GetDeviceListResponse;
import com.gos.platform.device.base.Connection;
import com.gos.platform.device.contact.ConnType;
import com.gos.platform.device.contact.ConnectStatus;
import com.gos.platform.device.contact.OnOff;
import com.gos.platform.device.ulife.UlifeConnection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class Device implements Comparable<Device> {
    private int frequencyOfUse;//用户使用频率

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_DOOR_BELL = 2;
    public static final int TYPE_P2P = 3;
    public int productType = TYPE_NORMAL;//产品类型

    public static final int STREAM_TUTK = 1;
    public static final int STREAM_P2P = 2;
    public static final int STREAM_TCP = 3;
    private int streamType = STREAM_TUTK;

    public static final int TALK_HALF_DUPLEX = 0;//半双工
    public static final int TALK_FULL_DUPLEX = 1;//全双工
    public static final int TALK_ALL_SUPPORT = 2;//都支持
    private int talkType = TALK_HALF_DUPLEX; //默认半双工，获取不到能力集时，也能保证通话功能正常
//    private int talkType = TALK_FULL_DUPLEX;

    public int OSS_TYPE;
    public static final int OSS_aliyun = 1;//阿里云存储
    public static final int OSS_aws = 2;//亚马逊云存储
    public static final int OSS_liantong = 3;//联通云存储



    public static final int AUDIO_ENC_AAC = 0;
    public static final int AUDIO_ENC_G711A = 1;
    public static final int AUDIO_ENC_UNKNOW = -1;
    public int audioEncType = AUDIO_ENC_UNKNOW;

    public boolean isSuportCloud;//是否支持云储存
    public boolean isSupportTF;//是否支持进度条样式的卡回放
    public boolean isSupportOldTF;//是否支持文件夹式的卡回放

    public int aiSupportType = 0; //0-不接入, 1-接入服务器版本,  2-接入设备端版本   是否支持智能识别，不等于0就是支持
    public int getEventListType = 0;//获取TF事件列表 0-Ulife, 1-P2P
    public boolean supportP2pPaging = false;//是否支持分页
    public int timezoneVerifyType = 0;
    public int videoStreamBitRate = 0;

    public String preTag; //A9996100 YSWK84GX4LPJ1CUP111A
    public String devId;
    public String devName;
    public boolean isOnline; // 是否在线
    public int createChn;  //是否创建过通道了
    public boolean isOwn;

    public String MAC;
    public String devPsw;
    public String p2pId;
    protected boolean isPushMsgOpen = true;
    public int devType;

    public String deviceHdType;
    public String deviceHdwVer;
    public String deviceSfwVer;//设备固件版本
    public boolean hasDevNewUpdate;//是否有固件更新包
    public QueryVersionInfo versionInfo; //设备跟新信息

    //0-GOSCOM  1-GoscamPro
    public int matchType;
    public String streamUser;
    public String streamPassword;

    private Connection conn;
    private HashMap<Integer,Connection> connectionMap= new HashMap<>();
    private HashMap<Integer,Connection> tcpConnectionMap= new HashMap<>();
    private UlifeConnection tcpConn;
    private DevCap devCap;
    private ConnType connType; //主连接类型
    public boolean isMultiSplit;//是否是一拖多

    //       -1、未获取
    //        1、正常使用中
    //        2、未使用
    //        0、服务已过期
    //        9、服务被禁用
    public int cloudStatus = -1;//云存储状态
    protected int camSwitchStatus = OnOff.On;

    public List<SubDevice> subDevList;
    public Map<Integer,Integer> pinPortStatusMap;

    //设备参数信息
    public int deviceStatus;//0不在线，1在线，2休眠，休眠需要持续发唤醒指令
    public int charging;//是否充电中,1在充电  0未在充电
    public int battery = -1;//电量,-1未获取到电量
    public int lowpowerSwitch; //省电模式开关
    public int sdCardStatus;//-2卡异常，-1无卡， 0正常
    public boolean isSuport5G;//是否支持5G WIFI配网
    public boolean isSuport2p4G = true;//是否支持2.4G WIFI配网

    public int subScreen;//判断是否是1对多摄像头，4代表一个设备对应4个摄像头
    public boolean isPackageDevice;//判断是否是套装设备

    public String accessoriesCategory;
    public String accessoriesUrl;

    public String btWifiMac;   //蓝牙锁wifi分配mac
    public String btWifiDeviceId; //门锁wifi配网后分配地址
    public List<DevVideoQuality> videoQualityList = new ArrayList<>();

    public boolean supportMFMotion;  //免费检测是否开启

    public String btAddress = "9C:BC:F0:8D:36:80"; // 蓝牙MAC地址

    private String day; // 4G套餐剩余时间

    private String iccId; // 4G设备iccId

    private String endTime; // 4G设备套餐到期时间

    private int linkPlayTime; // 联动直播时长

    private int refreshDownloadTime; // 时光流影下载间隔时长 0：一天；1：一周；2：一月

    private int quality; // 清晰度

    public static class SubDevice {
        public String devId;      //主设备ID
        public int chnNum;        //子设备通道号
        public String subId;      //子设备ID
        public String subDevName; //子设备名称
        public boolean isOnline;        //子设备在线状态
    }

    public int getSubDeviceSize() {
        if (this.subDeviceListIsNotEmpty())
            return this.subDevList.size();
        else
            return 0;
    }

    public void saveSubDeviceList(List<SubDevice> subDeviceList) {
        if (subDeviceList == null || subDeviceList.isEmpty())
            return;
        if (subDeviceListIsNotEmpty()) {
            Iterator<SubDevice> iterator = this.subDevList.iterator();
            while (iterator.hasNext()) {
                SubDevice subDevice = iterator.next();
                boolean remove = true;
                for (SubDevice newSubDev : subDeviceList) {
                    if (subDevice.subId.equals(newSubDev.subId)) {
                        subDevice.isOnline = newSubDev.isOnline;
                        subDevice.subDevName = newSubDev.subDevName;
                        subDevice.chnNum = newSubDev.chnNum;
                        remove = false;
                        break;
                    }
                }
                if (remove)
                    iterator.remove();
            }
            Iterator<SubDevice> iterator1 = subDeviceList.iterator();
            while (iterator1.hasNext()) {
                SubDevice newSubDev = iterator1.next();
                boolean add = true;
                for (SubDevice subDevice : this.subDevList) {
                    if (newSubDev.subId.equals(subDevice.subId)) {
                        add = false;
                        break;
                    }
                }
                if (add)
                    this.subDevList.add(newSubDev);
            }
        } else {
            this.subDevList = Collections.synchronizedList(new ArrayList<SubDevice>());
            this.subDevList.addAll(subDeviceList);
        }
        Collections.sort(this.subDevList, new Comparator<SubDevice>() {
            @Override
            public int compare(SubDevice o1, SubDevice o2) {//在线的chnNum小排在前，不在线chnNum大的排在后
                if (o1.isOnline && !o2.isOnline) {
                    return -1;
                } else if (!o1.isOnline && o2.isOnline) {
                    return 1;
                } else {
                    return Integer.valueOf(o1.chnNum).compareTo(Integer.valueOf(o2.chnNum));
                }
            }
        });
    }

    public boolean subDeviceListIsNotEmpty() {
        return subDevList != null && !subDevList.isEmpty();
    }

    public SubDevice findSubDeviceByChannel(int channel) {
        if (subDeviceListIsNotEmpty()) {
            for (SubDevice subDevice : subDevList) {
                if (subDevice.chnNum == channel)
                    return subDevice;
            }
        }
        return null;
    }

    public SubDevice findSubDeviceBySubDevId(String subDevId) {
        if (subDeviceListIsNotEmpty() && !TextUtils.isEmpty(subDevId)) {
            for (SubDevice subDevice : subDevList) {
                if (subDevice.subId.equals(subDevId))
                    return subDevice;
            }
        }
        return null;
    }

    public boolean deleteSubDeviceById(String subDevId) {
        if (subDeviceListIsNotEmpty()) {
            Iterator<SubDevice> iterator = subDevList.iterator();
            while (iterator.hasNext()) {
                SubDevice subDevice = iterator.next();
                if (subDevice.subId.equals(subDevId)) {
                    iterator.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public void updateSubDeviceStatus(List<DeviceStatus.SubDeviceStatus> subDeviceStatusList) {
        if (subDeviceStatusList != null && !subDeviceStatusList.isEmpty()) {
            for (DeviceStatus.SubDeviceStatus subDeviceStatus : subDeviceStatusList) {
                SubDevice subDevice = findSubDeviceBySubDevId(subDeviceStatus.subId);
                if (subDevice != null) {
                    subDevice.isOnline = subDeviceStatus.status == 1;
                }
            }
        }
    }

    public int getCamSwitchStatus() {
        if (productType == TYPE_DOOR_BELL || devType == DeviceType.DOOR_BELL || devType == DeviceType.BATTERY_IPC) {
            return camSwitchStatus = OnOff.On;
        }
        return camSwitchStatus;
    }

    public void setCamSwitchStatus(int onoff) {
        if (productType == TYPE_DOOR_BELL || devType == DeviceType.DOOR_BELL || devType == DeviceType.BATTERY_IPC) {
            return;
        }
        this.camSwitchStatus = onoff;
    }

    @Deprecated
    public Device(String deviceUid) {
        this(deviceUid, null, TYPE_TUTK, null);
    }

    @Deprecated
    public Device(String deviceUid, String capTag, ConnType connType, GetDeviceListResponse.Cap cap) {
        this.devId = deviceUid;
        parseDap(cap);
        this.connType = connType;
    }

    private ConnType realConnType;
    public Device(String deviceUid, String capTag, ConnType connType, GetDeviceListResponse.Cap cap, int matchType) {
        this.devId = deviceUid;
        this.matchType = matchType;

        parseDap(cap);
        //如果服务器放回为空，这使用就方法确定设备连接类型
        if (connType == null) {
            try{
                connType = ConnType.parse(Integer.parseInt(deviceUid.substring(5, 6)));
            }catch (Exception e){
                if(!TextUtils.isEmpty(deviceUid) && deviceUid.length() >= 20)
                    connType = TYPE_TUTK;
            }
        }

        this.realConnType = connType;
//        2 你只发起P2P
//        3 你只发起TCP
//        4 优先TCP，再判断切换到P2P
        //TYPE_P2P_TCP 主连接为P2P
        this.connType = connType != null ? (connType == ConnType.TYPE_P2P_TCP ? ConnType.TYPE_P2P : connType) : ConnType.TYPE_P2P;
        dbg.D("Device", "streamType=" + streamType + ",isSupportCloud=" + isSuportCloud + ",isSupportTF=" + isSupportTF +
                ",productType=" + productType + ",connType=" + this.connType);

//        TODO TEST
//        this.matchType = 1;
//        this.isSuportCloud = true;
//        this.OSS_TYPE = OSS_aliyun;
    }

    /**
     * 平台端设备在线状态
     */
    public boolean isPlatDevOnline() {
        return isOnline;
    }

    public void setPlatDevOnline(boolean isOnline) {
        this.isOnline = isOnline;
        if (conn != null) {
            //统一在此处使用这个方法
            if(deviceStatus == DeviceStatus.ONLINE){
                conn.setPlatDevOnline(true);
            }else{
                //deviceStatus == DeviceStatus.OFFLINE || deviceStatus == DeviceStatus.SLEEP
                //如果设备不现在，设置离线，
                conn.setPlatDevOnline(false);
                ////清楚连接
                conn.setConnectStatus(ConnectStatus.CONNECT_LOST);
            }

            //门锁设备不进行P2P连接
            if(devType == DeviceType.BLOCK){
                conn.setPlatDevOnline(false);
            }
        }
    }

    private boolean isConnectEnable;
    public void setTcpConnectEnable(boolean isConnectEnable){
        this.isConnectEnable = isConnectEnable;
    }

    public boolean getTcpConnectEnable(){
        return this.isConnectEnable;
    }

    public void doConnectAll(){
        Set<Integer> keys = connectionMap.keySet();
        Iterator<Integer> iterator = keys.iterator();
        while (iterator.hasNext()){
            Integer next = iterator.next();
            Connection connection = connectionMap.get(next);
            connection.connect(next);
        }

        Set<Integer> tcpKeys = tcpConnectionMap.keySet();
        Iterator<Integer> iterator2 = tcpKeys.iterator();
        while (iterator2.hasNext()){
            Integer next = iterator2.next();
            Connection connection = connectionMap.get(next);
            connection.connect(next);
        }

    }

    public void doConnect(){
        Connection connection = getConnection();
        if(tcpConn != null && !connection.isConnected() && isConnectEnable){
            tcpConn.setPlatDevOnline(deviceStatus == DeviceStatus.ONLINE);
            tcpConn.connect(0);
        }
        connection.connect(0);
    }

    public void release(){
        Connection connection = getConnection();
        if(connection != null){
            connection.release();
        }
        if(tcpConn != null){
            tcpConn.release();
        }
    }

    private boolean doorCallingEnable = true;
    public boolean getPushDoorCallingEnable() {
        if (productType == TYPE_DOOR_BELL) {
            return doorCallingEnable;
        }
        return false;
//        return doorCallingEnable;
    }




    public void setConnType(ConnType connType) {
        Connection conn = getConnection();
        this.connType = connType;
        conn.setConnType(this.connType);
    }

    public ConnType getConnType() {
        return connType;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getIccId() {
        return iccId;
    }

    public void setIccId(String iccId) {
        this.iccId = iccId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getLinkPlayTime() {
        return linkPlayTime;
    }

    public void setLinkPlayTime(int linkPlayTime) {
        this.linkPlayTime = linkPlayTime;
    }

    public int getRefreshDownloadTime() {
        return refreshDownloadTime;
    }

    public void setRefreshDownloadTime(int refreshDownloadTime) {
        this.refreshDownloadTime = refreshDownloadTime;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public synchronized Connection getConnection() {
        dbg.D("GET_CONN", "conn:" + conn + "::" + devId + ",connType," + connType);
        if (conn == null) {
//            conn = new GosConnection(deviceUid,"","");
//            conn = new TutkConnection(deviceUid,isPushMsgOpen);

            String TUTK_USER = "admin";
            String TUTK_PSW = "goscam123";
            //TODO TEST;
            //String TUTK_PSW = "000000";//双模调试
            //conn = new TutkConnection(deviceUid,TUTK_USER,TUTK_PSW,isPushMsgOpen);
            conn = new UlifeConnection(devId, TUTK_USER, TUTK_PSW, connType, isPushMsgOpen);
            //conn = new UlifeConnection(deviceUid, TUTK_USER, TUTK_PSW, ConnType.TYPE_TCP, isPushMsgOpen);
            conn.init();
            setPlatDevOnline(isOnline);

            //TCP Connection
            if((devType == DeviceType.DOOR_BELL || devType == DeviceType.BATTERY_IPC) && ConnType.TYPE_P2P_TCP == realConnType){
                tcpConn = new UlifeConnection(devId, TUTK_USER, TUTK_PSW, ConnType.TYPE_TCP, isPushMsgOpen);
                //不可重连， TCP连接的目的是为了提高，直播出图速度，连接失败不重连
                tcpConn.setRetryConnectEnable(false);
//                tcpConn = new UlifeConnection(deviceUid, TUTK_USER, TUTK_PSW, ConnType.TYPE_P2P_TCP, isPushMsgOpen);
//                tcpConn.init();
//                tcpConn.setRetryConnectEnable(false);
            }
        }
        return conn;
    }

    public synchronized Connection getConnectionByChannel(int channel) {
        dbg.D("GET_CONN", "conn:" + conn + "::" + day + ",connType," + connType);
        Connection connection = connectionMap.get(channel);
        if(connection!=null){
            return connection;
        }
//        if (conn == null) {
//            conn = new GosConnection(deviceUid,"","");
//            conn = new TutkConnection(deviceUid,isPushMsgOpen);

            String TUTK_USER = "admin";
            String TUTK_PSW = "goscam123";
            //TODO TEST;
            //String TUTK_PSW = "000000";//双模调试
            //conn = new TutkConnection(deviceUid,TUTK_USER,TUTK_PSW,isPushMsgOpen);
            UlifeConnection conn = new UlifeConnection(devId, TUTK_USER, TUTK_PSW, connType, isPushMsgOpen);
            //conn = new UlifeConnection(deviceUid, TUTK_USER, TUTK_PSW, ConnType.TYPE_TCP, isPushMsgOpen);
            conn.init();
            setPlatDevOnline(isOnline);

            //TCP Connection
//            if(deviceType == DeviceType.DOOR_BELL || getTcpConnectEnable()){
//                tcpConn = new UlifeConnection(deviceUid, TUTK_USER, TUTK_PSW, ConnType.TYPE_TCP, isPushMsgOpen);
                //不可重连， TCP连接的目的是为了提高，直播出图速度，连接失败不重连
//                tcpConn.setRetryConnectEnable(false);
//                tcpConn = new UlifeConnection(deviceUid, TUTK_USER, TUTK_PSW, ConnType.TYPE_P2P_TCP, isPushMsgOpen);
//                tcpConn.init();
//                tcpConn.setRetryConnectEnable(false);
//            }
//        }
        connectionMap.put(channel,conn);
        return conn;
    }

    public synchronized Connection getTcpConnectionByChannel(int channel){
        Connection connection = tcpConnectionMap.get(channel);
        if(connection!=null){
            return connection;
        }
        String TUTK_USER = "admin";
        String TUTK_PSW = "goscam123";
        UlifeConnection tcpConn = new UlifeConnection(devId, TUTK_USER, TUTK_PSW, ConnType.TYPE_TCP, isPushMsgOpen);
        //不可重连， TCP连接的目的是为了提高，直播出图速度，连接失败不重连
        tcpConn.setRetryConnectEnable(false);
//                tcpConn = new UlifeConnection(deviceUid, TUTK_USER, TUTK_PSW, ConnType.TYPE_P2P_TCP, isPushMsgOpen);
//                tcpConn.init();
//                tcpConn.setRetryConnectEnable(false);
        connectionMap.put(channel,tcpConn);
        return tcpConn;
    }

    public synchronized Connection getTcpConnection(){
        getConnection();
        return tcpConn;
    }

    public synchronized void setConnection(Connection conn) {
        this.conn = conn;
    }

    private final static String STREAM_PSW_TAG = "_STREAM_PSW_TAG";
    public final static String DEFAULT_STREAM_PSW = "";//user

    public DevCap getDevCap() {
        if (devCap == null) {
            devCap = new DevCap();
        }
        dbg.E("DevCap", "" + devCap);
        return devCap;
    }

    public void updateDevCap(DevCap cap){
        devCap = cap;
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

            //0 - 不支持
            //1 - 支持阿里云
            //2 - 支持亚马逊云
            //3 - 支持联通云
            isSuportCloud = cap.cap26 > 0;
            if(isSuportCloud){
                OSS_TYPE = cap.cap26;
            }

            //支持的对讲类型
            talkType = cap.cap57;

            //是否为一拖多
            isMultiSplit = cap.cap58 == 1;

            //音频格式 0：AAC 1：G711A
            audioEncType = cap.cap59;

            //是否支持人脸识别
            aiSupportType = cap.cap60;


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



//            TODO TEST
//            isPackageDevice = true;
//            //TODO TEST
//            devCap.devResetType = 1;
//            //TODO TEST
//            devCap.isSupportWarnSoundLight = true;
//            devCap.interpolationType = 1;
        }
    }

    public int getTalkTypeSupported() {
        return talkType;
    }

    public void setTalkTypeSupported(int talkType) {
        this.talkType = talkType;
    }

    public boolean isSupportFullDuplex() {
        return talkType == TALK_FULL_DUPLEX || talkType == TALK_ALL_SUPPORT;
    }



    public String getStreamPsw() {
        if(TextUtils.isEmpty(streamUser) && TextUtils.isEmpty(streamPassword)){
            return "user@123";
        }else{
            return streamUser + "@" + streamPassword;
        }
    }



    //视频播放，时间校准，时区  0或者空：需要从设备能力集获取， 1：整点时区  2：半点时区   3：精确分钟时区（秒值）
    public int getPTimeZone(){
        DevCap devCap = getDevCap();
        if(timezoneVerifyType == 0 && devCap != null){
            timezoneVerifyType = devCap.timeZoneType;
        }

        int timezone;
        if(timezoneVerifyType == 2){
            //支持半小时时区
            timezone = (TimeZone.getDefault().getRawOffset() / 360000) + 24;//5:30->5.5时区， 改为55显示
            if (TimeZone.getDefault().inDaylightTime(new Date())) {
                timezone+=10;
            }
        }else if(timezoneVerifyType == 3){
            //支持4:45时区
            Calendar calendar = Calendar.getInstance();
            // 夏令时时间，比标准时间快1小时，即3600000毫秒，
            // 根据系统时间计算，如果不在夏令时生效范围内，则为0毫秒，反之为3600000毫秒
            int dstOffset = calendar.get(Calendar.DST_OFFSET);
            // 取得与GMT之间的时间偏移量，例如罗马属于东1区，则时间偏移量为3600000毫秒
            int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
            timezone = (zoneOffset + dstOffset) / 1000 + 24;
        }else {
            timezone = (TimeZone.getDefault().getRawOffset() / 3600000) + 24;// on the IPC side, -24,
            if(TimeZone.getDefault().inDaylightTime(new Date())){
                timezone++;
            }
        }
        return timezone;
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
            dbg.D("resetDevTime","isLightTime="+isLightTime);
        }
        return timeZone;
    }

    @Override
    public String toString() {
        return "Device{" +
                ", deviceUid='" + devId + '\'' +
                ", deviceName='" + devName + '\'' +
                ", createChn=" + createChn +
                ", isPlatDevOnline=" + isOnline +
                ", isOwn=" + isOwn +
                ", MAC='" + MAC + '\'' +
                ", devPsw='" + devPsw + '\'' +
                ", p2pId='" + p2pId + '\'' +
                ", isPushMsgOpen=" + isPushMsgOpen +
                '}';
    }

    @Override
    public int compareTo(Device o) {
        if (this.isPlatDevOnline() && o.isPlatDevOnline()) {
            return o.frequencyOfUse - this.frequencyOfUse;
        }
        if (!this.isPlatDevOnline() && !o.isPlatDevOnline()) {
            return o.frequencyOfUse - this.frequencyOfUse;
        }
        if (this.isPlatDevOnline() && !o.isPlatDevOnline()) {
            return -1;
        }
        if (!this.isPlatDevOnline() && o.isPlatDevOnline()) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return devId != null ? devId.equals(device.devId) : device.devId == null;
    }

    @Override
    public int hashCode() {
        return devId != null ? devId.hashCode() : 0;
    }
}
