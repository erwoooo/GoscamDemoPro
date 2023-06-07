package com.gocam.goscamdemopro.data

import android.annotation.SuppressLint
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.entity.*
import com.gocam.goscamdemopro.net.RetrofitClient
import com.golway.uilib.bean.BaseResponse
import com.golway.uilib.utils.asyncTask
import com.google.gson.Gson
import com.gos.platform.api.GosSession

import com.gos.platform.api.request.Request.MsgType.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.ArrayList


@SuppressLint("StaticFieldLeak")
object RemoteDataSource : DataSource {

    private var isEncPsw = true
    private lateinit var mToken: String
    private val gsoSession: GosSession = GosSession.getSession()

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
        streamUser: String,
        streamPsw: String
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
        deviceOwner: Boolean
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
}