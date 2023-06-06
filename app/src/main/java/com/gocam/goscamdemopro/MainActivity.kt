package com.gocam.goscamdemopro

import android.os.Bundle
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
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityMainBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.net.RetrofitClient
import com.gocam.goscamdemopro.play.PlayActivity
import com.gocam.goscamdemopro.set.SettingActivity
import kotlinx.coroutines.NonCancellable.start

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var recycleDev: RecyclerView
     val deviceList: ArrayList<Device> = arrayListOf()
    private var devAdapter:DeviceAdapter?=null
    override fun getLayoutId(): Int = R.layout.activity_main

    override fun onCreateData(bundle: Bundle?) {
        recycleDev = mBinding!!.rvDevList
        devAdapter = DeviceAdapter()
        recycleDev.layoutManager = LinearLayoutManager(this)
        recycleDev.adapter = devAdapter
        mViewModel.apply {
            mDeviceList.observe(this@MainActivity){
                if (it != null){
                    devAdapter?.setDevices(it as ArrayList<Device>)
                }
            }

        }
    }


    override fun onResume() {
        super.onResume()
        mViewModel?.getDeviceList()
    }


    internal class DeviceAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

        val deviceList = arrayListOf<Device>()

        public fun setDevices(devices:ArrayList<Device>){
            deviceList.clear()
            deviceList.addAll(devices)
            notifyDataSetChanged()
        }

        @NonNull
        override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, type: Int): RecyclerView.ViewHolder {
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
                (vh as Vh).tv.text = "DevName:" + device.devName
                    .toString() + "\nDevId:" + device.devId.toString() +
                        "\nDevType:" + device.devType.toString() +
                        "\nStatus:" + if (device.isOnline) "Online" else "Offline"
                vh.itemView.setOnClickListener(View.OnClickListener {
//                    PlayActivity.startActivity(
//                        this@Main,
//                        device.devId
//                    )
                })
                (vh as Vh).ibtnTf.setOnClickListener {
//                    TfDayActivity.startActivity(
//                        this@MainActivity,
//                        device.devId
//                    )
                }
                (vh as Vh).ibtnCloud.setOnClickListener {
//                    CloudDayActivity.startActivity(
//                        this@MainActivity,
//                        device.devId
//                    )
                }
                (vh as Vh).ibtnSetting.setOnClickListener {
//                    SettingActivity.startActivity(
//                        this@MainActivity,
//                        device.devId
//                    )
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

}