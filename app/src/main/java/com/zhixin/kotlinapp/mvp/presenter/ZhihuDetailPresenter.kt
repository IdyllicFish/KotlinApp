package com.zhixin.kotlinapp.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.zhixin.kotlinapp.mvp.contract.ZhihuDetailContract
import com.zhixin.ywymvp.base.BasePresenter
import com.zhixin.kotlinapp.mvp.contract.ZhihuHomeContract
import com.zhixin.kotlinapp.mvp.model.ZhihuDetailModel
import com.zhixin.kotlinapp.mvp.model.ZhihuHomeModel

/**
 * Created by shine on 2018/3/27.
 */
class ZhihuDetailPresenter : BasePresenter<ZhihuDetailContract.View>(), ZhihuDetailContract.Presenter {

    private val zhihuDetailModel: ZhihuDetailModel by lazy {
        ZhihuDetailModel()
    }

    override fun requestNewsData(id: Int) {
        // 检测是否绑定 View
        checkViewAttached()
        mRootView?.showLoading()
        addSubscription(disposable = zhihuDetailModel.requestNewsData(id)
                .subscribe(
                        { issue ->
                            mRootView?.run {
                                dismissLoading()
                                setNewsData(issue)
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

    override fun requestExtraData(id: Int) {
        // 检测是否绑定 View
        checkViewAttached()
        addSubscription(disposable = zhihuDetailModel.requestExtraData(id)
                .subscribe(
                        { issue ->
                            mRootView?.run {
                                setExtraData(issue)
                            }
                        }, { throwable ->
                    mRootView?.apply {
                        //处理异常
                        showError(ExceptionHandle.handleException(throwable), ExceptionHandle.errorCode)
                    }
                })
        )
    }

}