package com.gocam.goscamdemopro.entity

data class BindStatusResult(
    val CGSId: String,
    val ResultDescribe: String,
    val SessionId: String
)

data class Body(
    val CGSId: String,
    val ResultDescribe: String,
    val SessionId: String
)