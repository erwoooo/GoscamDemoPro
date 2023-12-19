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
import com.gocam.goscamdemopro.cloud.data.entity.EventType
import com.gocam.goscamdemopro.databinding.ActivityIpcLayoutBinding
import com.gocam.goscamdemopro.entity.*
import com.gocam.goscamdemopro.set.TimeVerifyActivity
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gos.platform.api.UlifeResultParser.EventType.BYPASS_RESET_DEV_REQ
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.device.contact.MirrorMode
import com.gos.platform.device.contact.OnOff


/**
 * @Author hansan
 * @Date 2023/12/14 9:43
 */
class IPCSetActivity: BaseActivity<ActivityIpcLayoutBinding,IPCViewModel>(),
    RadioGroup.OnCheckedChangeListener {
    lateinit var mDevice:Device
    private var rebootDialog:Dialog?=null
    private var  rotateDialog:Dialog?=null
    private var  radioNormal:RadioButton?= null
    private var  radioHor:RadioButton?= null
    private var  radioVer:RadioButton?= null
    private var  radio180:RadioButton?= null
    private var radioRotateGroup:RadioGroup?=null
    private var smartNightDialog:Dialog?=null
    private var  radioColor:RadioButton?= null
    private var  radioBlack:RadioButton?= null
    private var  radioSmart:RadioButton?= null
    private var  radioNightGroup:RadioGroup?= null
    lateinit var devId:String
    override fun getLayoutId(): Int {
        return R.layout.activity_ipc_layout
    }

    override fun onCreateData(bundle: Bundle?) {
         devId = intent.getStringExtra("dev") as String
        mDevice = DeviceManager.getInstance().findDeviceById(devId)

        mViewModel.getData(devId)

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
                val intent =Intent(this@IPCSetActivity,AlarmPushActivity::class.java)
                intent.putExtra("dev",mDevice.devId)
                this@IPCSetActivity.startActivity(intent)
            }

            btnVideoRotate.setOnClickListener {
                rotateVideoDialog()
            }

            btnNightVision.setOnClickListener {
                smartNightVersionDialog()
            }

            btnSpeakVolume.setOnClickListener {
                val intent =Intent(this@IPCSetActivity,IntercomVolumeActivity::class.java)
                intent.putExtra("dev",mDevice.devId)
                this@IPCSetActivity.startActivity(intent)
            }

            btnRestartPlan.setOnClickListener {
                val intent =Intent(this@IPCSetActivity,RebootTimeActivity::class.java)
                intent.putExtra("dev",mDevice.devId)
                this@IPCSetActivity.startActivity(intent)
            }

            btnSoundDetection.setOnClickListener {
                val intent =Intent(this@IPCSetActivity,SoundDetectionActivity::class.java)
                intent.putExtra("dev",mDevice.devId)
                this@IPCSetActivity.startActivity(intent)
            }

            btnWakePlan.setOnClickListener {
                val intent =Intent(this@IPCSetActivity,WakeUpActivity::class.java)
                intent.putExtra("dev",mDevice.devId)
                this@IPCSetActivity.startActivity(intent)
            }

            btnSoundLight.setOnClickListener {
                val intent =Intent(this@IPCSetActivity,WarnSoundLightActivity::class.java)
                intent.putExtra("dev",mDevice.devId)
                this@IPCSetActivity.startActivity(intent)
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
                    mViewModel.rebootParam(devId)
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
            radioRotateGroup = rotateView.findViewById(R.id.radio_group_rotate)
            radioRotateGroup!!.setOnCheckedChangeListener(this)
            val param:ViewGroup.LayoutParams = rotateView.layoutParams
            param.width = 800
            mViewModel?.apply {
                mMirrorModeParam.observe(this@IPCSetActivity){
                    when(it.mirror_mode){
                        MirrorMode.NONE->{
                            radioNormal?.isChecked = true
                            radioHor?.isChecked = false
                            radioVer?.isChecked = false
                            radio180?.isChecked = false
                        }
                        MirrorMode.HOR->{
                            radioNormal?.isChecked = false
                            radioHor?.isChecked = true
                            radioVer?.isChecked = false
                            radio180?.isChecked = false
                        }
                        MirrorMode.VER->{
                            radioNormal?.isChecked = false
                            radioHor?.isChecked = false
                            radioVer?.isChecked = true
                            radio180?.isChecked = false
                        }
                        MirrorMode.HOR_VER->{
                            radioNormal?.isChecked = false
                            radioHor?.isChecked = false
                            radioVer?.isChecked = false
                            radio180?.isChecked = true
                        }
                    }
                }

            }

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
            radioNightGroup = smartView.findViewById(R.id.radio_night)
            val param:ViewGroup.LayoutParams = smartView.layoutParams
            param.width = 800

            mViewModel.mNightModeParam.observe(this){
                if (it.un_auto == OnOff.On){
                    radioSmart?.isChecked = true
                    radioBlack?.isChecked = false
                    radioColor?.isChecked = false
                }else{
                    radioSmart?.isChecked = false
                    if (it.un_day_night == OnOff.On ){
                        radioColor?.isChecked = true
                        radioBlack?.isChecked = false
                    }else{
                        radioColor?.isChecked = false
                        radioBlack?.isChecked = true
                    }
                }

            }
            radioNightGroup!!.setOnCheckedChangeListener(this)
        }
        smartNightDialog!!.show()

    }


    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){

            R.id.radio_normal->{
                val mirrorModeParam = MirrorModeParam(MirrorMode.NONE)
                val devParam = DevParamArray(
                    DevParam.DevParamCmdType.MirrorModeSetting,
                    mirrorModeParam
                )
                mViewModel.setSwitchParam(devParam, devId = devId)
            }
            R.id.radio_horizontal->{
                val mirrorModeParam = MirrorModeParam(MirrorMode.HOR)
                val devParam = DevParamArray(
                    DevParam.DevParamCmdType.MirrorModeSetting,
                    mirrorModeParam
                )
                mViewModel.setSwitchParam(devParam, devId = devId)
            }
            R.id.radio_vertical->{
                val mirrorModeParam = MirrorModeParam(MirrorMode.VER)
                val devParam = DevParamArray(
                    DevParam.DevParamCmdType.MirrorModeSetting,
                    mirrorModeParam
                )
                mViewModel.setSwitchParam(devParam, devId = devId)
            }
            R.id.radio_180->{
                val mirrorModeParam = MirrorModeParam(MirrorMode.HOR_VER)
                val devParam = DevParamArray(
                    DevParam.DevParamCmdType.MirrorModeSetting,
                    mirrorModeParam
                )
                mViewModel.setSwitchParam(devParam, devId = devId)
            }

            R.id.radio_smart->{
                val nightModeParam = NightModeParam(OnOff.On,OnOff.Off)
                val devParamArray  = DevParamArray(
                    DevParam.DevParamCmdType.NightSetting,
                    nightModeParam
                )
                mViewModel.setSwitchParam(devParamArray, devId = devId)
            }
            R.id.radio_color->{

                val nightModeParam = NightModeParam(OnOff.Off,OnOff.On)
                val devParamArray  = DevParamArray(
                    DevParam.DevParamCmdType.NightSetting,
                    nightModeParam
                )
                mViewModel.setSwitchParam(devParamArray, devId = devId)
            }
            R.id.radio_black->{

                val nightModeParam = NightModeParam(OnOff.Off,OnOff.Off)
                val devParamArray  = DevParamArray(
                    DevParam.DevParamCmdType.NightSetting,
                    nightModeParam
                )
                mViewModel.setSwitchParam(devParamArray, devId = devId)
            }
        }

    }


}