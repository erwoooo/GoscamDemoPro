package com.gocam.goscamdemopro.entity

/**
 * @Author cw
 * @Date 2023/12/19 10:17
 */
class VoicePlayParam {
    var VoiceList:List<Param>?=null

}

data class Param(
    val VoicePlayId:Int,
    val VoiceUrl:String,
    val PlayLanguage:String,
    val Describe:String,
)