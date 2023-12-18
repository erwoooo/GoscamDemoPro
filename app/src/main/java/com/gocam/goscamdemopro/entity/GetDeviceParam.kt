package com.gocam.goscamdemopro.entity

data class GetDeviceParam(
    val CMDTypeList: List<String>,
    val DeviceId: String,
    val SessionId: String,
    val UserName: String,
    val AccessToken: String,
    val UserType: Int
)