package com.gocam.goscamdemopro.play.ipc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.baby.MusicPlayFragment
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.cloud.CloudDayFragment
import com.gocam.goscamdemopro.databinding.ActivityLivePlayBinding
import com.gocam.goscamdemopro.tf.TfDayFragment

/**
 *
 * @Author wuzb
 * @Date 2025/09/12 17:55
 */
class LivePlayActivity : BaseBindActivity<ActivityLivePlayBinding>() {
    private val mFragmentList = arrayListOf<Fragment>()

    private var indexFragment = 0

    override fun getLayoutId(): Int = R.layout.activity_live_play

    companion object {

        fun startActivity(context: Context, devId: String) {
            val intent = Intent(context, LivePlayActivity::class.java)
            intent.putExtra("dev", devId)
            context.startActivity(intent)
        }
    }

    override fun onCreateData(bundle: Bundle?) {
        if (bundle == null) {
            val mLivePlayFragment = LivePlayFragment()
            val mMusicPlayFragment = MusicPlayFragment()
            val mTfDayFragment = TfDayFragment()
            val mCloudDayFragment = CloudDayFragment()
            mFragmentList.add(mLivePlayFragment)
            mFragmentList.add(mMusicPlayFragment)
            mFragmentList.add(mTfDayFragment)
            mFragmentList.add(mCloudDayFragment)
        } else {
            val fm = supportFragmentManager
            val play = fm.findFragmentByTag(LivePlayFragment::class.simpleName)
            val music = fm.findFragmentByTag(MusicPlayFragment::class.simpleName)
            val tf = fm.findFragmentByTag(TfDayFragment::class.simpleName)
            val cloud = fm.findFragmentByTag(CloudDayFragment::class.simpleName)
            mFragmentList.add(play ?: LivePlayFragment())
            mFragmentList.add(music ?: MusicPlayFragment())
            mFragmentList.add(tf ?: TfDayFragment())
            mFragmentList.add(cloud ?: CloudDayFragment())
        }
        mBinding?.apply {
            tvPlay.setOnClickListener { switchFragment(0) }
            tvMusic.setOnClickListener { switchFragment(1) }
            tvTf.setOnClickListener { switchFragment(2) }
            tvCloud.setOnClickListener { switchFragment(3) }
        }
        switchFragment(0)
    }

    private fun switchFragment(checkIndex: Int) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        val currentFragment = mFragmentList[indexFragment]
        indexFragment = checkIndex
        val targetFragment = mFragmentList[checkIndex]
        if (currentFragment != targetFragment) {
            if (!targetFragment.isAdded) {
                ft.hide(currentFragment).add(R.id.fl, targetFragment)
            } else {
                ft.hide(currentFragment).show(targetFragment)
            }
        } else {
            if (!targetFragment.isAdded) {
                ft.add(R.id.fl, targetFragment, targetFragment::class.simpleName).show(targetFragment)
            }
        }
        ft.commit()
    }
}