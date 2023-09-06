package com.gocam.goscamdemopro.entity

data class CmdRequestParam(
    val Body: CmdBody,
    val MessageType: String
)

data class CmdBody(
    val AccessToken: String,
    val DeviceId: String,
    val DeviceParam: BaseDeviceParam,
    val SessionId: String,
    val UserName: String,
    val UserType: Int
)


data class TfDeviceParam(
    val CMDType: Int,
    val format:Int,
    val channel: Int
) : BaseDeviceParam()

data class TFInfoParam(
    val CMDType: Int,
    val a_free_size: Int,
    val a_sd_status: Int,
    val a_total_size: Int,
    val a_used_size: Int
) : BaseDeviceParam()

data class GetUpgradeInfoRequestParam(
    val CMDType: Int,
    val upgrade_type: Int,
    val upgrade_channel: Int,
    val upgrade_url: String,
    val upgrade_version: String,
    val upgrade_md5: String,
    val upgrade_len: Int
):BaseDeviceParam()

data class KeepLiveParam(
    val CMDType: Int,
    val channel: Int
) : BaseDeviceParam()