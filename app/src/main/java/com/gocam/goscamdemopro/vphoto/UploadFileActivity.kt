package com.gocam.goscamdemopro.vphoto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.espressif.iot.esptouch.util.ZipUtil
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.cloud.data.GosCloud
import com.gocam.goscamdemopro.databinding.ActivitySelectImageVideoBinding
import com.gocam.goscamdemopro.utils.FileUtils
import com.gocam.goscamdemopro.vphoto.data.UploadFileResult
import com.gocam.goscamdemopro.vphoto.data.UploadUrlZipResult
import com.gos.platform.api.inter.OnPlatformEventCallback
import com.gos.platform.api.result.PlatResult
import org.json.JSONObject

class UploadFileActivity: BaseBindActivity<ActivitySelectImageVideoBinding>(), OnPlatformEventCallback {
    private lateinit var mDeviceId: String
    private var pathList = arrayListOf<String>()
    override fun getLayoutId(): Int {
        return R.layout.activity_select_image_video
    }

    companion object {
        fun startActivity(context: Context,  deviceId: String) {
            val intent = Intent(context, UploadFileActivity::class.java)
            intent.putExtra("DEV_ID", deviceId)
            context.startActivity(intent)
        }
    }

    override fun onCreateData(bundle: Bundle?) {
        mDeviceId = intent.getStringExtra("DEV_ID") as String
        GosCloud.getCloud().addOnPlatformEventCallback(this)
        mBinding?.apply {
            imgSelectPicture.setOnClickListener {
                pathList.add("/storage/emulated/0/Pictures/2024_01_08 16_09_22.jpg")
                val list = arrayListOf(mDeviceId)
                val jsonObject = JSONObject()
                jsonObject.put("device_id", list)
                showLoading()
                GosCloud.getCloud().uploadUrlZip(
                    "",
                    pathList.toString(),
                    jsonObject.toString(),
                    GApplication.app.vPhotoUser.vphotoAccessToken,
                    GApplication.app.user.userName,
                    GApplication.app.vPhotoUser.vphotUserId
                )
            }

            imgSelectVideo.setOnClickListener {
                pathList.add("/storage/emulated/0/Movies/TF-办公室5911-183219-02-21-2024-25S.mp4")
                val list = arrayListOf(mDeviceId)
                val jsonObject = JSONObject()
                jsonObject.put("device_id", list)
                showLoading()
                val mSelectVideoPath = pathList[0]
                val suffix = mSelectVideoPath.split(".")
                GosCloud.getCloud().uploadUrlVideo(
                    suffix.last(),
                    "",
                    pathList.toString(),
                    jsonObject.toString(),
                    GApplication.app.vPhotoUser.vphotoAccessToken,
                    GApplication.app.user.userName,
                    GApplication.app.vPhotoUser.vphotUserId
                )
            }
        }
    }

    override fun OnPlatformEvent(result: PlatResult?) {
        when(result?.platCmd) {
            PlatResult.PlatCmd.presignedUrlZip -> {
                val zipResult = result as UploadUrlZipResult
                val keyList = zipResult.uploadBean.key?.split(".")
                val url = zipResult.uploadBean.preSignedUrl
                var mPath: String
                if ("zip" == keyList?.last()) {
                    mPath = FileUtils.createRootPath(this) + "/" + System.currentTimeMillis() + ".zip";
                    ZipUtil.ZipFolder(pathList, mPath)
                } else{
                    mPath = pathList[0]
                }

                GosCloud.getCloud().uploadFile(
                    url,
                    mPath,
                    keyList?.last()
                )
            }
            PlatResult.PlatCmd.uploadFileVPhoto -> {
                dismissLoading()
                pathList.clear()
                val uploadResult = result as UploadFileResult
                if (result.responseCode == 0) {
                    ToastUtils.showLong(R.string.string_upload_success)
                } else {
                    ToastUtils.showLong(R.string.string_upload_failed, uploadResult.message)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GosCloud.getCloud().removeOnPlatformEventCallback(this)
    }
}