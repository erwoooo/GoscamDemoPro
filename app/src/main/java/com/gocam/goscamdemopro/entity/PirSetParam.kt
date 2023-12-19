package com.gocam.goscamdemopro.entity

data class DevSetParam(
    val Body: DevSetBody,
    val MessageType: String
)

data class DevSetBody(
    val DeviceId: String,
    val ParamArray: List<BaseParamArray>,
    val SessionId: String?,
    val AccessToken: String?,
    val UserName: String,
    val UserType: Int
)

data class DevParamArray(
    val CMDType: String,
    val DeviceParam: BaseDeviceParam
) : BaseParamArray()

data class PirParam(
    val un_cdtime: Int,
    val un_sensitivy: Int,
    val un_stay: Int,
    val un_switch: Int
) : BaseDeviceParam()

data class TimeParam(
    val AppTimeSec: Int,
    val un_TimeZone: Int
) : BaseDeviceParam()

data class CameraParam(
    val device_switch: Int
) : BaseDeviceParam()

data class NightModeParam(
    var un_auto: Int,
    var un_day_night: Int
) : BaseDeviceParam()

data class MirrorModeParam(
    val mirror_mode:Int
):BaseDeviceParam()

data class LedSwitchParam(
    val device_led_switch: Int
) : BaseDeviceParam()

data class MicSwitchParam(
    val device_mic_switch: Int
) : BaseDeviceParam()

data class LowPowerParam(
    val a_doorbell_lowpower: Int
) : BaseDeviceParam()

data class MotionParam(
    var c_sensitivity: Int,
    var c_switch: Int,
    val e_algo_type: Int,
    val rect_x: Int,
    val rect_y: Int,
    val s_threshold: Int,
    val un_enable: Int,
    val un_enable_str: List<Int>,
    val un_height: Int,
    val un_mode: Int,
    val un_start_x: Int,
    val un_start_y: Int,
    val un_submode: Int,
    val un_width: Int
) : BaseDeviceParam()

data class PushIntervalParam(
    var interval:Int,
    var motion_detection_switch:Int,
    var sound_detection_switch:Int,
    var cry_alarm_switch:Int,
    var person_detection_switch:Int,
    var temperature_alarm_switch:Int,
    var pir_alarm_switch:Int,
    var schedule:Schedule,
    var cry_push_state:Int,
):BaseDeviceParam()

data class Schedule(
    var enable:Int,
    var repeat:Int,
    var start_time:Int,
    var end_time:Int
)

data class ResetPlanParam(
    val enable:Int,
    val repeat:Int,
    val start_time:Int,
):BaseDeviceParam()

data class DevSwitchParam(
    val device_switch:Int
):BaseDeviceParam()

data class CameraPlanParam(
    val enable:Int,
    val repeat:Int,
    val start_time:Int,
    val end_time:Int,
):BaseDeviceParam()

data class TfRecordParam(
    val manual_record_switch:Int
):BaseDeviceParam()

data class ObjTrackParam(
    val un_switch: Int
):BaseDeviceParam()


data class SoundDetectionParam(
    val un_switch:Int,
    val un_sensitivity:Int
):BaseDeviceParam()

data class VolumeSetParam(
    val volume:Int
): BaseDeviceParam()
data class FirmWareParam(
    val CGSId: String,
    val Desc: String,
    val DeviceType: String,
    val FileSize: Int,
    val MD5: String,
    val Url: String,
    val Version: String
)

data class UpgradeStatusParam(
    val upgrade_item_type: Int,
    val upgrade_progress: Int,
    val upgrade_status: Int,
) : BaseDeviceParam()


data class WakeUpParam(
    val DeviceId: String,
    val DstInfo: DstInfo,
    val SessionId: String,
    val SrcInfo: SrcInfo
)

data class DstInfo(
    val Session: String,
    val SvrId: String
)

data class SrcInfo(
    val Session: String,
    val SvrId: String
)

data class DevicePlatStatus(
    val DeviceId: String,
    val IsOnline: Int,
    val SessionId: String
)

data class PztCmdParam(
    val control:Int,
    val CMDType: Int,
    val channel:Int
):BaseDeviceParam()

data class BindStatus(
    val deviceID:String,
    val bindStatus:Int
)

data class RebootParam(
    val CMDType: Int,
    val channel:Int
):BaseDeviceParam()

data class WarnSettingParam(
    var un_switch:Int,
    val schedule:WarnSchedule,
    val audio:Audio,
    val light: Light
):BaseDeviceParam()

data class WarnSchedule(
    var un_switch: Int,
    var un_repeat:Int,
    var start_time:Int,
    var end_time:Int,
)
data class Audio(
    var un_switch: Int,
    var un_times: Int,
    var un_volume: Int,
    var un_type: Int,
    var url: String
)
data class Light(
    var un_switch: Int,
    var un_duration:Int,
)

data class SmartPersonParam(
    var un_switch:Int,
    var un_sensitivity:Int,
    var permcnt:Int,
    var perms:List<Perms>
):BaseDeviceParam()

data class Perms(
    var pcnt:Int,
    var rect:List<Rect>
)
data class Rect(
    var x:Int,
    var y:Int,
)