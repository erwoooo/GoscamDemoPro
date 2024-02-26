package com.gocam.goscamdemopro.n12

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityN12DemoLayoutBinding
import com.gocam.goscamdemopro.entity.DevParamArray
import com.gocam.goscamdemopro.entity.MirrorModeParam
import com.gocam.goscamdemopro.entity.NightStarParam
import com.gocam.goscamdemopro.entity.NightStarRotateParam
import com.gos.platform.api.devparam.DevParam
import com.gos.platform.device.contact.MirrorMode
import com.gos.platform.device.contact.OnOff

/**
 * @Author cw
 * @Date 2024/1/3 10:54
 */
class N12SetActivity: BaseActivity<ActivityN12DemoLayoutBinding,N12SetViewModel>(),CompoundButton.OnCheckedChangeListener
    ,SeekBar.OnSeekBarChangeListener,RadioGroup.OnCheckedChangeListener {
    lateinit var devId: String
    var colorLight = 0
    var musicIndex = 0
    var musicPlay = 0
    var musicType = 0
    var n12ColorIndex = 0
    var colorIndex = 0
    lateinit var musicItems:ArrayList<Int>
    lateinit var colorItems:ArrayList<ColorItem>
    lateinit var colorN12Items:ArrayList<Int>

    lateinit var mMusicAdapter: MusicAdapter
    lateinit var mColorAdapter: ColorAdapter
    lateinit var mN12ColorAdapter: N12ColorAdapter
    lateinit var mN12SetParam:N12SetParam
    lateinit var un_list:ArrayList<Int>
    lateinit var palylist:ArrayList<Palylist?>
    lateinit var mPalylist:Palylist
    lateinit var mMoodLight:MoodLight
    lateinit var mLight:Light
    lateinit var mAudio:Audio

    private var mLiveSetParam = MutableLiveData<N12SetParam>()

    override fun getLayoutId(): Int {
        return R.layout.activity_n12_demo_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        devId = intent.getStringExtra("dev") as String

        mViewModel.getDevParam(devId)
        musicItems = arrayListOf()
        for (i in 0..14){
            musicItems.add(i)
        }
        colorN12Items = arrayListOf()
        for (i in 1..10){
            colorN12Items.add(i)
        }

        colorItems = arrayListOf()

        colorItems.apply {
            add(ColorItem(0x3CE18D,7))
            add(ColorItem(0x3EE0CE,8))
            add(ColorItem(0x44D6FB,9))
            add(ColorItem(0x2FAEFC,10))
            add(ColorItem(0x1A7FF6,11))
            add(ColorItem(0x0E4CF4,12))

            add(ColorItem(0x140EF7,13))
            add(ColorItem(0x480DED,14))
            add(ColorItem(0x820CEE,15))
            add(ColorItem(0xBF0DE4,16))
            add(ColorItem(0xF40DE6,17))
            add(ColorItem(0xF909B6,18))

            add(ColorItem(0xF90C7F,19))
            add(ColorItem(0xFA0845,20))
            add(ColorItem(0xFC0D18,21))
            add(ColorItem(0xFB420D,22))
            add(ColorItem(0xFB8215,23))
            add(ColorItem(0xFBBD13,24))

            add(ColorItem(0xF5F526,1))
            add(ColorItem(0xBEF714,2))
            add(ColorItem(0x84F612,3))
            add(ColorItem(0x4BF40C,4))
            add(ColorItem(0x36E10F,5))
            add(ColorItem(0x3AE34A,6))
        }

        mMusicAdapter = MusicAdapter()
        mColorAdapter = ColorAdapter()
        mN12ColorAdapter = N12ColorAdapter()
        mMusicAdapter.setOnclick(mOnCommonClickListener)
        mColorAdapter.setOnclick(mOnCommonClickListener)
        mN12ColorAdapter.setOnclick(mOnCommonClickListener)

        initN12param()

        mBinding?.apply {
            rvColor.apply {
                layoutManager = GridLayoutManager(this@N12SetActivity,3)
                adapter = mColorAdapter
            }

            rvMusic.apply {
                layoutManager = GridLayoutManager(this@N12SetActivity,3)
                adapter = mMusicAdapter
            }

            rvColorN12.apply {
                layoutManager = GridLayoutManager(this@N12SetActivity,3)
                adapter = mN12ColorAdapter
            }

            titleBar.textTitle.text = "N12下发指令"

            swStar.setOnCheckedChangeListener(this@N12SetActivity)
            swStarRotate.setOnCheckedChangeListener(this@N12SetActivity)
            seekSideLight.setOnSeekBarChangeListener(this@N12SetActivity)
        }



        mViewModel.apply {

            mNightStarParam.observe(this@N12SetActivity){
                mBinding?.swStar?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.un_switch == OnOff.On
                    setOnCheckedChangeListener(this@N12SetActivity)
                }
            }

            mNightStarRotateParam.observe(this@N12SetActivity){
                mBinding?.swStarRotate?.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = it.un_switch == OnOff.On
                    setOnCheckedChangeListener(this@N12SetActivity)
                }
            }

            mNightStatusParma.observe(this@N12SetActivity){
                mBinding?.apply {
                    //音乐类型
                    musicType = it.music_effect.un_class
                    when(it.music_effect.un_class){
                        0->{
                            //音乐
                            radioMusic.isChecked = true
                            radioWhite.isChecked = false
                        }
                        1->{
                            //白噪音
                            radioMusic.isChecked = false
                            radioWhite.isChecked = true
                        }
                    }
                    //音量

                    seekVolume.apply {
                        progress = it.volume
                        setOnSeekBarChangeListener(this@N12SetActivity)
                    }
                    //当前音乐下标
                    musicIndex = it.music_effect.un_index
                    //是否播放
                    musicPlay = it.music_effect.un_mode
                    //颜色
                    colorLight = it.led_color.un_color
                    //亮度
                    seekLight.apply {
                        setOnSeekBarChangeListener(this@N12SetActivity)
                        progress = it.led_color.un_brightness
                    }
                    //灯效
                    when(it.led_effect){
                        0->{
                            //关灯
                            radioBreath.isChecked = false
                            radioNormal.isChecked = false
                            radioFlash.isChecked = false
                        }
                        1->{
                            radioBreath.isChecked = false
                            radioNormal.isChecked = true
                            radioFlash.isChecked = false
                        }
                        2->{
                            radioBreath.isChecked = true
                            radioNormal.isChecked = false
                            radioFlash.isChecked = false
                        }
                        3->{
                            radioBreath.isChecked = false
                            radioNormal.isChecked = false
                            radioFlash.isChecked = true
                        }

                    }

                    swLightSide.apply {
                        setOnCheckedChangeListener(this@N12SetActivity)
                    }
                    it.mood_light?.let {
                        //氛围灯
                        swLightSide.apply {
                            setOnCheckedChangeListener(null)
                            isChecked = it.un_switch == OnOff.On
                            setOnCheckedChangeListener(this@N12SetActivity)
                        }

                        //氛围灯颜色-》速度
                        n12ColorIndex = it.un_color
                        //氛围灯亮度
                        seekSideLight.progress = it.un_effect
                    }


                    radioGroupEffect.setOnCheckedChangeListener(this@N12SetActivity)
                    radioGroupMusic.setOnCheckedChangeListener(this@N12SetActivity)

                }

                notifyDataSetChanged()
            }

        }

        mLiveSetParam.observe(this){
            mViewModel.setDevParam(devId,it)
        }

    }

    //初始化
    private fun initN12param() {
        un_list = arrayListOf()
        un_list.add(0)
        mPalylist = Palylist(musicType,0,un_list)
        palylist = arrayListOf()
        palylist.add(mPalylist)
        mMoodLight = MoodLight(1,0,0)
        mLight = Light(50,0,0,0,0)
        mAudio = Audio(palylist,0,0,0,50)
        mN12SetParam = N12SetParam(mAudio,mLight,mMoodLight,0,1,8)
    }

    companion object{
        fun startActivity(activity: Context, devId:String){
            val intent  = Intent(activity,N12SetActivity::class.java)
            intent.putExtra("dev",devId)
            activity.startActivity(intent)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView?.id){
            R.id.sw_light_side->{
                mMoodLight.un_switch = if (isChecked){
                    OnOff.On
                }else{
                    OnOff.Off
                }
                mN12SetParam.mood_light = mMoodLight
                mLiveSetParam.postValue(mN12SetParam)
            }

            R.id.sw_star->{
                val sw = if (!isChecked) {
                    OnOff.Off
                } else {
                    OnOff.On
                }
                val nightStarParam = NightStarParam(sw)
                val devParam = DevParamArray(
                    DevParam.DevParamCmdType.NightStarSetting,
                    nightStarParam
                )
                mViewModel.setSwitchParam(devParam, devId = devId)
            }

            R.id.sw_star_rotate->{
                val sw = if (!isChecked) {
                    OnOff.Off
                } else {
                    OnOff.On
                }
                val nightStarRotateParam = NightStarRotateParam(sw)
                val devParam = DevParamArray(
                    DevParam.DevParamCmdType.NightStarRotateSetting,
                    nightStarRotateParam
                )
                mViewModel.setSwitchParam(devParam, devId = devId)
            }
        }


    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {


    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        when(seekBar?.id){
            R.id.seek_volume->{
                mAudio.un_volume = seekBar.progress
                mN12SetParam.audio = mAudio
                mLiveSetParam.postValue(mN12SetParam)
            }
            R.id.seek_light->{
                mLight.un_brightness = seekBar.progress
                mN12SetParam.light = mLight
                mLiveSetParam.postValue(mN12SetParam)
            }

            R.id.seek_side_light->{
                mMoodLight.un_effect = seekBar.progress
                mN12SetParam.mood_light = mMoodLight
                mLiveSetParam.postValue(mN12SetParam)
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

        when(checkedId){
            //音乐
            R.id.radio_music->{
                musicType = 0
                notifyMusic()
            }
            R.id.radio_white->{
                musicType = 1
                notifyMusic()
            }

            //灯效
            R.id.radio_normal->{
                mLight.un_mode = 1
                mN12SetParam.light = mLight
                mLiveSetParam.postValue(mN12SetParam)
            }
            R.id.radio_breath->{
                mLight.un_mode = 2
                mN12SetParam.light = mLight
                mLiveSetParam.postValue(mN12SetParam)
            }
            R.id.radio_flash->{
                mLight.un_mode = 3
                mN12SetParam.light = mLight
                mLiveSetParam.postValue(mN12SetParam)
            }

        }

    }



    data class ColorItem(
        val color:Long,
        val index:Int
    )

    inner class CommonVH(view:View): RecyclerView.ViewHolder(view){
        var ivBg:ImageView
        var tvName:TextView
        var ivContent:ImageView
        init {
            ivBg = view.findViewById(R.id.iv_bg)
            tvName = view.findViewById(R.id.tv_name)
            ivContent = view.findViewById(R.id.iv_content)
        }
    }


    inner class MusicAdapter: RecyclerView.Adapter<CommonVH>(){
        var mOnCommonClickListener:OnCommonClickListener?=null
        fun setOnclick(clickListener: OnCommonClickListener){
            mOnCommonClickListener = clickListener
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonVH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_common_layout,parent,false)
            val holder = CommonVH(view = view)
            holder.itemView.setOnClickListener {
                mOnCommonClickListener?.onMusicClick(it.tag as Int)
            }
            return holder
        }

        override fun onBindViewHolder(holder: CommonVH, position: Int) {
         if (musicType == 0) holder.tvName.text = "music $position" else{
             holder.tvName.text = "white $position"
            }
            if (musicIndex == position) holder.ivBg.visibility = View.VISIBLE else{
                holder.ivBg.visibility = View.GONE
            }
            holder.itemView.tag = position
        }

        override fun getItemCount(): Int {
            return musicItems.size
        }

    }

    inner class N12ColorAdapter: RecyclerView.Adapter<CommonVH>(){
        var mOnCommonClickListener:OnCommonClickListener?=null
        fun setOnclick(clickListener: OnCommonClickListener){
            mOnCommonClickListener = clickListener
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonVH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_common_layout,parent,false)
            val holder = CommonVH(view = view)
            holder.itemView.setOnClickListener {
                mOnCommonClickListener?.onN12ColorClick(it.tag as Int)
            }
            return holder
        }

        override fun onBindViewHolder(holder: CommonVH, position: Int) {
            holder.tvName.text = "色彩变化速度 ${colorN12Items[position]}"
            if (n12ColorIndex == position) holder.ivBg.visibility = View.VISIBLE else{
                holder.ivBg.visibility = View.GONE
            }
            holder.itemView.tag = position
        }

        override fun getItemCount(): Int {
            return colorN12Items.size
        }

    }

    inner class ColorAdapter: RecyclerView.Adapter<CommonVH>(){
        var mOnCommonClickListener:OnCommonClickListener?=null
        fun setOnclick(clickListener: OnCommonClickListener){
            mOnCommonClickListener = clickListener
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonVH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_common_layout,parent,false)
            val holder = CommonVH(view = view)
            holder.itemView.setOnClickListener {
                mOnCommonClickListener?.onColorClick(it.tag as ColorItem,holder.adapterPosition)
            }
            return holder
        }

        override fun onBindViewHolder(holder: CommonVH, position: Int) {
            holder.tvName.text = "颜色 ${colorItems[position].color}"
            if (colorIndex == position) holder.ivBg.visibility = View.VISIBLE else{
                holder.ivBg.visibility = View.GONE
            }
            holder.itemView.tag = colorItems[position]
        }

        override fun getItemCount(): Int {
            return colorItems.size
        }

    }


    interface OnCommonClickListener{
        fun onMusicClick(position:Int)
        fun onN12ColorClick(position: Int)
        fun onColorClick(item:ColorItem,index: Int)
    }

    val mOnCommonClickListener = object : OnCommonClickListener{
        override fun onMusicClick(position: Int) {
            //音乐下标更新
            musicIndex = position
            un_list[0] = position
            mPalylist.un_list = un_list
            mPalylist.un_class = musicType
            palylist[0] = mPalylist
            mAudio.palylist = palylist
            mN12SetParam.audio = mAudio
            mLiveSetParam.postValue(mN12SetParam)
            notifyMusic()
        }

        override fun onN12ColorClick(position: Int) {
            n12ColorIndex = position
            mMoodLight.un_color = colorN12Items[position]
            mN12SetParam.mood_light = mMoodLight
            mLiveSetParam.postValue(mN12SetParam)
            notifyN12()
        }

        override fun onColorClick(item: ColorItem,index: Int) {
            mLight.un_color = item.color.toInt()
            mLight.un_random = item.index
            mN12SetParam.light = mLight
            mLiveSetParam.postValue(mN12SetParam)
            colorIndex = index
            notifyColor()
        }

    }

    fun notifyDataSetChanged(){
        notifyMusic()
        notifyN12()
        notifyColor()
    }

    fun notifyColor(){
        mColorAdapter.notifyDataSetChanged()
    }

    fun notifyMusic(){
        mMusicAdapter.notifyDataSetChanged()
    }

    fun notifyN12(){
        mN12ColorAdapter.notifyDataSetChanged()
    }

}