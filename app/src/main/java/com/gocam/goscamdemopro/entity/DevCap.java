package com.gocam.goscamdemopro.entity;

import com.gos.platform.device.domain.DevCapabilityA;
import com.gos.platform.device.domain.DevCapabilityB;
import com.gos.platform.device.domain.DevCapabilityC;


public class DevCap {
    public static final int CAP_A = 1;
    public static final int CAP_B = 2;
    public static final int CAP_C = 3;
    public static final int CAP_D = 4;


    public int id;

    public String deviceID;

    public int capType;


    public boolean hasPir;

    public boolean hasPtz;
    public int ptzType;
    public boolean hasPreset;//是否支持预置位

    public boolean hasMic;

    public boolean hasSpeaker;

    public boolean hasTemp;


    //DevType.GOS;

    public int devType;

    public boolean hasIc;

    public boolean hasSdCard;
    public boolean isSupportTimeZone;
    public int timeZoneType; //1.整点时区， 2.半点时区
    public boolean hasNightVison;
    public int nightVisonType;//1是夜视，2是全彩夜视
    public boolean hasEthernet;
    //SmartType.UNSUPPORT
    public int smartType;
    public boolean hasMotionDetection;
    public int motionType;//1.支持区移动侦测  2.支持自定义移动侦测  3.支持区块和时间区间设置
    public boolean isSupportRecord;

    //EthernetType.LAN
    //0	支持WIFI
    //1	有线
    //2	2.4G WIFI+有线
    //3	5G WIFI
    //4	2.4G/5G双混频WIFI
    //5	4G流量
    //6 2.4G/5G双混频WIFI+有线
    public int ethernetType;
    public boolean hasLightFlag;
    public boolean hasVoiceDetection;

    public boolean isSupportLullaby;
    public int lullabyType;

    public boolean hasBattery;      //是否有电池
    public boolean isSupportWakeOn; //远程唤醒
    public boolean isSupportLedSw; //状态灯开关
    public boolean isSupportCamSw; //摄像头开关
    public int camSwitchType; //0不支持开关，1，支持开关 2，支持开关和计划
    public boolean isSupportMicSw; //麦克风开关
    public boolean isSupportCloud; //是否支持云存储
    public boolean isSupportStreamPsw; //是否支持视频密码

    public boolean isSupportBatteryLevel;
    public boolean isSupportNetlinkSignal;

    public boolean isSupportAlexaSkills;
    public boolean isSupportAlexaVoice;
    public boolean isSupportEhco;
    public boolean isSupportShow;
    public boolean isSupportGoogleHome;

    public boolean isSupportDoorbellRing;
    public boolean isSupportPirDistance;
    public boolean isSupportCameraSetting;//是否支持摄像头计划
    public boolean isSupportCryAlarm;//是否支持哭声报警

    public boolean isSupportIotSensor;//是否支持IOT设备
    public int supportIotSensorTypes;//支持的IOT设备类型

    public boolean isSupportSoundLight;//声光告警支持
    public int smartMotionDetection;//人形侦测支持
    public int smartObjMotionDetection;//物体跟踪支持
    public int humanMotionDetection;//人形跟踪支持

    public int httpsSupport;//设备软件是否支持https升级

    public int isSupportSpeakerVolume;//是否支持设置音量
    public int isSupportPushInterval;//是否支持设置推送间隔

    public int p2pEncryption;//流加密
    public int devResetType;//设置重启计划设置

    public boolean isSupDoorbellLed;//led灯提醒能力集
    public boolean isSupDoorbellRemoveAlarm;//强拆报警
    public boolean isSupDoorbellLowpower;//低电量
    public int doorbellBandwidth;//宽动态

    public int mainStream;
    public int mainWidth;
    public int mainHeight;

    //全天录像
    public int recordAlarmAllType;
    //图像翻转   1 180°翻转   2.支持水平，竖直，180°
    public int mirrorMode;

    //警戒功能
    public boolean isSupportWarnSoundLight;
    //插值设备
    public int interpolationType;
    // 是否有外设
    public boolean isPeripheral;

    public DevCap parseDevCap(String deviceID,DevCapabilityA a){
        this.deviceID = deviceID;
        this.capType = CAP_A;

        this.hasPir = a.hasPir;
        this.hasPtz = a.hasPtz;
        this.hasMic = a.hasMic;
        this.hasSpeaker = a.hasSpeaker;
        this.hasTemp = a.hasTemp;

        return this;
    }

    public DevCap parseDevCap(String deviceID,DevCapabilityB b){
        this.deviceID = deviceID;
        this.capType = CAP_B;

        this.devType = b.devType;
        this.hasIc = b.hasIc;
        this.hasPir = b.hasPir;
        this.hasPtz = b.hasPtz;
        this.hasMic = b.hasMic;
        this.hasSpeaker = b.hasSpeaker;
        this.hasSdCard = b.hasSdCard;
        this.hasTemp = b.hasTemp;
        this.isSupportTimeZone = b.isSupportTimeZone;
        this.hasNightVison = b.hasNightVison;
        this.hasEthernet = b.hasEthernet;
        this.smartType = b.smartConnect;
        this.hasMotionDetection = b.hasMotionDetection;
        this.isSupportRecord = b.isSupportRecord;

        return this;
    }

    public DevCap parseDevCap(String deviceID,DevCapabilityC c){
        this.deviceID = deviceID;
        this.capType = CAP_C;

        this.devType = c.devType;
        this.hasIc = c.hasIc;
        this.hasPir = c.hasPir;
        this.hasPtz = c.hasPtz;
        this.hasMic = c.hasMic;
        this.hasSpeaker = c.hasSpeaker;

        this.hasSdCard = c.hasSdcard;
        this.hasTemp = c.isSupportTemp;
        this.isSupportTimeZone = c.isSupportTimezone;
        this.timeZoneType = c.timeZoneType;
        this.hasNightVison = c.hasNightVison;
        this.nightVisonType = c.nightVisonType;
        this.ethernetType = c.ethernetType;
        this.smartType = c.smartType;
        this.hasMotionDetection = c.isSupportMotion;
        this.motionType = c.motionType;
        this.isSupportRecord = c.isSupportRecord;
        this.hasLightFlag = c.hasLightFlag;
        this.hasVoiceDetection = c.hasVoiceDetection;

        this.isSupportLullaby = c.isSupportLullaby;
        this.lullabyType = c.lullabyType;


        this.hasBattery = c.hasBattery;      //是否有电池
        this.isSupportWakeOn = c.isSupportWakeOn; //远程唤醒
        this.isSupportLedSw = c.isSupportLedSw; //状态灯开关
        this.isSupportCamSw = c.isSupportCamSw; //摄像头开关
        this.isSupportMicSw = c.isSupportMicSw; //麦克风开关
        this.isSupportCloud = c.isSupportCloud; //是否支持云存储

        this.isSupportStreamPsw = c.isSupportStreamPsw;
        this.isSupportNetlinkSignal = c.isSupportNetlinkSignal;
        this.isSupportBatteryLevel = c.isSupportBatteryLevel;

        this.isSupportAlexaSkills = c.isSupportAlexaSkills;
        this.isSupportAlexaVoice = c.isSupportAlexaVoice;
        this.isSupportEhco = c.isSupportEhco;
        this.isSupportShow = c.isSupportShow;
        this.isSupportGoogleHome = c.isSupportGoogleHome;

        this.isSupportDoorbellRing = c.isSupportDoorbellRing;
        this.isSupportPirDistance = c.isSupportPirDistance;

        this.isSupportIotSensor = c.isSupportIotSensor;
        this.supportIotSensorTypes = c.supportIotSensorTypes;
        this.isSupportCryAlarm = c.isSupportCryAlarm;

        this.isSupportSoundLight = c.isSupportSoundLight;
        this.smartMotionDetection=c.smartMotionDetection;
        this.smartObjMotionDetection=c.smartObjMotionDetection;

        this.httpsSupport=c.httpsSupport;
        this.humanMotionDetection=c.humanTrackingFlag;
        this.isSupportSpeakerVolume=c.speakerVolume;
        this.isSupportPushInterval=c.pushInterval;


        this.p2pEncryption=c.p2pEncryption;
        this.isSupDoorbellLed = c.isSupDoorbellLed;
        this.isSupDoorbellRemoveAlarm = c.isSupDoorbellRemoveAlarm;
        this.isSupDoorbellLowpower = c.isSupDoorbellLowpower;
        this.doorbellBandwidth = c.doorbellBandwidth;

        this.mainStream = c.mainStream;//主码率
        if(c.mainStream > 0){
            this.mainWidth = mainStream >> 16;
            this.mainHeight = mainStream - (mainWidth << 16);
        }

        this.recordAlarmAllType = c.recordAlarmAllType;
        return this;
    }

    //大于 1080 ，认为是高码率 (1920<<16) +1080    (2304<<16) + 1296
    //width = 150996240 >> 16  height = 150996240 - (2304<<16)
    // 000036 订单
    //public boolean isHighBitRate(){
    //    return mainHeight > 1080;
    //}

    @Override
    public String toString() {
        return "DevCap{" +
                "id=" + id +
                ", deviceID='" + deviceID + '\'' +
                ", capType=" + capType +
                ", hasPir=" + hasPir +
                ", hasPtz=" + hasPtz +
                ", hasMic=" + hasMic +
                ", hasSpeaker=" + hasSpeaker +
                ", hasTemp=" + hasTemp +
                ", devType=" + devType +
                ", hasIc=" + hasIc +
                ", hasSdCard=" + hasSdCard +
                ", isSupportTimeZone=" + isSupportTimeZone +
                ", timeZoneType=" + timeZoneType +
                ", hasNightVison=" + hasNightVison +
                ", hasEthernet=" + hasEthernet +
                ", smartType=" + smartType +
                ", hasMotionDetection=" + hasMotionDetection +
                ", motionType=" + motionType +
                ", isSupportRecord=" + isSupportRecord +
                ", ethernetType=" + ethernetType +
                ", hasLightFlag=" + hasLightFlag +
                ", hasVoiceDetection=" + hasVoiceDetection +
                ", isSupportLullaby=" + isSupportLullaby +
                ", lullabyType=" + lullabyType +
                ", hasBattery=" + hasBattery +
                ", isSupportWakeOn=" + isSupportWakeOn +
                ", isSupportLedSw=" + isSupportLedSw +
                ", isSupportCamSw=" + isSupportCamSw +
                ", isSupportMicSw=" + isSupportMicSw +
                ", isSupportCloud=" + isSupportCloud +
                ", isSupportStreamPsw=" + isSupportStreamPsw +
                ", isSupportBatteryLevel=" + isSupportBatteryLevel +
                ", isSupportNetlinkSignal=" + isSupportNetlinkSignal +
                ", isSupportAlexaSkills=" + isSupportAlexaSkills +
                ", isSupportAlexaVoice=" + isSupportAlexaVoice +
                ", isSupportIotSensor=" + isSupportIotSensor +
                ", supportIotSensorTypes=" + supportIotSensorTypes +
                ", isSupportDoorbellRing=" + isSupportDoorbellRing +
                ", isSupportCryAlarm=" + isSupportCryAlarm +
                ", p2pEncryption=" + p2pEncryption +
                ", mainStream=" + mainStream +
                ", mainWidth=" + mainWidth +
                ", mainHeight=" + mainHeight +
                '}';
    }
}
