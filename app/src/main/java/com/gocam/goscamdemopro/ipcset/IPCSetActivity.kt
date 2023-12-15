package com.gocam.goscamdemopro.ipcset

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.databinding.ActivityIpcLayoutBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.set.TimeVerifyActivity
import com.gocam.goscamdemopro.utils.DeviceManager


/**
 * @Author hansan
 * @Date 2023/12/14 9:43
 */
class IPCSetActivity: BaseActivity<ActivityIpcLayoutBinding,IPCViewModel>(),
    CompoundButton.OnCheckedChangeListener {
    lateinit var mDevice:Device
    private var rebootDialog:Dialog?=null
    private var  rotateDialog:Dialog?=null
    private var  radioNormal:RadioButton?= null
    private var  radioHor:RadioButton?= null
    private var  radioVer:RadioButton?= null
    private var  radio180:RadioButton?= null
    private var smartNightDialog:Dialog?=null
    private var  radioColor:RadioButton?= null
    private var  radioBlack:RadioButton?= null
    private var  radioSmart:RadioButton?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_ipc_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev")
        mDevice = DeviceManager.getInstance().findDeviceById(devId)

        mBinding?.apply {
            //camera time verify
            btnTimeVerify.setOnClickListener {
                val intent = Intent(this@IPCSetActivity, TimeVerifyActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@IPCSetActivity.startActivity(intent)
            }

            //reboot device
            btnReboot.setOnClickListener {
                rebootDeviceDialog()
            }

            btnTfRecord.setOnClickListener {
                val intent = Intent(this@IPCSetActivity,SwitchActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@IPCSetActivity.startActivity(intent)
            }

            btnLightStatus.setOnClickListener {
                val intent = Intent(this@IPCSetActivity,SwitchActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@IPCSetActivity.startActivity(intent)
            }

            btnObjectTracking.setOnClickListener {
                val intent = Intent(this@IPCSetActivity,SwitchActivity::class.java)
                intent.putExtra("dev", mDevice.devId)
                this@IPCSetActivity.startActivity(intent)
            }

            btnAlarmPush.setOnClickListener {

            }

            btnVideoRotate.setOnClickListener {
                rotateVideoDialog()
            }

            btnNightVision.setOnClickListener {
                smartNightVersionDialog()
            }

        }


    }

    private fun rebootDeviceDialog(){
        if (rebootDialog == null){
            rebootDialog  = Dialog(this,R.style.DialogNoBackground)
            val rebootView = LayoutInflater.from(this).inflate(R.layout.dialog_operation_layout,null,false)
            rebootDialog!!.setContentView(rebootView)
            rebootView.findViewById<TextView>(R.id.tv_cancel).apply {
                setOnClickListener {
                rebootDialog!!.dismiss()
            }
            }

            rebootView.findViewById<TextView>(R.id.tv_yes).apply {
                setOnClickListener {
                    rebootDialog!!.dismiss()
                }
            }

            val param:ViewGroup.LayoutParams = rebootView.layoutParams
            param.width = 800
        }
        rebootDialog!!.show()

    }


    private fun rotateVideoDialog(){
        if (rotateDialog == null){
            rotateDialog = Dialog(this,R.style.DialogNoBackground)
            val rotateView = LayoutInflater.from(this).inflate(R.layout.dialog_rotate_layout,null,false)
            rotateDialog!!.setContentView(rotateView)
            radioNormal = rotateView.findViewById(R.id.radio_normal)
            radioHor = rotateView.findViewById(R.id.radio_horizontal)
            radioVer = rotateView.findViewById(R.id.radio_vertical)
            radio180 = rotateView.findViewById(R.id.radio_180)
            radioNormal?.apply {
                setOnCheckedChangeListener(this@IPCSetActivity)
            }
            radioHor?.apply {
                setOnCheckedChangeListener(this@IPCSetActivity)
            }
            radioVer?.apply {
                setOnCheckedChangeListener(this@IPCSetActivity)
            }
            radio180?.apply {
                setOnCheckedChangeListener(this@IPCSetActivity)
            }
            val param:ViewGroup.LayoutParams = rotateView.layoutParams
            param.width = 800
        }
        rotateDialog!!.show()

    }


    private fun smartNightVersionDialog(){
        if (smartNightDialog == null){
            smartNightDialog = Dialog(this,R.style.DialogNoBackground)
            val smartView = LayoutInflater.from(this).inflate(R.layout.dialog_smart_layout,null,false)
            smartNightDialog!!.setContentView(smartView)
            radioColor = smartView.findViewById(R.id.radio_color)
            radioBlack = smartView.findViewById(R.id.radio_black)
            radioSmart = smartView.findViewById(R.id.radio_smart)
            radioSmart?.apply {
                setOnCheckedChangeListener(this@IPCSetActivity)
            }
            radioColor?.apply {
                setOnCheckedChangeListener(this@IPCSetActivity)
            }
            radioBlack?.apply {
                setOnCheckedChangeListener(this@IPCSetActivity)
            }
            val param:ViewGroup.LayoutParams = smartView.layoutParams
            param.width = 800
        }
        smartNightDialog!!.show()
    }


    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

    }


}