package com.gocam.goscamdemopro.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityDevShareBinding
import com.gocam.goscamdemopro.dialog.LoadingDialog.showLoading
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.utils.DeviceManager

class DevShareActivity : BaseActivity<ActivityDevShareBinding, DevShareViewModel>() {
    private lateinit var deviceId: String
    private lateinit var mDevice: Device
    private var shareAdapter: ShareAdapter? = null
    override fun getLayoutId(): Int = R.layout.activity_dev_share

    override fun onCreateData(bundle: Bundle?) {
        deviceId = intent.getStringExtra("dev").toString()
        mDevice = DeviceManager.getInstance().findDeviceById(deviceId)
        if (mDevice == null)
            finish()
        mViewModel?.getShareList(deviceId)

        shareAdapter = ShareAdapter()

        shareAdapter!!.setUnSharedListener(object : ShareAdapter.OnUnSharedListener {
            override fun unShared(userName: String) {
                mViewModel.unSharedDevice(userName, deviceId)
            }
        })
        mBinding?.apply {
            toolBar.backImg.setOnClickListener {
                finish()
            }

            recycleView.apply {
                layoutManager = LinearLayoutManager(this@DevShareActivity)
                adapter = shareAdapter
            }

            btnShare.setOnClickListener {
                val userName = etUserName.text.toString()
                mViewModel.shareDeviceToPeople(userName, mDevice)
            }
        }

        mViewModel?.apply {
            mShareUserList.observe(this@DevShareActivity) {
                shareAdapter!!.setData(it as ArrayList<String>)
            }

            mShareResult.observe(this@DevShareActivity) {
                showLToast("success")
            }

            mUnShareResult.observe(this@DevShareActivity) {
                showLToast("success")
            }
        }
    }


    internal class ShareAdapter : RecyclerView.Adapter<ShareAdapter.Vh?>() {

        val mShareList = arrayListOf<String>()
        var unSharedListener: OnUnSharedListener? = null

        @JvmName("setUnSharedListener1")
        fun setUnSharedListener(sharedListener: OnUnSharedListener) {
            unSharedListener = sharedListener
        }

        fun setData(list: ArrayList<String>) {
            mShareList.clear()
            mShareList.addAll(list)
            notifyDataSetChanged()
        }

        @NonNull
        override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, type: Int): Vh {
            val view: View = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_share, viewGroup, false)
            return Vh(view)
        }

        override fun onBindViewHolder(@NonNull vh: Vh, i: Int) {
            val s: String = mShareList.get(i)
            vh.tv.text = s
            vh.ivDelete.setOnClickListener {
                unSharedListener?.unShared(s)
            }
        }

        internal inner class Vh(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tv: TextView
            var ivDelete: ImageView

            init {
                tv = itemView.findViewById(R.id.tv)
                ivDelete = itemView.findViewById(R.id.iv_delete)
            }
        }

        override fun getItemCount(): Int {
            return mShareList.size
        }


        interface OnUnSharedListener {
            fun unShared(userName: String)
        }
    }

}