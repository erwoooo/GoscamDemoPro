package com.gocam.goscamdemopro.entity

import com.gos.platform.api.result.PlatResult.PlatCmd

class PlatResult {
    var platCmd: PlatCmd? = null
    var eventType: Int? = 0
    var code: Int? = 0
    var json: String? = null
}