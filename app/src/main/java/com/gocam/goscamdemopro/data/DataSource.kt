package com.gocam.goscamdemopro.data

import com.gocam.goscamdemopro.entity.*
import com.golway.uilib.bean.BaseResponse
import com.gos.platform.api.result.LoginResult

interface DataSource {

    //get appGetBSAddress
    suspend fun appGetBSAddress(userName: String,psw: String?):BaseResponse<BsAddressResult?>?
    //login
    suspend fun login(username:String,psw:String):LoginBeanResult?

    //get verification code
    suspend fun getVerificationCode(findType:Int,userInfo:String,verifyWay:Int,userType:Int,mobileCN:String?,language:String?):CommonResult?

    //register
    suspend fun register(userType:Int,registWay:Int,userName:String,psw:String,phoneNum:String?,emailAddr:String?,verifyCode:String,mobileCN:String?):CommonResult?

    //modifyUserPassword
    suspend fun modifyUserPassword(userName:String,oldPsw:String,newPsw:String,isEncPsw: Boolean):CommonResult?

    //getDeviceList
    suspend fun getDeviceList(userName: String):List<Device>?

    //logout
    suspend fun logout()

    //addDevice getBindToken
    suspend fun getBindToken(userName: String,deviceId:String?): BindTokenResult?

    //check bind status
    suspend fun queryUserBindResult(userName: String,bindToken:String):BaseResponse<BindStatusResult?>?

    //modify device alias
    suspend fun modifyDeviceAttr(deviceId:String,deviceName:String,streamUser:String?,streamPsw:String?):ModifyNameResult?

    // share device
    suspend fun shareSmartDevice(userName: String,deviceId:String,isOwner:Int,deviceName:String,deviceType:Int,streamUser:String?,streamPsw:String?,areaId:String?,appMatchType:Int):ShareDeviceResult?

    // get a list of user you've shared
    suspend fun getShareUserList(deviceId:String):ShareUserList?

    //delete user from shared lists
    suspend fun unbindSharedSmartDevice(userName: String,deviceId: String,deviceOwner:Int):ShareDeviceResult?

    //unbind device
    suspend fun unbindSmartDevice(userName: String,deviceId: String,deviceOwner: Int):ShareDeviceResult?


    //device param get
    suspend fun getDeviceParam(vararg cmd:String,deviceId:String):List<ParamArray>

    //set device param
    suspend fun setDeviceParam(vararg baseParamArray: BaseParamArray,deviceId: String)

    //getCMD
    suspend fun getCmdParam(baseDeviceParam: BaseDeviceParam,deviceId: String):CmdResponseBody?

    suspend fun checkNewVer(deviceType: String):FirmWareParam?

    suspend fun wakeDevice(deviceId: String):WakeUpParam?

    suspend fun queryDeviceOnlineStatusSyn(deviceId: String):DevicePlatStatus?

    suspend fun setPzt(deviceId: String, baseDeviceParam: BaseDeviceParam)

    //check bind status
    suspend fun checkBindStatus(deviceId: String):BindStatus?

}