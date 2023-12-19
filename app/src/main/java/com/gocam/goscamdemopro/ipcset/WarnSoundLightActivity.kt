package com.gocam.goscamdemopro.ipcset

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityWarnSetLayoutBinding
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.Param
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2023/12/18 17:41
 */
class WarnSoundLightActivity: BaseActivity<ActivityWarnSetLayoutBinding,WarnSoundLightViewModel>(),CompoundButton.OnCheckedChangeListener,
    SeekBar.OnSeekBarChangeListener,RadioGroup.OnCheckedChangeListener {
    lateinit var deviceId:String
    val voicePlayList = arrayListOf<Param>()
    override fun getLayoutId(): Int {
        return R.layout.activity_warn_set_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        deviceId = intent.getStringExtra("dev") as String
        mViewModel.getWarnData(deviceId)
        mViewModel.getPlayContent(deviceId)
        mViewModel.apply {
            mWarnSettingParam.observe(this@WarnSoundLightActivity){
                mBinding?.apply {
                    swWarnFun.apply {
                        setOnCheckedChangeListener(null)
                        isChecked = it.un_switch == OnOff.On
                        setOnCheckedChangeListener(this@WarnSoundLightActivity)
                    }

                    swSoundWarn.apply {
                        setOnCheckedChangeListener(null)
                        isChecked = it.audio.un_switch == OnOff.On
                        setOnCheckedChangeListener(this@WarnSoundLightActivity)
                    }

                    swLightWarn.apply {
                        setOnCheckedChangeListener(null)
                        isChecked = it.light.un_switch == OnOff.On
                        setOnCheckedChangeListener(this@WarnSoundLightActivity)
                    }

                }
                it.schedule.let {
                    schedule->
                    run {
                        if (schedule.un_switch == OnOff.Off) {
                            mBinding?.tvWarnTimeContent?.text = "not open"
                        } else {
                            val EVERY_DAY = 127
                            val time8 = 8 * 60 * 60 //8点

                            val time20 = 20 * 60 * 60 //20点
                            if (schedule.un_repeat == EVERY_DAY && schedule.start_time == time8) {
                                mBinding?.tvWarnTimeContent?.text = "day time"
                            } else if (schedule.un_repeat == EVERY_DAY && schedule.start_time == time20) {
                                mBinding?.tvWarnTimeContent?.text = "night time"
                            } else {
                                mBinding?.tvWarnTimeContent?.text = "custom"
                            }

                        }
                    }

                }

                it.audio.let {
                    audio->
                    run {
                        mBinding?.tvSoundWarnTimesContent?.text = "${audio.un_times}"
                        mBinding?.seekSoundWarn?.progress = audio.un_volume
                        mBinding?.tvSoundWarnVolumeValue?.text = "${audio.un_volume}"
                        mBinding?.tvSoundWarnContentValue?.text = "${audio.un_type}"
                    }

                }

                it.light.let {
                    light ->
                    run {
                        mBinding?.tvLightDurationContent?.text = "${light.un_duration}"
                    }
                }
            }

            mVoicePlayParam.observe(this@WarnSoundLightActivity){
                it.VoiceList?.let { it1 ->
                    voicePlayList.clear()
                    voicePlayList.addAll(it1) }
            }

        }

        mBinding?.apply {
            tvSoundWarnContent.setOnClickListener {
                playContentDialog()
            }
            seekSoundWarn.setOnSeekBarChangeListener(this@WarnSoundLightActivity)
            tvSoundWarnTimes.setOnClickListener {
                soundAlarmDialog()
            }
            tvLightDuration.setOnClickListener {
                lightFlashDialog()
            }
            tvWarnTime.setOnClickListener {
                if (scrollPushPlan.visibility == View.VISIBLE){
                    scrollPushPlan.visibility = View.GONE
                }else{
                    scrollPushPlan.visibility = View.VISIBLE
                }
            }

            tvClosePlan.setOnClickListener {
                ivClosePlan.visibility = View.VISIBLE
                ivDayPlan.visibility = View.GONE
                ivNightPlan.visibility = View.GONE
                constrainCustom.visibility = View.GONE
                mViewModel.mWarnSettingParam.value?.let {
                    it.schedule.un_switch = OnOff.Off
                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }

            }

            tvDayPlan.setOnClickListener {
                ivClosePlan.visibility = View.GONE
                ivDayPlan.visibility = View.VISIBLE
                ivNightPlan.visibility = View.GONE
                constrainCustom.visibility = View.GONE
                mViewModel.mWarnSettingParam.value?.let {
                    it.schedule.un_switch = OnOff.On
                    it.schedule.start_time = 28800
                    it.schedule.end_time = 72000
                    it.schedule.un_repeat = 127
                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }

            }

            tvNightPlan.setOnClickListener {
                ivClosePlan.visibility = View.GONE
                ivDayPlan.visibility = View.GONE
                ivNightPlan.visibility = View.VISIBLE
                constrainCustom.visibility = View.GONE
                mViewModel.mWarnSettingParam.value?.let {
                    it.schedule.un_switch = OnOff.On
                    it.schedule.start_time = 72000
                    it.schedule.end_time = 28800
                    it.schedule.un_repeat = 127
                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }

            }

            tvClosePlan.setOnClickListener {
                ivClosePlan.visibility = View.GONE
                ivDayPlan.visibility = View.GONE
                ivNightPlan.visibility = View.GONE
                constrainCustom.visibility = View.VISIBLE
            }

            btnYes.setOnClickListener {
                val startTime:Int = etStartH.text.toString().toInt()  * 3600 +  etStartM.text.toString().toInt() * 60
                val endTime:Int = etEndH.text.toString().toInt() * 3600 + etEndM.text .toString().toInt() * 60

                var repeat = 0;
                if (checkboxSun.isChecked){
                    repeat = repeat.or( 0x01)
                }
                if (checkboxMon.isChecked){
                    repeat = repeat.or( (0x01).shl(1))
                }
                if (checkboxTue.isChecked){
                    repeat = repeat.or( (0x01).shl(2))
                }
                if (checkboxWed.isChecked){
                    repeat = repeat.or( (0x01).shl(3))
                }
                if (checkboxThu.isChecked){
                    repeat = repeat.or( (0x01).shl(4))
                }
                if (checkboxFri.isChecked){
                    repeat = repeat.or( (0x01).shl(5))
                }
                if (checkboxSat.isChecked){
                    repeat = repeat.or( (0x01).shl(6))
                }

                mViewModel.mWarnSettingParam.value?.let {
                    it.schedule.un_switch = OnOff.On
                    it.schedule.un_repeat = repeat
                    it.schedule.start_time = startTime
                    it.schedule.end_time = endTime

                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)

                }

            }

        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

        when(buttonView?.id){
            R.id.sw_warn_fun->{
                mViewModel.mWarnSettingParam.value?.let {
                    it.un_switch = if (isChecked){
                        OnOff.On
                    }else{
                        OnOff.Off
                    }

                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )

                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }
            }
            R.id.sw_sound_warn->{
                mViewModel.mWarnSettingParam.value?.let {
                    it.audio.un_switch = if (isChecked){
                        OnOff.On
                    }else{
                        OnOff.Off
                    }

                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )

                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }

            }
            R.id.sw_light_warn->{
                mViewModel.mWarnSettingParam.value?.let {
                    it.light.un_switch = if (isChecked){
                        OnOff.On
                    }else{
                        OnOff.Off
                    }

                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )

                    mViewModel.setSwitchParam(devParamArray,deviceId)
                }
            }
        }

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {


    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        mViewModel.mWarnSettingParam.value?.let {
            it.audio.un_volume = seekBar?.progress!!

            val devParamArray = DevParamArray(
                DevParam.DevParamCmdType.WarnSetting,
                it
            )
            mBinding?.tvSoundWarnVolumeValue?.text="${seekBar.progress}"
            mViewModel.setSwitchParam(devParamArray,deviceId)
        }
    }

    var playContentDialog:Dialog?= null
    private fun playContentDialog(){
        if (playContentDialog == null){
            playContentDialog = Dialog(this,R.style.DialogNoBackground)
            val playContentView = LayoutInflater.from(this).inflate(R.layout.dialog_play_content_layout,null,false)
            playContentDialog!!.setContentView(playContentView)
            val rvPlayContent:RecyclerView = playContentView.findViewById(R.id.rv_play_content)
            val playAdapter = PlayContentAdapter(mViewModel.mVoicePlayParam.value?.VoiceList!!)
            playAdapter.setPlayListener(playContentListener)
            rvPlayContent.apply {
                layoutManager = LinearLayoutManager(this@WarnSoundLightActivity)
                adapter = playAdapter
            }
            val param:ViewGroup.LayoutParams = playContentView.layoutParams
            param.width = 1080

        }
        playContentDialog!!.show()

    }

    private val playContentListener = object :OnPlayContentListener{
        override fun onItemClick(param: Param) {
            mViewModel.mWarnSettingParam.value?.let {
                it.audio.un_type = param.VoicePlayId
                it.audio.url = param.VoiceUrl

                val devParamArray = DevParamArray(
                    DevParam.DevParamCmdType.WarnSetting,
                    it
                )
                mViewModel.setSwitchParam(devParamArray,deviceId)

            }
            mBinding?.tvSoundWarnContentValue?.text = "${param.VoicePlayId}"
            playContentDialog!!.dismiss()


        }

    }


    inner class PlayContentAdapter(list: List<Param>):RecyclerView.Adapter<PlayViewHolder>(){
        var onPlayContentListener:OnPlayContentListener?=null
        val arrayParam = list

        fun setPlayListener(onContentListener: OnPlayContentListener){
            onPlayContentListener = onContentListener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayViewHolder {
            val view:View = LayoutInflater.from(parent.context).inflate(R.layout.item_play_content_layout,null,false)
            val playViewHolder = PlayViewHolder(view)
             playViewHolder.tvContent.setOnClickListener {
                onPlayContentListener?.onItemClick(playViewHolder.itemView.tag as Param)
            }

            return playViewHolder
        }

        override fun onBindViewHolder(holder: PlayViewHolder, position: Int) {
            holder.tvContent.text = "id:${arrayParam[position].VoicePlayId}  des:${arrayParam[position].Describe}"
            holder.itemView.tag = arrayParam[position]
        }

        override fun getItemCount(): Int {
            return arrayParam.size
        }

    }

    interface OnPlayContentListener{
        fun onItemClick(param: Param)
    }

    inner class PlayViewHolder(view:View):RecyclerView.ViewHolder(view){
        var tvContent:TextView
        init {
            tvContent = view.findViewById(R.id.tv_content)
        }



    }



    var soundAlarmDialog:Dialog?=null
    private fun soundAlarmDialog(){
        if (soundAlarmDialog == null){
            soundAlarmDialog = Dialog(this,R.style.DialogNoBackground)
            val soundAlarmView = LayoutInflater.from(this).inflate(R.layout.dialog_sound_times_layout,null,false)
            soundAlarmDialog!!.setContentView(soundAlarmView)
            val soundGroup: RadioGroup = soundAlarmView.findViewById(R.id.radio_group_sound)
            soundGroup.setOnCheckedChangeListener(this)
            val param:ViewGroup.LayoutParams = soundAlarmView.layoutParams
            param.width = 1080
        }
        soundAlarmDialog!!.show()
    }
    var lightFlashDialog:Dialog?=null
    private fun lightFlashDialog(){
        if (lightFlashDialog == null){
            lightFlashDialog = Dialog(this,R.style.DialogNoBackground)
            val lightFlashView = LayoutInflater.from(this).inflate(R.layout.dialog_flash_interval_layout,null,false)
            lightFlashDialog!!.setContentView(lightFlashView)
            val lightGroup:RadioGroup = lightFlashView.findViewById(R.id.radio_group_flash_interval)
            lightGroup.setOnCheckedChangeListener(this)
            val param:ViewGroup.LayoutParams = lightFlashView.layoutParams
            param.width = 1080
        }
        lightFlashDialog!!.show()
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){
            R.id.radio_1->{
                mViewModel.mWarnSettingParam.value?.let {
                    it.audio.un_times = 1
                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                    mBinding?.tvSoundWarnTimesContent?.text = "1"
                }
            }
            R.id.radio_3->{
                mViewModel.mWarnSettingParam.value?.let {
                    it.audio.un_times = 3
                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                    mBinding?.tvSoundWarnTimesContent?.text = "3"
                }
            }
            R.id.radio_5->{
                mViewModel.mWarnSettingParam.value?.let {
                    it.audio.un_times = 5
                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                    mBinding?.tvSoundWarnTimesContent?.text = "5"
                }
            }
            R.id.radio_10->{
                mViewModel.mWarnSettingParam.value?.let {
                    it.light.un_duration = 10
                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                    mBinding?.tvLightDurationContent?.text = "10s"
                }
            }
            R.id.radio_30->{
                mViewModel.mWarnSettingParam.value?.let {
                    it.light.un_duration = 30
                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                    mBinding?.tvLightDurationContent?.text = "30s"
                }
            }
            R.id.radio_60->{
                mViewModel.mWarnSettingParam.value?.let {
                    it.light.un_duration = 60
                    val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.WarnSetting,
                        it
                    )
                    mViewModel.setSwitchParam(devParamArray,deviceId)
                    mBinding?.tvLightDurationContent?.text = "60s"
                }
            }
        }

    }
}