package com.gocam.goscamdemopro.add

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityZxCaptureBinding
import com.gos.platform.api.contact.BindStatus
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils

/**
 * @Author cw
 * @Date 2023/12/15 9:40
 */
class ZxingCaptureKtActivity: BaseActivity<ActivityZxCaptureBinding,ZxingCaptureViewModel>() {
    lateinit var mCaptureFragment:CaptureFragment
    lateinit var devId:String
    val TAG = "ZxingCaptureKtActivity"
    override fun getLayoutId(): Int {
        return R.layout.activity_zx_capture
    }

    override fun onCreateData(bundle: Bundle?) {
        mCaptureFragment = CaptureFragment()
        CodeUtils.setFragmentArgs(mCaptureFragment,R.layout.zxing_camera)
        mCaptureFragment.analyzeCallback = object :CodeUtils.AnalyzeCallback{
            override fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String?) {
                mCaptureFragment.analyzeCallback = null
                Log.e(TAG, "onAnalyzeSuccess: result = $result")
                devId = result!!;
                mViewModel.queryBindStatus(devId)
            }

            override fun onAnalyzeFailed() {
                mCaptureFragment.analyzeCallback = null
                Log.e(TAG, "onAnalyzeFailed" )
            }

        }
        supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, mCaptureFragment)
            .commit()
        mViewModel.bindStatus.observe(this){
            when(it?.bindStatus){
                BindStatus.BIND->{
                    finish()
                    Log.e(TAG, "queryBindStatus: The device has been bound" )
                }
                BindStatus.SHARE_BIND, BindStatus.OWN_BIND->{
                    finish()
                    Log.e(TAG, "queryBindStatus: You have bound the device" )
                }
                BindStatus.UNBIND->{
                    Log.e(TAG, "queryBindStatus: device not bound" )
                    mViewModel.getBindToken(devId)
                }
            }
        }

        mViewModel.bindToken.observe(this){
            val mIntent = Intent(this,CheckBindStatusActivity::class.java)
            mIntent.putExtra("token",it?.BindToken)
            startActivity(mIntent)
            finish()
        }
    }



}