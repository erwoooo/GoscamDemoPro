package com.gocam.goscamdemopro.add

import android.os.Bundle
import androidx.lifecycle.coroutineScope
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityCheckBindStatusBinding
import kotlinx.coroutines.launch

class CheckBindStatusActivity :
    BaseActivity<ActivityCheckBindStatusBinding, CheckBindStatusViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_check_bind_status
    }

    override fun onCreateData(bundle: Bundle?) {
        val token  = intent.getStringExtra("token") as String
        lifecycle.coroutineScope.launch {
            //Obtain once per second
           val result =  RemoteDataSource.queryUserBindResult(GApplication.app.user.userName!!,token)
        }

        mBinding?.toolBar?.backImg?.setOnClickListener {
            finish()
        }
    }
}