package com.gocam.goscamdemopro.add

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityQrcodeBinding
import com.gocam.goscamdemopro.utils.QRBitmapCreater
import com.gos.platform.api.ConfigManager
import kotlinx.coroutines.launch

class QrCodeActivity : BaseBindActivity<ActivityQrcodeBinding>(), QRBitmapCreater.QRBitmapCallback {
    private lateinit var ssid: String
    private lateinit var psw: String

    override fun getLayoutId(): Int {
        return R.layout.activity_qrcode
    }

    override fun onCreateData(bundle: Bundle?) {
        ssid = intent.getStringExtra("ssid").toString()
        psw = intent.getStringExtra("psw") as String

        showLoading()
        lifecycleScope.launch {
            val result = RemoteDataSource.getBindToken(GApplication.app.user.userName!!, "")
            result?.let {
                val token = it.BindToken

                createQr(token!!)

                dismissLoading()
            }
        }

        mBinding?.apply {
            btnNext.setOnClickListener {
                val intent = Intent(this@QrCodeActivity, CheckBindStatusActivity::class.java)
                this@QrCodeActivity.startActivity(intent)
            }
        }
    }


    private fun createQr(bindToken: String) {

        val appName = resources.getString(R.string.app_name)
        val sS = ConfigManager.getInstance().curServer
        var server = ""
        if (TextUtils.equals(sS, ConfigManager.S_CN_URL)) {
            server = "CN"
        } else if (TextUtils.equals(sS, ConfigManager.S_EN_URL)) {
            server = "US"
        }
        val qrText = """
            $appName
            $ssid
            $psw
            $bindToken
            $server
            0
            """.trimIndent()
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val p = Point()
        wm.defaultDisplay.getSize(p)
        val mWidth = Math.min(p.x, p.y)
        QRBitmapCreater(qrText, mWidth, mWidth, this).execute()
    }

    override fun onQRBitmapCreated(bmp: Bitmap?, created: Boolean) {
        if (created && bmp != null) {
            mBinding?.qrImage?.setImageBitmap(bmp)
            mBinding?.btnNext?.isEnabled = true
        }
    }
}