package com.gocam.goscamdemopro.entity

import com.google.gson.JsonObject

data class CmdResponseParam(
    val Body: CmdResponseBody,
    val MessageType: String
)

data class CmdResponseBody(
    val AccessToken: String,
    val DeviceId: String,
    val DeviceParam: JsonObject,
    val SessionId: String,
    val UserName: String,
    val UserType: Int
)



