package com.gocam.goscamdemopro.play.ipc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.gocam.goscamdemopro.R
import com.gocam.goscamdemopro.baby.MusicPlayFragment
import com.gocam.goscamdemopro.base.BaseBindActivity
import com.gocam.goscamdemopro.cloud.CloudDayFragment
import com.gocam.goscamdemopro.databinding.ActivityHomeBinding
import com.gocam.goscamdemopro.databinding.ActivityLivePlayBinding
import com.gocam.goscamdemopro.tf.TfDayFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy

/**
 *
 * @Author wuzb
 * @Date 2025/09/15 18:32
 */
class HomeActivity : BaseBindActivity<ActivityHomeBinding>() {
    private lateinit var fragments: Array<Fragment>

    private lateinit var tabLayout: TabLayout

    private lateinit var viewPager: ViewPager2
    private val tabTitles = arrayOf("视频", "音乐", "消息","用户")

    companion object {

        fun startActivity(context: Context, devId: String) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra("dev", devId)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun isImmersive(): Boolean {
        return true // 需要时可返回 false 关闭沉浸式
    }
    override fun onCreateData(bundle: Bundle?) {
        initFragment()
    }
    private fun initFragment() {
        tabLayout = mBinding!!.tabLayout
        viewPager = mBinding!!.fragmentContainerViewpager
        val mLivePlayFragment = LivePlayFragment()
        val mMusicPlayFragment = MusicPlayFragment()
        val mTfDayFragment = TfDayFragment()
        val mCloudDayFragment = CloudDayFragment()
        fragments = arrayOf(mLivePlayFragment, mMusicPlayFragment, mTfDayFragment,mCloudDayFragment)

        viewPager.adapter = object : androidx.viewpager2.adapter.FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }
        // 绑定 TabLayout 与 ViewPager
        TabLayoutMediator(
            tabLayout, viewPager,
            TabConfigurationStrategy { tab: TabLayout.Tab?, position: Int ->
                tab?.setCustomView(
                    getTabView(position)
                )
            }
        ).attach()
        // 默认选中第一个
        setTabSelected(tabLayout.getTabAt(0), true)
        // 监听 Tab 切换
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab!!.position
                viewPager.setCurrentItem(position, false) // false = 无动画
                setTabSelected(tab, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                setTabSelected(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }

    // 自定义 tab view
    private fun getTabView(position: Int): View {
        val view: View = LayoutInflater.from(this).inflate(R.layout.item_tab_layout, null)
        val textView = view.findViewById<TextView?>(R.id.tab_title)
        textView?.text = tabTitles[position]
        return view
    }

    // 切换图标与文字颜色
    private fun setTabSelected(tab: TabLayout.Tab?, isSelected: Boolean) {
        if (tab == null || tab.customView == null) return
        val textView = tab.customView!!.findViewById<TextView?>(R.id.tab_title)

        val position = tab.position
        textView?.isSelected = isSelected
    }
}