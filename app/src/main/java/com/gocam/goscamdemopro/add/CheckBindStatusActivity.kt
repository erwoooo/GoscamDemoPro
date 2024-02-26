package com.gocam.goscamdemopro.add

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.coroutineScope
import com.github.penfeizhou.animation.apng.APNGDrawable
import com.gocam.goscamdemopro.GApplication
import com.gocam.goscamdemopro.MainActivity
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.base.BaseActivity
import com.gocam.goscamdemopro.data.RemoteDataSource
import com.gocam.goscamdemopro.databinding.ActivityCheckBindStatusBinding
import com.gocam.goscamdemopro.entity.BindStatusResult
import com.golway.uilib.bean.BaseResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class CheckBindStatusActivity :
    BaseActivity<ActivityCheckBindStatusBinding, CheckBindStatusViewModel>() {
    private  var  result: BaseResponse<BindStatusResult?>?=null
    private var  bindOver:Boolean = false;
    private val TAG = "CheckBindStatusActivity"
    override fun getLayoutId(): Int {
        return R.layout.activity_check_bind_status
    }

    override fun onCreateData(bundle: Bundle?) {

        val token  = intent.getStringExtra("token") as String
        lifecycle.coroutineScope.launch {
            //Obtain once per second
                do{
                    result = RemoteDataSource.queryUserBindResult(GApplication.app.user.userName!!,token)
                    delay(1000)
                    Log.e(TAG, "onCreateData: out result = $result" )
                    result?.apply {
                        var user: String? = null
                        var deviceId = ""
                        try {
                            if (!TextUtils.isEmpty(this.Body?.ResultDescribe)) {
                                val des: String = this.Body?.ResultDescribe!!
                                val split = des.split("&").toTypedArray()
                                for (i in 0..1) {
                                    val split1 = split[i].split("=").toTypedArray()
                                    if (i == 0) {
                                        //ID of the device to be bound
                                        deviceId = split1[1]
                                    } else if (i == 1) {
                                        //Bound user name
                                        user = split1[1]
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    when(result?.ResultCode){
                        0-> {
                            //Binding successful
                            mBinding?.tv?.text="success"
                            bindOver = true
                            val intent = Intent(this@CheckBindStatusActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        -10000->{
                            //Continue query
                            bindOver = false
                        }
                        10128,-10128->{
                            bindOver = true
                            //The device has been bound. Procedure
                        }
                        -10130->{
                            bindOver = true
                            //token invalidation
                        }
                    }
                }while(!bindOver)






        }

        mBinding?.toolBar?.backImg?.setOnClickListener {
            finish()
        }
    }
}