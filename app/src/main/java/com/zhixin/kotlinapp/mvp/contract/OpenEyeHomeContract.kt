package com.zhixin.kotlinapp.mvp.contract

import com.zhixin.kotlinapp.mvp.model.bean.OpenEyeHomeBean
import com.zhixin.ywymvp.base.IBaseView

interface OpenEyeHomeContract {
    interface View : IBaseView {

        /**
         * 首页首次加载的数据（Banner）
         */
        fun setFirstData(data: OpenEyeHomeBean)

        /**
         * 加载更多数据
         */
        fun setLoadMoreData(datas:ArrayList<OpenEyeHomeBean.Issue.Item>)

        /**
         * 显示错误信息
         */
        fun showError(msg: String, errorCode: Int)

    }

    interface Presenter {

        /**
         * 获取首页精选数据
         */
        fun requestHomeData(num: Int)

        /**
         * 加载更多数据
         */
        fun loadMoreData()

    }
}