package com.gocam.goscamdemopro.baby

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.databinding.ActivityTimeAlbumBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.utils.DateUtils
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gocam.goscamdemopro.utils.FileUtils
import com.gocam.goscamdemopro.utils.Packet
import com.gocam.goscamdemopro.utils.dbg
import com.gos.platform.api.contact.ResultCode
import com.gos.platform.device.inter.IVideoPlay
import com.gos.platform.device.inter.OnDevEventCallback
import com.gos.platform.device.result.DevResult
import com.gos.platform.device.result.DevResult.DevCmd
import com.gos.platform.device.result.GetTimeAlbumResult
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *
 * @Author wuzb
 * @Date 2025/09/02 16:44
 */
class TimeAlbumActivity : BaseBindActivity<ActivityTimeAlbumBinding>(), OnDevEventCallback {
    private lateinit var mDevice: Device

    private val mAdapter: AlbumAdapter by lazy { AlbumAdapter() }

    private var picViewPath: String? = null

    private val mHandler = Handler(Looper.getMainLooper())

    override fun getLayoutId(): Int = R.layout.activity_time_album

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev") ?: return
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        if (mDevice == null)
            finish()

        mBinding?.apply {
            val manager = GridLayoutManager(this@TimeAlbumActivity, 3)
            rvAlbum.layoutManager = manager
            rvAlbum.adapter = mAdapter
        }
        mDevice.connection.addOnEventCallbackListener(this)
        openRecJpeg()
        mDevice.connection.getAlbumForYearSyn(0)
    }

    /**
     * 打开缩略图通道
     */
    private fun openRecJpeg() {
        val timestamp =
            (System.currentTimeMillis() / 1000L).toInt() // IPC 为unsigned int, so +24, timezone > 0;
        val timezone = mDevice.getPTimeZone()
        mDevice.connection
            .openRecJpeg(0, mDevice.streamPsw, timestamp, timezone, thumbVideoPlay)
    }

    /**
     * 下载图片
     */
    private fun getAiJpeg(startTime: Long, endTime: Long) {
        dbg.D("TimeAlbumActivity", "开始下载")
        picViewPath = FileUtils.getTimeAlbum(
            GApplication.app.user.userName!!,
            mDevice.devId,
            "Pic",
            startTime.toString()
        ).absolutePath
        val buf = intArrayOf(startTime.toInt(), endTime.toInt())
        mDevice.getConnection().getAiJpeg(0, buf, buf.size, 4)
    }

    /**
     * 关闭缩略图通道
     */
    private fun closeRecJpeg() {
        if (mDevice.getConnection() != null) {
            mDevice.getConnection().closeRecJpeg(0, thumbVideoPlay)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closeRecJpeg()
    }

    override fun onDevEvent(devId: String?, devResult: DevResult) {
        if (!TextUtils.equals(devId, mDevice.devId)) {
            return
        }

        val devCmd: DevCmd = devResult.devCmd
        val code: Int = devResult.getResponseCode()
        when (devCmd) {
            DevCmd.getTimeAlbum -> if (ResultCode.SUCCESS == code) {
                // 月文件信息
                val albumResult = devResult as GetTimeAlbumResult
                val forMonthList = albumResult.mothFile
                if (forMonthList.isNotEmpty()) {
                    for (file in forMonthList) {
                        if (file.fileNum > 0) {
                            // 获取到有图片的日期 这里只拿一天 可根据自己需要获取
                            val mStartTime: Long = DateUtils.stringToTime(
                                DateUtils.Pattern_yyyyMMdd,
                                file.monthTime
                            ) / 1000
                            val mEndTime = mStartTime + 24 * 3600
                            getAiJpeg(mStartTime, mEndTime)
                            return
                        }
                    }
                }
            }
        }
    }

    private val thumbVideoPlay = IVideoPlay { devId, avFrame ->
        val nFrameNo: Int = Packet.byteArrayToInt_Little(avFrame.data, 0) //当前缩略图的索引
        val nFrameType: Int = Packet.byteArrayToInt_Little(avFrame.data, 4) //对应这个帧类型102
        val nTimestamp: Int = Packet.byteArrayToInt_Little(avFrame.data, 16) //对应视频时间戳
        val nReserved: Int = Packet.byteArrayToInt_Little(avFrame.data, 24) //缩略图总数量
        val nDataSize: Int = Packet.byteArrayToInt_Little(avFrame.data, 28) //数据长度
        dbg.D(
            "onVideoStream", ("nFrameNo=" + nFrameNo
                    + ",nFrameType=" + nFrameType
                    + ",nTimestamp=" + nTimestamp
                    + ",nReserved=" + nReserved
                    + ",nDataSize=" + nDataSize)
        )
        if (nFrameType != 103) {
            // 103对应精彩瞬间抓拍图片
            return@IVideoPlay
        }

        if ((nReserved == 0)) {
            // 图片下载完成
            mHandler.post { endDownload() }
            return@IVideoPlay
        }

        val filePath = picViewPath + File.separator + nTimestamp + ".jpg"
        val file = File(filePath)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            fos.write(avFrame.data, 32, nDataSize)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun endDownload() {
        dbg.D("TimeAlbumActivity", "下载完成")
        val file = File(picViewPath)
        mAdapter.setList(file)
    }

    private class AlbumAdapter : RecyclerView.Adapter<AlbumViewHolder>() {
        val pathList: ArrayList<String> by lazy { ArrayList<String>() }

        fun setList(file: File) {
            pathList.clear()
            val files = file.listFiles()
            if (files != null && files.size > 0) {
                for (pathFile in files) {
                    pathList.add(pathFile.absolutePath)
                }
                dbg.D("TimeAlbumActivity", "下载图片数量 = " + pathList.size)
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): AlbumViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_time_album, parent, false)
            return AlbumViewHolder(view)
        }

        override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
            val imgPath = pathList[position]
            dbg.D("TimeAlbumActivity", "图片路径 = $imgPath")
            Glide.with(holder.itemView.context)
                .load(imgPath)
                .error(R.mipmap.ic_fg_device_item)
                .into(holder.mImg)
        }

        override fun getItemCount(): Int {
            return pathList.size
        }
    }

    private class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImg: ImageView = itemView.findViewById(R.id.img_picture)
    }
}