package com.golway.uilib.bean

data class BaseResponse<T>(
    val Body: T,
    val MessageType: String,
    val ResultCode: Int
)
