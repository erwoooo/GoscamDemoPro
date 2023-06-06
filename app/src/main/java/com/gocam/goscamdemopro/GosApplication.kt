package com.gocam.goscamdemopro

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import com.gocam.goscamdemopro.entity.User
import com.gocam.goscamdemopro.net.RetrofitClient
import com.gocam.goscamdemopro.utils.Util
import com.gos.avplayer.GosMediaPlayer
import com.gos.platform.api.ConfigManager
import com.gos.platform.api.Goscam
import com.gos.platform.device.PlatformType
import com.gos.platform.device.contact.TransportProType

class GosApplication : Application() {

    var user: User? = null
    val userType = 32

    override fun onCreate() {
        super.onCreate()

        if (!shouldInit(this))
            return
        GosMediaPlayer.init()

        Goscam.setDebugenble(true)
        val transportProType = TransportProType.NETPRO_ENABLE_ALL

        Goscam.init(this, PlatformType.ULIFE, transportProType, 0, userType, true, 2, null)
        ConfigManager.IS_ENCRYPT = true
        ConfigManager.serverType = ConfigManager.CN_SERVER
    }



    companion object {
        val application = this
    }

    init {
        System.loadLibrary("AVAPIs")
        System.loadLibrary("IOTCAPIs")
        System.loadLibrary("NetProSDK")
        System.loadLibrary("GosNetSDK")
        System.loadLibrary("DevPlatform")

        System.loadLibrary("AVPlayer")
        System.loadLibrary("avplayercodec")

        System.loadLibrary("voiceRecog")

        user = User()
    }

    fun shouldInit(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos = am.runningAppProcesses
        val mainProcessName = context.packageName
        val myPid = Process.myPid()
        for (info in processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true
            }
        }
        return false
    }


}