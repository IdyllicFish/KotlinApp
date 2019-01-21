package com.zhixin.ywymvp.base

import android.os.Bundle

/**
 * Created by shine on 2018/4/28.
 */
abstract class BaseMvpActivity<V : IBaseView, out P : BasePresenter<V>>: BaseActivity() {
    val mPresenter by lazy { initPresenter() }

    protected abstract fun initPresenter(): P

    override fun onCreate(savedInstanceState: Bundle?) {
        mPresenter.attachView(this as V)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

}