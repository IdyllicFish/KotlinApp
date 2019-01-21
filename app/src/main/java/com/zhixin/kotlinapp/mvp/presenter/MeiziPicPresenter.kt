package com.zhixin.kotlinapp.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.zhixin.kotlinapp.mvp.contract.MeiziPicContract
import com.zhixin.kotlinapp.mvp.contract.ZhihuCommentContract
import com.zhixin.kotlinapp.mvp.contract.ZhihuDetailContract
import com.zhixin.ywymvp.base.BasePresenter
import com.zhixin.kotlinapp.mvp.contract.ZhihuHomeContract
import com.zhixin.kotlinapp.mvp.model.MeiziPicModel
import com.zhixin.kotlinapp.mvp.model.ZhihuCommentModel
import com.zhixin.kotlinapp.mvp.model.ZhihuDetailModel
import com.zhixin.kotlinapp.mvp.model.ZhihuHomeModel

/**
 * Created by shine on 2018/3/27.
 */
class MeiziPicPresenter : BasePresenter<MeiziPicContract.View>(), MeiziPicContract.Presenter {

    private val meiziPicModel: MeiziPicModel by lazy {
        MeiziPicModel()
    }

    override fun requestPicData(classify: String, page: Int) {
        // 检测是否绑定 View
        checkViewAttached()
        mRootView?.showLoading()
        addSubscription(disposable = meiziPicModel.requestPicData(classify,page)
                .subscribe(
                        { issue ->
                            mRootView?.run {
                                dismissLoading()
                                setPicData(issue)
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