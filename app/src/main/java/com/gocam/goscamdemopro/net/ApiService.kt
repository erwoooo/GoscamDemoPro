package com.gocam.goscamdemopro.net

import com.gocam.goscamdemopro.entity.*
import com.golway.uilib.bean.BaseResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

interface ApiService {

    companion object{
        const val CONTENT_TYPE = "Content-Type:text/json; charset=utf-8"
        const val CONTENT_FORM = "Content-Type:application/x-www-form-urlencoded"
        const val CONTENT_HEAD = "Content-Type: application/json"
    }

    @POST(".")
    suspend fun login(@Body body:RequestBody): Response<BaseResponse<LoginBeanResult?>>
    
    @POST(".")
    suspend fun getBsAddress(@Body body: RequestBody):Response<BaseResponse<BsAddressResult?>>

    @POST(".")
    suspend fun register(@Body body: RequestBody):Response<BaseResponse<CommonResult?>>

    @POST(".")
    suspend fun modifyUserPassword(@Body body: RequestBody):Response<BaseResponse<CommonResult?>>

    @POST(".")
    suspend fun getVerificationCode(@Body body: RequestBody):Response<BaseResponse<CommonResult?>>

    @POST(".")
    suspend fun getDeviceList(@Body body: RequestBody):Response<BaseResponse<DeviceEntity?>>

    @POST(".")
    suspend fun getBindToken(@Body body: RequestBody):Response<BaseResponse<BindTokenResult?>>

    @POST(".")
    suspend fun queryUserBindResult(@Body body: RequestBody):Response<BaseResponse<BindStatusResult?>>

    @POST(".")
    suspend fun modifyDeviceAttr(@Body body: RequestBody):Response<BaseResponse<ModifyNameResult?>>

    @POST(".")
    suspend fun shareSmartDevice(@Body body: RequestBody):Response<BaseResponse<ShareDeviceResult?>>

    @POST(".")
    suspend fun getShareUserList(@Body body: RequestBody):Response<BaseResponse<ShareUserList?>>

    @POST(".")
    suspend fun unbindSharedSmartDevice(@Body body: RequestBody):Response<BaseResponse<ShareDeviceResult?>>

    @POST(".")
    suspend fun unbindSmartDevice(@Body body: RequestBody):Response<BaseResponse<ShareDeviceResult?>>

    @POST(".")
    suspend fun getDeviceParams(@Body body: RequestBody):Response<DeviceParamResponse?>

    @POST(".")
    suspend fun setDeviceParams(@Body body: RequestBody):Response<DeviceParamResponse?>

    @POST(".")
    suspend fun checkNewVer(@Body body: RequestBody):Response<BaseResponse<FirmWareParam?>>

    @POST(".")
    suspend fun getCMDParam(@Body body: RequestBody):Response<CmdResponseParam?>


    @POST(".")
    suspend fun wakeUpDevice(@Body body: RequestBody):Response<BaseResponse<WakeUpParam?>>

    @POST(".")
    suspend fun queryDeviceStatus(@Body body: RequestBody):Response<BaseResponse<DevicePlatStatus?>>

    @POST(".")
    suspend fun checkBindStatus(@Body body: RequestBody):Response<BaseResponse<BindStatus?>>
}