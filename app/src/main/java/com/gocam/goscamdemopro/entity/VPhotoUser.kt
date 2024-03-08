package com.gocam.goscamdemopro.entity

class VPhotoUser {
    var vphotUserId: Int = 0 // VPhoto userId
    var indexVPhoto: Int = 0 // Number of current bound devices
    var totalVPhoto: Int = 0 // Number of devices to be bound
    var vphotoAccessToken: String? = null // VPhoto Token
    var vphotoUserSystemToken: String? = null // VPhoto SystemToken
    var devVPhotoUid: String? = null // VPhoto deviceId
    var linkName: String? = null // VPhoto name
    var linkDevices: String? = null // VPhoto associated deviceId
    var devUid: String? = null // device userId
    var ssid: String? = null // WIFI account
    var psw: String? = null // WIFI psw
    var isVPhotoPackage: Boolean = false
}