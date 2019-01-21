package com.zhixin.kotlinapp.ui.activity

import android.content.res.Resources
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.TypedValue
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.zhixin.kotlinapp.R
import com.zhixin.kotlinapp.ui.fragment.ZhihuCommentFragment
import com.zhixin.ywymvp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_zhihu_comment.*
import java.lang.reflect.Field
import java.util.*

class ZhihuCommentActivity : BaseActivity() {

    private val title: String by lazy { intent.getStringExtra("title") }

    override fun initLayout(): Int = R.layout.activity_zhihu_comment

    override fun initView() {
        if (comment_toolbar != null) {
            comment_toolbar.title = setToolbarTitle()
            setSupportActionBar(comment_toolbar)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        val pagerAdapter = MyViewPagerAdapter(supportFragmentManager)
        pagerAdapter.addFragment(ZhihuCommentFragment.getInstance(0), "短评论")
        pagerAdapter.addFragment(ZhihuCommentFragment.getInstance(1), "长评论")
        zhihu_comment_view_page.adapter = pagerAdapter
        zhihu_comment_tab.addTab(zhihu_comment_tab.newTab().setText("短评论"))
        zhihu_comment_tab.addTab(zhihu_comment_tab.newTab().setText("长评论"))
        zhihu_comment_tab.tabMode = TabLayout.MODE_FIXED
        zhihu_comment_tab.setupWithViewPager(zhihu_comment_view_page)
        zhihu_comment_tab.post({ setIndicator(zhihu_comment_tab, 50, 50) })

        var statusBarHeight1 = -1
        //获取status_bar_height资源的ID
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = resources.getDimensionPixelSize(resourceId)
        }
        val lp = RelativeLayout.LayoutParams(zhihu_comment_coord.layoutParams)
        lp.setMargins(0, statusBarHeight1, 0, 0)
        zhihu_comment_coord.layoutParams = lp
    }

    override fun initData() {

    }

    override fun setNoStatusBar(): Boolean = true

    override fun setToolbarTitle(): String = title

    inner class MyViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val mFragments = ArrayList<Fragment>()
        private val fragmentTitles = ArrayList<String>()

        fun addFragment(fragment: Fragment, title: String) {
            mFragments.add(fragment)
            fragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            //这里返回的标题就是TabLayout的标题
            return fragmentTitles[position]
        }
    }

    fun setIndicator(tabs: TabLayout, leftDip: Int, rightDip: Int) {
        val tabLayout = tabs.javaClass
        var tabStrip: Field? = null
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip")
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }

        tabStrip!!.isAccessible = true
        var llTab: LinearLayout? = null
        try {
            llTab = tabStrip.get(tabs) as LinearLayout
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        val left = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip.toFloat(), Resources.getSystem().displayMetrics).toInt()
        val right = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip.toFloat(), Resources.getSystem().displayMetrics).toInt()

        for (i in 0 until llTab!!.childCount) {
            val child = llTab.getChildAt(i)
            child.setPadding(0, 0, 0, 0)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            params.leftMargin = left
            params.rightMargin = right
            child.layoutParams = params
            child.invalidate()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
