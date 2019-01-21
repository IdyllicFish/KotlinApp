package com.zhixin.kotlinapp.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.zhixin.kotlinapp.mvp.contract.OpenEyeHomeContract
import com.zhixin.kotlinapp.mvp.model.OpenEyeHomeModel
import com.zhixin.kotlinapp.mvp.model.bean.OpenEyeHomeBean
import com.zhixin.ywymvp.base.BasePresenter

/**
 * Created by shine on 2018/3/27.
 */
class OpenEyePresenter : BasePresenter<OpenEyeHomeContract.View>(), OpenEyeHomeContract.Presenter {
    private var bannerHomeBean: OpenEyeHomeBean? = null

    private var nextPageUrl: String? = null     //加载首页的Banner 数据+一页数据合并后，nextPageUrl没 add


    private val openEyeHomeModel: OpenEyeHomeModel by lazy {
        OpenEyeHomeModel()
    }

    override fun requestHomeData(num: Int) {
        // 检测是否绑定 View
        checkViewAttached()
        mRootView?.showLoading()
        addSubscription(disposable = openEyeHomeModel.requestFirstData(num)
                .flatMap({

                    //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
                    val bannerItemList = it.issueList[0].itemList

                    bannerItemList.filter { item ->
                        item.type == "banner2" || item.type == "horizontalScrollCard"
                    }.forEach { item ->
                        //移除 item
                        bannerItemList.remove(item)
                    }

                    bannerHomeBean = it //记录第一页是当做 banner 数据


                    //根据 nextPageUrl 请求下一页数据
                    openEyeHomeModel.loadMoreData(it.nextPageUrl)
                })
                .subscribe({
                    mRootView?.apply {
                        dismissLoading()

                        nextPageUrl = it.nextPageUrl
                        //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
                        val newBannerItemList = it.issueList[0].itemList

                        newBannerItemList.filter { item ->
                            item.type == "banner2" || item.type == "horizontalScrollCard"
                        }.forEach { item ->
                            //移除 item
                            newBannerItemList.remove(item)
                        }
                        // 重新赋值 Banner 长度
                        bannerHomeBean!!.issueList[0].count = bannerHomeBean!!.issueList[0].itemList.size

                        //赋值过滤后的数据 + banner 数据
                        bannerHomeBean?.issueList!![0].itemList.addAll(newBannerItemList)

                        setFirstData(bannerHomeBean!!)

                    }

                }, { t ->
                    mRootView?.apply {
                        dismissLoading()
                        showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                    }
                })
        )
    }

    override fun loadMoreData() {
        val disposable = nextPageUrl?.let {
            openEyeHomeModel.loadMoreData(it)
                    .subscribe({
                        mRootView?.apply {
                            //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
                            val newItemList = it.issueList[0].itemList

                            newItemList.filter { item ->
                                item.type == "banner2" || item.type == "horizontalScrollCard"
                            }.forEach { item ->
                                //移除 item
                                newItemList.remove(item)
                            }

                            nextPageUrl = it.nextPageUrl
                            setLoadMoreData(newItemList)
                        }

                    }, { t ->
                        mRootView?.apply {
                            showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                        }
                    })
        }
        if (disposable != null) {
            addSubscription(disposable)
        }
    }

}