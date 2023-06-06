package com.gocam.goscamdemopro.entity

import com.gos.platform.api.response.AppGetBsAddressResponse.RServer

class BsAddressResult {
    var UserName: String? = null
    var ServerList: List<RServer>? = null
    var CryptKey //加密密钥
            : String? = null
    var PushType = 0
}