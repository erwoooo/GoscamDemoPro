package com.gocam.goscamdemopro.ipcset

import android.os.Bundle
import android.view.View
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityRebootLayoutBinding
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2023/12/14 11:48
 */
class RebootTimeActivity: BaseActivity<ActivityRebootLayoutBinding,RebootTimeViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_reboot_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev") as String

        mViewModel.getDevResetData(devId)

        mViewModel.apply {
            mResetPlanParam.observe(this@RebootTimeActivity){
                mBinding?.ivCloseReboot?.visibility = if (it.enable == OnOff.On){
                    View.VISIBLE
                }else{
                    View.INVISIBLE
                }
            }
        }
    }
}