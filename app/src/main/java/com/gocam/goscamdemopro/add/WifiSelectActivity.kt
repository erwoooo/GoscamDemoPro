package com.gocam.goscamdemopro.add

import android.content.Intent
import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.base.BaseViewModel
import com.gocam.goscamdemopro.ble.BleScanActivity
import com.gocam.goscamdemopro.databinding.ActivityWifiSelectBinding
import com.gocam.goscamdemopro.utils.PermissionUtil

class WifiSelectActivity : BaseBindActivity<ActivityWifiSelectBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_wifi_select
    }

    override fun onCreateData(bundle: Bundle?) {

        mBinding?.toolBar?.backImg?.setOnClickListener {
            finish()
        }
        mBinding?.apply {

            btnIpc.setOnClickListener {
                val ssid = etSsid.text.toString()
                val psw = etPassword.text.toString()
                if (psw.isEmpty() || ssid.isEmpty())
                    return@setOnClickListener
                val intent = Intent(this@WifiSelectActivity, QrCodeActivity::class.java)
                intent.putExtra("ssid",ssid)
                intent.putExtra("psw",psw)
                this@WifiSelectActivity.startActivity(intent)
            }

            btnScanBle.setOnClickListener {
                val ssid = etSsid.text.toString()
                val psw = etPassword.text.toString()
                if (psw.isEmpty() || ssid.isEmpty())
                    return@setOnClickListener
                val intent = Intent(this@WifiSelectActivity, BleScanActivity::class.java)
                intent.putExtra("ssid",ssid)
                intent.putExtra("psw",psw)
                this@WifiSelectActivity.startActivity(intent)
            }

            btnScanQr.setOnClickListener {

              val intent = Intent(this@WifiSelectActivity,ScanQrCodeActivity::class.java)
              this@WifiSelectActivity.startActivity(intent)
            }

        }
    }
}