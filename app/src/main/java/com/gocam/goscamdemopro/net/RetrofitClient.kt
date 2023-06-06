package com.gocam.goscamdemopro.net

import com.gos.platform.api.ConfigManager
import com.gos.platform.api.PlatformApiTask
import com.gos.platform.device.util.LogUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException

object RetrofitClient {

    private val mApiService: ApiService
    private val lock = Any()
    private var reponseJson //返回结果的json
            : String? = null
    val apiService: ApiService
        get() {
            synchronized(lock) {
                return mApiService
            }
        }

    init {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ConfigManager.getUrl())
            .build()
        mApiService = retrofit.create(ApiService::class.java)
    }


     fun doHttpRequest(data: String): Int {
        var data: String? = data
        var retVal: Int
        var connection: HttpURLConnection? = null
        //boolean isRetry = false;
        //do {
        try {
            val u = ConfigManager.getUrl()
            LogUtil.d("DEV_JNI", "URL=$u,data=$data")
            if (ConfigManager.serverType == ConfigManager.India_SERVER) {
                data = URLEncoder.encode(data, "UTF-8")
                LogUtil.d("DEV_JNI", "URLEncoder data=$data")
            }
            val url = URL(u)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection!!.connectTimeout = 10000
            connection.readTimeout = 30000
            connection.doOutput = true
            connection.doInput = true
            connection.useCaches = false
            connection.setRequestProperty(
                "Content-Type",
                "application/json;charset=utf-8"
            ) //设置参数类型是json格式
            connection.setRequestProperty("Content-Length", "" + (data?.toByteArray()?.size ?: 0))
            connection.connect()
            val writer = BufferedWriter(
                OutputStreamWriter(
                    connection.outputStream, "UTF-8"
                )
            )
            writer.write(data)
            writer.close()
            val responseCode = connection.responseCode
            LogUtil.d(
                "DEV_JNI",
                "URL=$u,responseCode=${responseCode}"
            )
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                reponseJson = getRetString(inputStream) //将流转换为字符串。
                LogUtil.d("DEV_JNI", "URL=$u,reponseJson=$reponseJson")
                inputStream.close()
            }
            retVal = if (responseCode == HttpURLConnection.HTTP_OK) 0 else responseCode
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.d(
                "DEV_JNI",
                "Exception=" + e.toString() + ",is SSl ex:" + (e is SSLException)
            )
            try {
                connection?.disconnect()
            } catch (ex: Exception) {
                LogUtil.d("DEV_JNI", "ex=" + e.localizedMessage)
            }
            retVal = -103

        }
        return retVal
    }


    @Throws(IOException::class)
    private fun getRetString(`is`: InputStream): String? {
        var buf: String? = null
        val reader = BufferedReader(InputStreamReader(`is`, "utf-8"))
        val sb = StringBuilder()
        var line = ""
        while (reader.readLine().also { line = it } != null) {
            sb.append(
                """
                $line
                
                """.trimIndent()
            )
        }
        `is`.close()
        buf = sb.toString()
        return buf
    }
}