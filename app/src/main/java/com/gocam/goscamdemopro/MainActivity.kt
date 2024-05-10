package com.gocam.goscamdemopro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gocam.goscamdemopro.add.WifiSelectActivity
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.cloud.CloudDayActivity
import com.gocam.goscamdemopro.databinding.ActivityMainBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.ipcset.IPCSetActivity
import com.gocam.goscamdemopro.n12.N12SetActivity
import com.gocam.goscamdemopro.net.RetrofitClient
import com.gocam.goscamdemopro.play.PlayActivity
import com.gocam.goscamdemopro.play.PlayEchoActivity
import com.gocam.goscamdemopro.play.PlayJavaActivity
import com.gocam.goscamdemopro.play.ipc.IpcPlayEchoActivity
import com.gocam.goscamdemopro.set.SettingActivity
import com.gocam.goscamdemopro.tf.TfDayActivity
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gocam.goscamdemopro.vphoto.UploadFileActivity
import com.gos.platform.api.contact.DeviceType
import com.gos.platform.api.domain.DeviceStatus
import kotlinx.coroutines.NonCancellable.start

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(),
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var recycleDev: RecyclerView
    val deviceList: ArrayList<Device> = arrayListOf()
    private var devAdapter: DeviceAdapter? = null
    override fun getLayoutId(): Int = R.layout.activity_main

    override fun onCreateData(bundle: Bundle?) {
        recycleDev = mBinding!!.rvDevList
        devAdapter = DeviceAdapter()
        recycleDev.layoutManager = LinearLayoutManager(this)
        recycleDev.adapter = devAdapter
        mViewModel.apply {
            mDeviceList.observe(this@MainActivity) {
                mBinding?.swipeRefresh?.isRefreshing = false
                Log.e(TAG, "onCreateData: $it")
                if (it != null) {
                    devAdapter?.setDevices(it as ArrayList<Device>)
                }
            }

        }

        mBinding?.apply {
            toolBar.backImg.visibility=View.INVISIBLE
            toolBar.rightImg.apply {
                visibility = View.VISIBLE
                setImageResource(R.mipmap.icon_add)
                setOnClickListener {
                    val intent = Intent(this@MainActivity, WifiSelectActivity::class.java)
                    this@MainActivity.startActivity(intent)
                }
            }
        }


        mBinding?.swipeRefresh?.setOnRefreshListener(this)

        mViewModel?.getDeviceList()
    }


    override fun onResume() {
        super.onResume()

    }


    internal class DeviceAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

        private val deviceList = arrayListOf<Device>()

        public fun setDevices(devices: ArrayList<Device>) {
            deviceList.clear()
            deviceList.addAll(devices)
            for(device in devices){
                Log.e("TAG", "setDevices: ${device.getVerifyTimezone()}", )
            }
            notifyDataSetChanged()
        }

        @NonNull
        override fun onCreateViewHolder(
            @NonNull viewGroup: ViewGroup,
            type: Int
        ): RecyclerView.ViewHolder {
            return if (type == 2) {
                val view: View = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.item_device, viewGroup, false)
                Vh(view)
            } else {
                val view: View = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.item_device_empty, viewGroup, false)
                EmptyVh(view)
            }
        }

        override fun onBindViewHolder(@NonNull vh: RecyclerView.ViewHolder, i: Int) {
            if (vh is Vh) {
                val device: Device = deviceList.get(i)
                var isOnline = false
                if (device.devType == DeviceType.V_PHOTO) {
                    isOnline = DeviceManager.getInstance().isOnline(device.devId)
                }
                ("DevName:" + device.devName
                    .toString() + "\nDevId:" + device.devId.toString() +
                        "\nDevType:" + device.devType.toString() +
                        "\nStatus:" + if (device.deviceStatus != DeviceStatus.OFFLINE || isOnline) "Online" else "Offline").also {
                    (vh as Vh).tv.text = it
                }
                vh.itemView.setOnClickListener(View.OnClickListener {
                    when(device.devType){
                        DeviceType.DOOR_BELL->{
                            PlayEchoActivity.startActivity(
                                vh.itemView.context,
                                device.devId
                            )
                        }
                        DeviceType.IPC,DeviceType.GLO_NIGHT->{

                            N12SetActivity.startActivity(
                                vh.itemView.context,
                                device.devId
                            )
                        }
                        DeviceType.V_PHOTO -> {
                            UploadFileActivity.startActivity(
                                vh.itemView.context,
                                device.devId
                            )
                        }

                        else->{
                            PlayEchoActivity.startActivity(
                                vh.itemView.context,
                                device.devId
                            )
                        }
                    }

                })
                (vh as Vh).ibtnTf.setOnClickListener {
                    TfDayActivity.startActivity(
                        vh.itemView.context,
                        device.devId
                    )
                }
                (vh as Vh).ibtnCloud.setOnClickListener {
                    CloudDayActivity.startActivity(
                        vh.itemView.context,
                        device.devId
                    )
                }
                (vh as Vh).ibtnSetting.setOnClickListener {
                    when(device.devType){
                        DeviceType.DOOR_BELL->{
                            val intent = Intent(vh.itemView.context, SettingActivity::class.java)
                            intent.putExtra("dev", device.devId)
                            vh.itemView.context.startActivity(intent)
                        }
                        DeviceType.IPC,DeviceType.GLO_NIGHT->{
                            val intent = Intent(vh.itemView.context, SettingActivity::class.java)
                            intent.putExtra("dev", device.devId)
                            vh.itemView.context.startActivity(intent)
//                            val intent = Intent(vh.itemView.context, IPCSetActivity::class.java)
//                            intent.putExtra("dev", device.devId)
//                            vh.itemView.context.startActivity(intent)
                        }
                        else -> {
                            val intent = Intent(vh.itemView.context, SettingActivity::class.java)
                            intent.putExtra("dev", device.devId)
                            vh.itemView.context.startActivity(intent)
                        }
                    }
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (deviceList == null || deviceList.size == 0) 1 else 2
        }

        override fun getItemCount(): Int {
            return if (deviceList == null || deviceList.size == 0) 1 else deviceList.size
        }

        internal inner class Vh(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tv: TextView
            var ibtnTf: ImageButton
            var ibtnCloud: ImageButton
            var ibtnSetting: ImageButton

            init {
                tv = itemView.findViewById(R.id.tv)
                ibtnTf = itemView.findViewById(R.id.ibtn_tf)
                ibtnCloud = itemView.findViewById(R.id.ibtn_cloud)
                ibtnSetting = itemView.findViewById(R.id.ibtn_setting)
            }
        }

        internal inner class EmptyVh(@NonNull itemView: View?) :
            RecyclerView.ViewHolder(itemView!!)
    }

    override fun onRefresh() {
        mViewModel?.getDeviceList()
    }

}