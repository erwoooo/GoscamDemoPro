package com.gocam.goscamdemopro.entity

data class BindStatusResult(
    val Body: Body,
    val MessageType: String,
    val ResultCode: Int
)

data class Body(
    val CGSId: String,
    val ResultDescribe: String,
    val SessionId: String
)