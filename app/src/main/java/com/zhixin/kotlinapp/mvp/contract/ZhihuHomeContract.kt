package com.zhixin.kotlinapp.mvp.contract

import com.zhixin.ywymvp.base.IBaseView
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuHomeListBean

/**
 * Created by shine on 2018/3/27.
 */
interface ZhihuHomeContract {
    interface View : IBaseView {

        /**
         * 设置第一次请求的数据
         */
        fun setHomeData(data: ZhihuHomeListBean)

        /**
         * 设置加载更多的数据
         */
        fun setMoreData(data: ZhihuHomeListBean)

        /**
         * 显示错误信息
         */
        fun showError(msg: String,errorCode:Int)

    }

    interface Presenter {

        /**
         * 获取首页精选数据
         */
        fun requestHomeData()

        /**
         * 加载更多数据
         */
        fun loadMoreData(date: String)


    }
}