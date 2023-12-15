package com.gocam.goscamdemopro.add

import android.content.Intent
import android.os.Bundle
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.databinding.ActivityScanQrLayoutBinding
import com.gocam.goscamdemopro.utils.PermissionUtil

/**
 * @Author cw
 * @Date 2023/12/14 16:59
 */
class ScanQrCodeActivity: BaseBindActivity<ActivityScanQrLayoutBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_scan_qr_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        mBinding?.btnScanQr?.apply {
            setOnClickListener {
                if (PermissionUtil.verifyCameraPermissions(this@ScanQrCodeActivity)){
                    val intent = Intent(this@ScanQrCodeActivity,ZxingCaptureKtActivity::class.java)
                    this@ScanQrCodeActivity.startActivity(intent)
                }
            }
        }
    }
}