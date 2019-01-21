package com.zhixin.kotlinapp.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.zhixin.ywymvp.base.BasePresenter
import com.zhixin.kotlinapp.mvp.contract.ZhihuHomeContract
import com.zhixin.kotlinapp.mvp.model.ZhihuHomeModel

/**
 * Created by shine on 2018/3/27.
 */
class ZhihuHomePresenter : BasePresenter<ZhihuHomeContract.View>(), ZhihuHomeContract.Presenter {



    private val zhihuHomeModel: ZhihuHomeModel by lazy {
        ZhihuHomeModel()
    }

    override fun requestHomeData() {
        // 检测是否绑定 View
        checkViewAttached()
        mRootView?.showLoading()
        addSubscription(disposable = zhihuHomeModel.requestHomeData()
                .subscribe(
                        { issue ->
                            mRootView?.run {
                                dismissLoading()
                                setHomeData(issue)
                            }
                        }, { throwable ->
                    mRootView?.apply {
                        dismissLoading()
                        //处理异常
                        showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                    }
                })
        )
    }

    override fun loadMoreData(date: String) {
        // 检测是否绑定 View
        checkViewAttached()
        addSubscription(disposable = zhihuHomeModel.loadMoreData(date)
                .subscribe(
                        { issue ->
                            mRootView?.run {
                                dismissLoading()
                                setMoreData(issue)
                            }
                        }, { throwable ->
                    mRootView?.apply {
                        //处理异常
                        dismissLoading()
                        showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                    }
                }
                )
        )
    }

}