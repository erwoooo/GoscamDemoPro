package com.gocam.goscamdemopro.baby

import com.gos.platform.api.result.GetSleepInfoResult.SleepTime
import com.gos.platform.api.result.GetTotalTimeResult

/**
 *
 * @Author wuzb
 * @Date 2025/09/02 15:26
 */ 
class GetSleepInfoResult {
    var SessionId: String = "" // 通信会话
    var CGSId: String = "" // 服务器标识
    var TotalSleepTime: Int = 0 // 总睡眠时间(单位: 秒)
    var MaxSleepTime: Int = 0 // 最长睡眠时间(单位: 秒)
    var DeepSleepTime: Int = 0 // 深睡时间(单位: 秒)
    var LightSleepTime: Int = 0 // 浅睡时间(单位: 秒)
    var CryingCount: Int = 0 // 哭闹次数
    var ActivityCount: Int = 0 // 活动次数
    var SleepTimeList: MutableList<SleepTime>? = null // 睡眠时间列表
    var ActivityTimeList: MutableList<SleepTime>? = null // 活动时间列表
    var CryingTimeList: MutableList<SleepTime>? = null // 哭闹时间列表
}

class GetTotalTimeResult {
    var SessionId: String = "" // 通信会话
    var CGSId: String = "" // 服务器标识
    var DeviceId: String = "" // 总睡眠时间(单位: 秒)
    val TotalSleepTimeList: MutableList<GetTotalTimeResult.DayTime>? = null // 每天总睡眠时长
}