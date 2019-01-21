package com.zhixin.ywymvp.base

import android.content.Context

/**
 * Created by shine on 2018/5/2.
 */
abstract class BaseMvpFragment<V : IBaseView, out P : BasePresenter<V>> : BaseFragment() {
    val mPresenter by lazy { initPresenter() }

    protected abstract fun initPresenter(): P

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mPresenter.attachView(this as V)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}