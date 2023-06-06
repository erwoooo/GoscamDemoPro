package com.gocam.goscamdemopro.entity

import com.gos.platform.api.response.GetDeviceListResponse

data class DeviceEntity(
    var DeviceList: List<DeviceBody>,
    var EndFlag: Int
)

data class DeviceBody(
    var DeviceId: String? = null,
    var DeviceOwner ////0-分享、1-拥有
    : Int = 0,
    var DeviceName: String? = null,
    var DeviceType: Int = 0,
    var Status: Int = 0,
    var StreamUser: String? = null,
    var StreamPassword: String? = null,
    var AreaId: String? = null,
    var DeviceCap //设备具有的能力集
    : String? = null,
    var MediaTransportType: Int = 0,

    var DeviceSfwVer // 设备软件版本号:C_900.GT4027.018.001
    : String? = null,
    var DeviceHdwVer // 设备硬件版本号
    : String? = null,

    var DeviceHdType //设备硬件类型
    : String? = null,
    //0-中性 1-pro
    var MatchType: Int = 0,

    var Cap: GetDeviceListResponse.Cap? = null,

    var UseStatus //0-正常  1-试用   2-禁用
    : Int = 0,
    var SaleType //0-普通销售渠道    1-国内新零售模式   2-国外新零售模式
    : Int = 0,
    var Hours //设备试用剩余时间
    : Int = 0,

    var AccessoriesCategory //设备图片分组
    : String? = null,
    var AccessoriesUrl //设备图片地址
    : String? = null
)