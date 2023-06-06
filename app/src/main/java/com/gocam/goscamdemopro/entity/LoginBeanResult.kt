package com.gocam.goscamdemopro.entity

data class LoginBeanResult(
    val AccessToken: String,
    val CGSId: String,
    val EffectiveTime: Int,
    val KeepAliveTime: Int,
    val ProtocolType: Int,
    val RefreshToken: String,
    val RefreshTokenTime: Int,
    val SessionId: String,
    val SessionIdEx: String,
    val UserName: String,
    val UserToken: String
)