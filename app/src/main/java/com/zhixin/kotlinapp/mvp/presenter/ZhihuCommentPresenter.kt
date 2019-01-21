package com.zhixin.kotlinapp.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.zhixin.kotlinapp.mvp.contract.ZhihuCommentContract
import com.zhixin.kotlinapp.mvp.contract.ZhihuDetailContract
import com.zhixin.ywymvp.base.BasePresenter
import com.zhixin.kotlinapp.mvp.contract.ZhihuHomeContract
import com.zhixin.kotlinapp.mvp.model.ZhihuCommentModel
import com.zhixin.kotlinapp.mvp.model.ZhihuDetailModel
import com.zhixin.kotlinapp.mvp.model.ZhihuHomeModel

/**
 * Created by shine on 2018/3/27.
 */
class ZhihuCommentPresenter : BasePresenter<ZhihuCommentContract.View>(), ZhihuCommentContract.Presenter {

    private val zhihuCommentModel: ZhihuCommentModel by lazy {
        ZhihuCommentModel()
    }

    override fun requestShortData(id: Int) {
        // 检测是否绑定 View
        checkViewAttached()
        mRootView?.showLoading()
        addSubscription(disposable = zhihuCommentModel.requestShort(id)
                .subscribe(
                        { issue ->
                            mRootView?.run {
                                dismissLoading()
                                setShortData(issue)
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

    override fun requestLongData(id: Int) {
        // 检测是否绑定 View
        checkViewAttached()
        mRootView?.showLoading()
        addSubscription(disposable = zhihuCommentModel.requestLong(id)
                .subscribe(
                        { issue ->
                            mRootView?.run {
                                dismissLoading()
                                setLongData(issue)
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

}