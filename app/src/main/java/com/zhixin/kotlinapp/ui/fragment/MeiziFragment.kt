package com.zhixin.kotlinapp.ui.fragment

import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.classic.common.MultipleStatusView
import com.zhixin.kotlinapp.R
import com.zhixin.ywymvp.base.BaseFragment
import kotlinx.android.synthetic.main.activity_zhihu_comment.*
import kotlinx.android.synthetic.main.meizi_home_layout.*
import java.lang.reflect.Field
import java.util.ArrayList

/**
 * Created by shine on 2018/3/20.
 */
class MeiziFragment : BaseFragment() {
    override fun setMultipleStatusView(): MultipleStatusView = MultipleStatusView(context)

    override fun setToolbarTitle(): String = resources.getString(R.string.girl)

    private var mTitle: String? = null

    companion object {
        fun getInstance(title: String): MeiziFragment {
            val fragment = MeiziFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    override fun getLayoutId(): Int {
       return R.layout.meizi_home_layout
    }

    override fun initView() {
        if (meizi_home_toolbar != null) {
            meizi_home_toolbar.title = setToolbarTitle()
            (activity as AppCompatActivity).setSupportActionBar(comment_toolbar)
        }
        val pagerAdapter = MyViewPagerAdapter((activity as AppCompatActivity).supportFragmentManager)
        val meiziKeys = resources.getStringArray(R.array.meizi_classify_keys)
        val meiziValues = resources.getStringArray(R.array.meizi_classify_values)

        meiziKeys.forEachIndexed { index, s ->
            pagerAdapter.addFragment(MeiziAllFragment.getInstance(s), meiziValues[index])
        }
        meizi_home_view_page.adapter = pagerAdapter
        meiziValues.forEach {
            meizi_home_tab.addTab(meizi_home_tab.newTab().setText(it))
        }
        meizi_home_tab.tabMode = TabLayout.MODE_SCROLLABLE
        meizi_home_tab.setupWithViewPager(meizi_home_view_page)
        meizi_home_tab.post({ setIndicator(meizi_home_tab, 0, 0) })

        var statusBarHeight1 = -1
        //获取status_bar_height资源的ID
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = resources.getDimensionPixelSize(resourceId)
        }
        val lp = RelativeLayout.LayoutParams(meizi_home_topCoordinatorLayout.layoutParams)
        lp.setMargins(0, statusBarHeight1, 0, 0)
        meizi_home_topCoordinatorLayout.layoutParams = lp
    }

    override fun lazyLoad() {
    }

    override fun initListens() {

    }

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

        override fun getItemPosition(`object`: Any?): Int {
            return super.getItemPosition(`object`)
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
}