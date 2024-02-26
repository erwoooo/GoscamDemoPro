package com.gocam.goscamdemopro.n12

import com.gocam.goscamdemopro.entity.BaseDeviceParam

/**
 * @Author cw
 * @Date 2024/1/4 15:53
 */

data class N12CmdParam(
    var param: N12SetParam?,
    var un_type:Int,
    var CMDType:Int,
    var channel:Int,
):BaseDeviceParam()

data class N12SetParam(
    var audio: Audio?,
    var light: Light?,
    var mood_light: MoodLight?,
    var un_id: Int?,
    var un_switch: Int?,
    var un_type: Int?
)

data class Audio(
    var palylist: List<Palylist?>?,
    var un_effect: Int?,
    var un_mode: Int?,
    var un_repeat: Int?,
    var un_volume: Int?
)

data class Light(
    var un_brightness: Int?,
    var un_color: Int?,
    var un_effect: Int?,
    var un_mode: Int?,
    var un_random: Int?
)

data class MoodLight(
    var un_color: Int?,
    var un_effect: Int?,
    var un_switch: Int?
)

data class Palylist(
    var un_class: Int?,
    var un_cycle: Int?,
    var un_list: List<Int?>?
)