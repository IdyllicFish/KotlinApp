package com.zhixin.kotlinapp.ui.activity

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.zhixin.kotlinapp.R
import com.zhixin.kotlinapp.data.MainBotShowStatus
import com.zhixin.ywymvp.base.BaseActivity
import com.zhixin.kotlinapp.mvp.model.bean.TabEntity
import com.zhixin.kotlinapp.ui.fragment.MeiziFragment
import com.zhixin.kotlinapp.ui.fragment.OpenEyeFragment
import com.zhixin.kotlinapp.ui.fragment.ZhihuFragment
import com.zhixin.ywymvp.utils.log
import com.zhixin.ywymvp.utils.showToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_zhihu_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

class MainActivity : BaseActivity() {
    override fun setToolbarTitle(): String = ""

    private val mTitles by lazy { resources.getStringArray(R.array.main_tab_values) }

    // 未被选中的图标
    private val mIconUnSelectIds by lazy { intArrayOf(R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher) }
    // 被选中的图标
    private val mIconSelectIds by lazy { intArrayOf(R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher) }

    private val mTabEntities = ArrayList<CustomTabEntity>()
    private var mZhihuFragment: ZhihuFragment? = null
    private var mOpenEyeFragment: OpenEyeFragment? = null
    private var mMeiziFragment: MeiziFragment? = null

    private var mIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            mIndex = savedInstanceState.getInt("currTabIndex")
        }
        initTab()
        tab_layout.currentTab = mIndex
        switchFragment(mIndex)
    }

    override fun initLayout(): Int = R.layout.activity_main

    override fun initView() {
        EventBus.getDefault().register(this)
    }

    override fun initData() {

    }

    //初始化底部菜单
    private fun initTab() {
        (0 until mTitles.size)
                .mapTo(mTabEntities) { TabEntity(mTitles[it], mIconSelectIds[it], mIconUnSelectIds[it]) }
        //为Tab赋值
        tab_layout.setTabData(mTabEntities)
        tab_layout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                //切换Fragment
                switchFragment(position)
            }

            override fun onTabReselect(position: Int) {

            }
        })
    }

    /**
     * 切换Fragment
     * @param position 下标
     */
    private fun switchFragment(position: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        when (position) {
            0 // 知乎
            -> mZhihuFragment?.let {
                transaction.show(it)
            } ?: ZhihuFragment.getInstance(mTitles[position]).let {
                mZhihuFragment = it
                transaction.add(R.id.fl_container, it, "zhihu")
            }
            1  //开眼
            -> mOpenEyeFragment?.let {
                transaction.show(it)
            } ?: OpenEyeFragment.getInstance(mTitles[position]).let {
                mOpenEyeFragment = it
                transaction.add(R.id.fl_container, it, "open")
            }
            2  //妹子
            -> mMeiziFragment?.let {
                transaction.show(it)
            } ?: MeiziFragment.getInstance(mTitles[position]).let {
                mMeiziFragment = it
                transaction.add(R.id.fl_container, it, "meizi")
            }
            else -> {

            }
        }

        mIndex = position
        tab_layout.currentTab = mIndex
        transaction.commitAllowingStateLoss()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //记录fragment的位置,防止崩溃 activity被系统回收时，fragment错乱
        if (tab_layout != null) {
            outState.putInt("currTabIndex", mIndex)
        }
    }

    /**
     * 隐藏所有的Fragment
     * @param transaction transaction
     */
    private fun hideFragments(transaction: FragmentTransaction) {
        mZhihuFragment?.let { transaction.hide(it) }
        mOpenEyeFragment?.let { transaction.hide(it) }
        mMeiziFragment?.let { transaction.hide(it) }
    }

    private var mExitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis().minus(mExitTime) <= 2000) {
                finish()
            } else {
                mExitTime = System.currentTimeMillis()
                showToast(this,"再按一次退出程序")
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun setNoStatusBar(): Boolean = true

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setBotShowStatus(showStatus: MainBotShowStatus) {
        if (showStatus.isShow) {
//            main_bot_rl.animate().translationY(0f)
        } else {
//            main_bot_rl.animate().translationY(main_bot_rl.height.toFloat())
        }
    }

}
