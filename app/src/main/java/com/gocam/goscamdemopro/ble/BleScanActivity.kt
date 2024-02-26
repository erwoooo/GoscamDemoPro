package com.gocam.goscamdemopro.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattService
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clj.fastble.BleManager
import com.clj.fastble.callback.*
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.utils.HexUtil
import com.clj.fastble.utils.HexUtil.formatHexString
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.add.CheckBindStatusActivity
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.databinding.ActivityBleScanLayoutBinding
import com.gocam.goscamdemopro.utils.RegexUtils
import com.gos.platform.api.ConfigManager
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author cw
 * @Date 2023/12/28 11:44
 * 蓝牙扫描界面
 */
class BleScanActivity: BaseActivity<ActivityBleScanLayoutBinding,BleConViewModel>() {
    var ssid:String?=null
    var psw:String?=null
    var bindToken:String?=null
    var mBleDevAdapter:BleDevAdapter?=null
    val UUID_SERVICE_N11:String = "0000fee0-0000-1000-8000-00805f9b34fb"
    val UUID_WRITE_N11:String = "0000fee3-0000-1000-8000-00805f9b34fb"
    val UUID_NOTIFY_N11:String = "0000fee4-0000-1000-8000-00805f9b34fb"

    val UUID_SERVICE_N12:String = "0000ffff-0000-1000-8000-00805f9b34fb"
    val UUID_WRITE_N12:String = "0000ff01-0000-1000-8000-00805f9b34fb"
    val UUID_NOTIFY_N12:String = "0000ff03-0000-1000-8000-00805f9b34fb"


    var UUID_SERVICE:String = UUID_SERVICE_N12
    var UUID_WRITE:String = UUID_WRITE_N12
    var UUID_NOTIFY:String = UUID_NOTIFY_N12


    val N11 = "BK3288_DEMO_BLE"
    val N12 = "N12XR12ONE"

    val TAG = "BleScanActivity"
    override fun getLayoutId(): Int {
        return R.layout.activity_ble_scan_layout
    }

    override fun onCreateData(bundle: Bundle?) {
        ssid = intent.getStringExtra("ssid") as String
        psw = intent.getStringExtra("psw") as String

        BleManager.getInstance().init(GApplication.app)
        BleManager.getInstance()
            .enableLog(true)
            .setReConnectCount(1,5000)
            .setConnectOverTime(20000).operateTimeout = 5000


        mViewModel.getBleDevToken()

        mViewModel.apply {
            mTokenResult.observe(this@BleScanActivity){
                bindToken = it
                Log.e(TAG, "onCreateData: 绑定的token = $bindToken")
            }
        }


        mBleDevAdapter = BleDevAdapter()
        mBleDevAdapter?.setBleDevCon(object : BleDevConListener{
            override fun onBleCon(dev: BleDevice) {
                Log.e(TAG, "onBleCon: 点击设备", )
                BleManager.getInstance().cancelScan()
                if (dev.name.toString().contains(N11)){
                    UUID_SERVICE = UUID_SERVICE_N11
                    UUID_WRITE = UUID_WRITE_N11
                    UUID_NOTIFY = UUID_NOTIFY_N11
                }else{
                    UUID_SERVICE = UUID_SERVICE_N12
                    UUID_WRITE = UUID_WRITE_N12
                    UUID_NOTIFY = UUID_NOTIFY_N12
                }
                connectBleDev(dev)
            }

        })
        mBinding?.apply {
            btnScanBle.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startScanBle()
                }
            }

            btnDiscBle.setOnClickListener {
                BleManager.getInstance().disconnectAllDevice()
            }
            rvBleList.apply {
                layoutManager = LinearLayoutManager(this@BleScanActivity)
                adapter = mBleDevAdapter
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun startScanBle(){
        //检查权限
        val bluetoothAdapter:BluetoothAdapter =BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.isEnabled){
            Toast.makeText(this, "请先打开蓝牙", Toast.LENGTH_LONG).show()
            return
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            val arr = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(arr,0)
            return
        }
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED){
            val arr = arrayOf(Manifest.permission.BLUETOOTH_SCAN)
            requestPermissions(arr,0)
            return
        }
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
            val arr = arrayOf(Manifest.permission.BLUETOOTH_CONNECT)
            requestPermissions(arr,0)
            return
        }


        startScan()

    }




    private fun startScan(){
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanStarted(p0: Boolean) {
                showLoading()
                mBinding?.btnScanBle?.isEnabled = false
            }

            override fun onScanning(p0: BleDevice?) {

            }

            override fun onScanFinished(bleDevs: MutableList<BleDevice>?) {
                mBinding?.btnScanBle?.isEnabled = true
                dismissLoading()
                bleDevs?.let { mBleDevAdapter?.setBleDevs(it) }
            }

        })
    }

    private fun connectBleDev(dev: BleDevice){
        Log.e(TAG, "connectBleDev: 开始连接" )
        BleManager.getInstance().connect(dev,object: BleGattCallback(){
            override fun onStartConnect() {
                Log.e(TAG, "onStartConnect: 开始连接1", )
                showLoading()
            }

            override fun onConnectFail(dev: BleDevice?, p1: BleException?) {
                dismissLoading()
                Log.e(TAG, "onConnectFail: 连接失败 $p1")
                Toast.makeText(this@BleScanActivity,getString(R.string.connect_fail),Toast.LENGTH_SHORT).show()

            }

            override fun onConnectSuccess(dev: BleDevice?, p1: BluetoothGatt?, p2: Int) {
                Log.e(TAG, "onConnectSuccess: 连接成功")
                if (BleManager.getInstance().isConnected(dev)){
                    Toast.makeText(this@BleScanActivity,getString(R.string.connect_success),Toast.LENGTH_SHORT).show()
                    sendDataToBle(dev)
                }else{
                    Toast.makeText(this@BleScanActivity,getString(R.string.connect_fail),Toast.LENGTH_SHORT).show()
                    dismissLoading()
                }
            }

            override fun onDisConnected(isActiveDisConnected: Boolean, dev: BleDevice?, gatt: BluetoothGatt?, status: Int) {
                dismissLoading()
                if (isActiveDisConnected){
                    Toast.makeText(this@BleScanActivity,getString(R.string.active_disconnected),Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@BleScanActivity,getString(R.string.disconnected),Toast.LENGTH_SHORT).show()
                }
            }

        })

    }

    private fun sendDataToBle(dev: BleDevice?) {

        BleManager.getInstance().setMtu(dev,200,object : BleMtuChangedCallback(){
            override fun onSetMTUFailure(p0: BleException?) {
                Log.e(TAG, "onSetMTUFailure: 修改mtu 异常 $p0")
                BleManager.getInstance().disconnect(dev)
            }

            override fun onMtuChanged(p0: Int) {
                Log.e(TAG, "onMtuChanged: 修改mtu成功")

                val bleGatt: BluetoothGatt = BleManager.getInstance().getBluetoothGatt(dev)

                for (service in bleGatt.services){
                    Log.e(TAG, "sendDataToBle: service = ${service.uuid}" )
                    if (service.uuid.toString() == UUID_SERVICE){
                        Log.e(TAG, "sendDataToBle: 开始做service信息")
                        openNotifyService(dev,service)
                    }
                }
            }

        })

    }

    private fun openNotifyService(dev: BleDevice?,service: BluetoothGattService?) {
        Log.e(TAG, "openNotifyService: 发送绑定信息dev ${dev?.mac}" +
                "  service= ${service?.uuid}" )
        val appName = "GOSCOM Pro"
        val sS = ConfigManager.getInstance().curServer
        var server = ""
        if (TextUtils.equals(sS, ConfigManager.S_CN_URL)) {
            server = "CN"
        } else if (TextUtils.equals(sS, ConfigManager.S_EN_URL)) {
            server = "US"
        }
        val qrText = """
            $appName
            $ssid
            $psw
            $bindToken
            $server
            0
            """.trimIndent()
        Log.e("BleScanActivity", "sendDataToBle: sendData= $qrText")
        Thread.sleep(1000)
        service?.let {
            for (character in it.characteristics){
                if (character.uuid.toString() == UUID_NOTIFY){
                    BleManager.getInstance().notify(dev,character.service.uuid.toString(),character.uuid.toString(),object : BleNotifyCallback(){
                        override fun onNotifySuccess() {
                            Log.e("BleScanActivity", "onNotifySuccess: ", )

                            for (cha in it.characteristics){
                                if (cha.uuid.toString() == UUID_WRITE){
                                    BleManager.getInstance().write(dev,cha.service.uuid.toString(),cha.uuid.toString(),qrText.toByteArray(
                                        StandardCharsets.UTF_8),false,object : BleWriteCallback(){
                                        override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray?) {
                                            Log.e("BleScanActivity", "onWriteSuccess: current= $current total= $total txt= ${HexUtil.formatHexString(justWrite)}" )
                                            dismissLoading()
                                            val intent = Intent(this@BleScanActivity, CheckBindStatusActivity::class.java)
                                            intent.putExtra("token",bindToken)
                                            startActivity(intent)
                                            finish()
                                        }

                                        override fun onWriteFailure(p0: BleException?) {
                                            Log.e("BleScanActivity", "onWriteFailure: $p0", )
                                            dismissLoading()
                                            BleManager.getInstance().disconnect(dev)
                                        }

                                    })
                                }
                            }


                        }

                        override fun onNotifyFailure(p0: BleException?) {
                            BleManager.getInstance().disconnect(dev)
                            Log.e("BleScanActivity", "onNotifyFailure: $p0" )
                        }

                        override fun onCharacteristicChanged(p0: ByteArray?) {
                            Log.e("BleScanActivity", "onCharacteristicChanged: ${RegexUtils.bytesToHexString(character.value)}" )

                        }

                    })
                }
            }
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        BleManager.getInstance().disconnectAllDevice()
        BleManager.getInstance().destroy()
    }


    inner class BleViewHolder(view: View): RecyclerView.ViewHolder(view){
        var tvName:TextView
        var btnConnect:Button

        init {
            tvName = view.findViewById(R.id.tv_name)
            btnConnect = view.findViewById(R.id.btn_connect)
        }

    }

    inner class BleDevAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        private val bleList: ArrayList<BleDevice> = arrayListOf()
        var mBleDevConListener: BleDevConListener?=null

        fun setBleDevCon(conListener:BleDevConListener){
            mBleDevConListener = conListener
        }
        fun setBleDevs(list: List<BleDevice>){
            bleList.clear()
            bleList.addAll(list)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view:View = LayoutInflater.from(parent.context).inflate(R.layout.item_ble_dev_layout,parent,false)
            val holder = BleViewHolder(view)
            holder.btnConnect.setOnClickListener {
                Log.e(TAG, "onCreateViewHolder: 点击时间1", )
                mBleDevConListener?.onBleCon(holder.itemView.tag as BleDevice)
            }
            return holder

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val bleHolder = holder as BleViewHolder
            bleHolder.tvName.text = "${bleList[position].name}" +
                    "  ${bleList[position].mac}"
            bleHolder.itemView.tag = bleList[position]
        }

        override fun getItemCount(): Int {
            return bleList.size
        }

    }

    interface BleDevConListener{
        fun onBleCon(dev:BleDevice)
    }
}