package com.zhixin.ywymvp.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.classic.common.MultipleStatusView

/**
 * Created by shine on 2018/3/20.
 */
abstract class BaseFragment : Fragment() {

    private var resetView: View? = null

    /**
     * 视图是否加载完毕
     */
    private var isViewPrepare = false
    /**
     * 数据是否加载过了
     */
    private var hasLoadData = false
    /**
     * 多种状态的 View 的切换
     */
    protected var mLayoutStatusView: MultipleStatusView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (resetView == null) {
            resetView = inflater?.inflate(getLayoutId(), container, false)
        }
        return resetView
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoadDataIfPrepared()
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isViewPrepare){
            isViewPrepare = true
            mLayoutStatusView = setMultipleStatusView()
            initView()
            initListens()
            lazyLoadDataIfPrepared()
            //多种状态切换的view 重试点击事件
            mLayoutStatusView!!.setOnClickListener(mRetryClickListener)
        }

    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }

    open val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        lazyLoad()
    }


    /**
     * 加载布局
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 初始化 ViewI
     */
    abstract fun initView()

    /**
     * 监听事件
     */
    abstract fun initListens()

    /**
     * 懒加载
     */
    abstract fun lazyLoad()

    abstract fun setToolbarTitle(): String

    abstract fun setMultipleStatusView(): MultipleStatusView

    fun getResetView():View{
        return resetView!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (resetView!!.parent as ViewGroup).removeView(resetView)
    }

}