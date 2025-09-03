package com.gocam.goscamdemopro.peripheral

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.databinding.ActivityPeripheralBinding
import com.gocam.goscamdemopro.entity.Device
import com.gocam.goscamdemopro.utils.DeviceManager
import com.gocam.goscamdemopro.utils.PeripheralManager

/**
 *
 * @Author wuzb
 * @Date 2025/08/28 10:58
 */
class PeripheralListActivity : BaseActivity<ActivityPeripheralBinding, PeripheralListViewModel>() {
    private lateinit var recycleDev: RecyclerView

    private lateinit var mDevice: Device

    private var mAdapter: PeripheralAdapter? = null

    override fun getLayoutId(): Int = R.layout.activity_peripheral

    override fun onCreateData(bundle: Bundle?) {
        val devId = intent.getStringExtra("dev") ?: return
        mDevice = DeviceManager.getInstance().findDeviceById(devId)
        recycleDev = mBinding!!.rvPeripheral
        mAdapter = PeripheralAdapter {
            // 打开外设页面
            val intent = Intent(this@PeripheralListActivity, ReminderSettingActivity::class.java)
            intent.putExtra("dev", devId)
            intent.putExtra("sub", it.subDevId)
            startActivity(intent)
        }
        recycleDev.layoutManager = LinearLayoutManager(this)
        recycleDev.adapter = mAdapter
        mViewModel.apply {
            mPeripheralParam.observe(this@PeripheralListActivity) {
                PeripheralManager.getInstance().saveDevice(mDevice.devId, it.dev_list)
                mAdapter?.setPeripherals(it.dev_list)
            }
        }

        mBinding?.apply {
            btnAdd.setOnClickListener {
                val intent = Intent(this@PeripheralListActivity, AddPeripheralDeviceActivity::class.java)
                intent.putExtra("dev", devId)
                startActivity(intent)
            }
        }
        devId.let {
            mViewModel.getPeripheralParam(it)
        }
    }
}