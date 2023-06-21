package com.gocam.goscamdemopro.data

import android.annotation.SuppressLint
import android.util.Log
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.entity.*
import com.gocam.goscamdemopro.net.RetrofitClient
import com.golway.uilib.bean.BaseResponse
import com.golway.uilib.utils.asyncTask
import com.google.gson.Gson
import com.gos.platform.api.ConfigManager
import com.gos.platform.api.GosSession
import com.gos.platform.api.devparam.DevParam.DevParamCmdType

import com.gos.platform.api.request.Request.MsgType.*
import com.gos.platform.device.GosConnection
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.ArrayList


@SuppressLint("StaticFieldLeak")
object RemoteDataSource : DataSource {
    private val TAG: String = "RemoteDataSource"
    private var isEncPsw = true
    private lateinit var mToken: String
    private val gsoSession: GosSession = GosSession.getSession()
    private val cmdArray = arrayListOf<String>()
    private val devParamArray = arrayListOf<BaseParamArray>()
    override suspend fun appGetBSAddress(
        userName: String,
        psw: String?
    ): BaseResponse<BsAddressResult?>? {
        val job = asyncTask {
            val map = mapOf(
                Pair("UserName", userName),
                Pair("Password", null),
                Pair("ServerType", arrayOf(3, 4)),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", AppGetBSAddressExRequest)
            )
            val json = Gson().toJson(nMap)
            val body = json.toRequestBody()
            val response = RetrofitClient.apiService.getBsAddress(body = body)

            return@asyncTask response.body()
        }

        val result = job.await()
        return if (result is BaseResponse<BsAddressResult?>) {
            result
        } else {
            null
        }

    }

    override suspend fun login(username: String, psw: String): LoginBeanResult? {
        val job = asyncTask {

            val map = mapOf(
                Pair("UserName", username),
                Pair("Password", gsoSession.encodeData(psw)),
                Pair("MobileCN", ""),
                Pair("UserType", GApplication.app.userType),
                Pair("ProtocolType", 1),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", LoginCGSARequest)
            )
            val json = Gson().toJson(nMap)
            val response = RetrofitClient.apiService.login(json.toRequestBody())

            return@asyncTask response.body()
        }

        val result = job.await()
        return if (result is BaseResponse<LoginBeanResult?>) {
            mToken = result.Body!!.AccessToken
            GApplication.app.user.userName = result.Body!!.UserName
            GApplication.app.user.sessionId = result.Body!!.SessionId
            GApplication.app.user.token = result.Body!!.AccessToken
            gsoSession.accessToken = result.Body!!.AccessToken
            gsoSession.userName = result.Body!!.UserName

            GosConnection.TransportProtype()
            result.Body
        } else {
            null
        }
    }

    override suspend fun getVerificationCode(
        findType: Int,
        userInfo: String,
        verifyWay: Int,
        userType: Int,
        mobileCN: String?,
        language: String?
    ): CommonResult? {
        val job = asyncTask {
            val map = mapOf(
                Pair("FindPasswordType", findType),
                Pair("UserInfo", userInfo),
                Pair("VerifyWay", verifyWay),
                Pair("UserType", userType),
                Pair("MobileCN", ""),
                Pair("Language", null),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", GetVerifyCodeRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()

            val response = RetrofitClient.apiService.getVerificationCode(json)

            return@asyncTask response.body()
        }

        val result = job.await()

        return if (result is BaseResponse<CommonResult?>)
            result.Body
        else
            null
    }

    override suspend fun register(
        userType: Int,
        registWay: Int,
        userName: String,
        psw: String,
        phoneNum: String?,
        emailAddr: String?,
        verifyCode: String,
        mobileCN: String?
    ): CommonResult? {
        val job = asyncTask {
            val map = mapOf(
                Pair("UserType", userType),
                Pair("RegisterWay", registWay),
                Pair("UserName", userName),
                Pair("Password", gsoSession.encodeData(psw)),
                Pair("PhoneNumber", phoneNum),
                Pair("EmailAddr", emailAddr),
                Pair("AreaId", ""),
                Pair("VerifyCode", verifyCode),
                Pair("MobileCN", mobileCN),

                )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", UserRegisterRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.register(json)
            return@asyncTask response.body()
        }

        val result = job.await()
        return if (result is BaseResponse<CommonResult?>)
            result.Body
        else
            null
    }

    override suspend fun modifyUserPassword(
        userName: String,
        code: String,
        newPsw: String,
        isEncPsw: Boolean
    ): CommonResult? {
        val job = asyncTask {
            val map = mapOf(
                Pair("UserName", userName),
                Pair("VerifyCode", code),
                Pair("NewPassword", gsoSession.encodeData(newPsw)),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", ModifyUserPasswordRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.modifyUserPassword(json)

            return@asyncTask response.body()
        }

        val result = job.await()

        return if (result is BaseResponse<CommonResult?>)
            result.Body
        else
            null
    }

    override suspend fun getDeviceList(userName: String): List<Device>? {
        val job = asyncTask {
            val map = mapOf(
                Pair("UserName", userName),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", GetUserDeviceListRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()

            val response = RetrofitClient.apiService.getDeviceList(json)

            return@asyncTask response.body()
        }

        val result = job.await()
        val deviceList = arrayListOf<Device>()
        result?.Body?.let {
            for (deviceBody in it.DeviceList) {   //组装成device
                val device = Device(
                    deviceBody.DeviceName,
                    deviceBody.DeviceId,
                    deviceBody.Status == 1,
                    deviceBody.DeviceType,
                    deviceBody.StreamUser,
                    deviceBody.StreamPassword,
                    deviceBody.Cap,
                    deviceBody.DeviceHdType,
                    deviceBody.DeviceSfwVer,
                    deviceBody.DeviceHdwVer
                )

                deviceList.add(device)
            }
        }
        return deviceList
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun getBindToken(userName: String, deviceId: String?): BindTokenResult? {
        val job = asyncTask {
            val map = mapOf(
                Pair("UserName", userName),
                Pair("DeviceId", null),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", GetBindTokenRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.getBindToken(json)

            return@asyncTask response.body()
        }

        val result = job.await()
        return if (result is BaseResponse<BindTokenResult?>) {
            result.Body
        } else
            null
    }

    override suspend fun queryUserBindResult(
        userName: String,
        bindToken: String
    ): BindStatusResult? {
        val job = asyncTask {
            val map = mapOf(
                Pair("UserName", userName),
                Pair("BindToken", bindToken),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", QueryUserBindResultRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.queryUserBindResult(body = json)

            return@asyncTask response.body()
        }

        val result = job.await()
        return if (result is BaseResponse<BindStatusResult?>)
            result.Body
        else
            null
    }

    override suspend fun modifyDeviceAttr(
        deviceId: String,
        deviceName: String,
        streamUser: String?,
        streamPsw: String?
    ): ModifyNameResult? {
        val job = asyncTask {
            val map = mapOf(
                Pair("DeviceId", deviceId),
                Pair("DeviceName", deviceName),
                Pair("StreamUser", streamUser),
                Pair("StreamPassword", streamPsw),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", ModifyDeviceAttrRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.modifyDeviceAttr(json)

            return@asyncTask response.body()
        }

        val result = job.await()

        return if (result is BaseResponse<ModifyNameResult?>)
            result.Body
        else
            null
    }

    override suspend fun shareSmartDevice(
        userName: String,
        deviceId: String,
        isOwner: Int,
        deviceName: String,
        deviceType: Int,
        streamUser: String?,
        streamPsw: String?,
        areaId: String?,
        appMatchType: Int
    ): ShareDeviceResult? {
        val job = asyncTask {
            val map = mapOf(
                Pair("UserName", userName),
                Pair("DeviceId", deviceId),
                Pair("DeviceOwner", isOwner),
                Pair("DeviceName", deviceName),
                Pair("DeviceType", deviceType),
                Pair("StreamUser", streamUser),
                Pair("StreamPassword", streamPsw),
                Pair("AreaId", areaId),
                Pair("AppMatchType", appMatchType),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", BindSmartDeviceRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.shareSmartDevice(json)

            return@asyncTask response.body()
        }

        val result = job.await()

        return if (result is BaseResponse<ShareDeviceResult?>)
            result.Body
        else
            null
    }

    override suspend fun getShareUserList(deviceId: String): ShareUserList? {
        val job = asyncTask {
            val map = mapOf(
                Pair("DeviceId", deviceId),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", GetShareUserListRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.getShareUserList(json)

            return@asyncTask response.body()
        }

        val result = job.await()

        return if (result is BaseResponse<ShareUserList?>)
            result.Body
        else
            null
    }

    override suspend fun unbindSharedSmartDevice(
        userName: String,
        deviceId: String,
        deviceOwner: Int
    ): ShareDeviceResult? {
        val job = asyncTask {
            val map = mapOf(
                Pair("UserName", userName),
                Pair("DeviceId", deviceId),
                Pair("DeviceOwner", 0),

                )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", UnbindSmartDeviceRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()

            val response = RetrofitClient.apiService.unbindSharedSmartDevice(json)

            return@asyncTask response.body()

        }
        val result = job.await()
        return if (result is BaseResponse<ShareDeviceResult?>)
            result.Body
        else
            null
    }

    override suspend fun unbindSmartDevice(
        userName: String,
        deviceId: String,
        deviceOwner: Int
    ): ShareDeviceResult? {
        val job = asyncTask {
            val map = mapOf(
                Pair("UserName", userName),
                Pair("DeviceId", deviceId),
                Pair("DeviceOwner", deviceOwner),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", UnbindSmartDeviceRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.unbindSmartDevice(json)

            return@asyncTask response.body()
        }

        val result = job.await()

        return if (result is BaseResponse<ShareDeviceResult?>)
            result.Body
        else
            null
    }

    override suspend fun getDeviceParam(vararg cmd: String, deviceId: String): List<ParamArray> {
        val job = asyncTask {
            cmdArray.clear()
            cmdArray.addAll(cmd)
            val param =
                GetDeviceParam(
                    cmdArray,
                    deviceId,
                    GApplication.app.user.sessionId!!,
                    GApplication.app.user.userName!!,
                    GApplication.app.userType

                )
            val map = mapOf(
                Pair("Body", param),
                Pair("MessageType", AppGetDeviceParamRequest),
            )

            val json = Gson().toJson(map).toRequestBody()
            val response = RetrofitClient.apiService.getDeviceParams(json)

            return@asyncTask response.body()?.Body
        }

        val result = job.await()
        Log.e(TAG, "getDeviceParam: $result")
        return if (result is ResponseBody) {
            result.ParamArray
        } else
            emptyList()

    }


    override suspend fun setDeviceParam(vararg baseParamArray: BaseParamArray, deviceId: String) {
        val job = asyncTask {
            devParamArray.clear()
            devParamArray.addAll(baseParamArray)

            val evSetBody = DevSetBody(
                deviceId,
                devParamArray,
                GApplication.app.user.sessionId!!,
                GApplication.app.user.token!!,
                GApplication.app.user.userName!!,
                GApplication.app.userType
            )
            val devSetParam = DevSetParam(
                evSetBody,
                AppSetDeviceParamRequest
            )

            val json = Gson().toJson(devSetParam).toRequestBody()

            val response = RetrofitClient.apiService.setDeviceParams(json)

            return@asyncTask response.body()
        }

        val result = job.await()

        Log.e(TAG, "setPirParam: $result")

    }

    override suspend fun getCmdParam(
        baseDeviceParam: BaseDeviceParam,
        deviceId: String
    ): CmdResponseBody? {
        val job = asyncTask {
            val cmdBody = CmdBody(
                GApplication.app.user.token!!,
                deviceId,
                baseDeviceParam,
                GApplication.app.user.sessionId!!,
                GApplication.app.user.userName!!,
                GApplication.app.userType,
            )

            val cmdRequestParam = CmdRequestParam(
                cmdBody,
                BypassParamRequest
            )

            val json = Gson().toJson(cmdRequestParam).toRequestBody()
            val response = RetrofitClient.apiService.getCMDParam(json)
            return@asyncTask response.body()

        }

        val result = job.await()
        return if (result is CmdResponseParam) {
            result.Body
        } else
            null
    }

    override suspend fun checkNewVer(deviceType: String): FirmWareParam? {
        val job = asyncTask {
            val map = mapOf(
                Pair("DeviceType", deviceType),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", CheckNewerVerRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.checkNewVer(json)

            return@asyncTask response.body()
        }

        val result = job.await()
        Log.e(TAG, "checkNewVer: $result")
        return if (result is BaseResponse<FirmWareParam?>)
            result.Body
        else
            null
    }

    override suspend fun wakeDevice(deviceId: String) : WakeUpParam?{
        val job = asyncTask {
            val map = mapOf(
                Pair("DeviceId", deviceId),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", WakeUpDeviceRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.wakeUpDevice(json)
            Log.e(TAG, "wakeDevice: $response", )
            return@asyncTask response.body()
        }

        val result = job.await()
        Log.e(TAG, "wakeDevice: $result", )
        return if(result is BaseResponse<WakeUpParam?>)
            result.Body
        else
            null
    }


    override suspend fun queryDeviceOnlineStatusSyn(deviceId: String) : DevicePlatStatus?{
        val job = asyncTask {
            val map = mapOf(
                Pair("DeviceId", deviceId),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", QueryDeviceOnlineStatusRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.queryDeviceStatus(json)
            Log.e(TAG, "wakeDevice: $response", )
            return@asyncTask response.body()
        }

        val result = job.await()
        return if (result is BaseResponse<DevicePlatStatus?>)
            result.Body
        else
            null
    }
}