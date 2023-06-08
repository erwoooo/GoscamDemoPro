package com.gocam.goscamdemopro.entity

import com.google.gson.JsonObject

data class DeviceParamResponse(
    val Body: ResponseBody,
    val MessageType: String,
    val ResultCode: Int
)

data class ResponseBody(
    val AccessToken: String,
    val CGSId: String,
    val DeviceId: String,
    val ParamArray: List<ParamArray>,
    val SessionId: String,
    val UserName: String
)

data class ParamArray(
    val CMDType: String,
    val DeviceParam: JsonObject
)
