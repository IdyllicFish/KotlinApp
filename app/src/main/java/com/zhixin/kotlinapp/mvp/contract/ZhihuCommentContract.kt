package com.zhixin.kotlinapp.mvp.contract

import com.zhixin.kotlinapp.mvp.model.bean.ZhihuInfoCommentBean
import com.zhixin.ywymvp.base.IBaseView

interface ZhihuCommentContract {
    interface View : IBaseView {

        /**
         * 设置短评论数据
         */
        fun setShortData(data: ZhihuInfoCommentBean)

        /**
         * 设置长评论数据
         */
        fun setLongData(data: ZhihuInfoCommentBean)


        /**
         * 显示错误信息
         */
        fun showError(msg: String,errorCode:Int)

    }

    interface Presenter {

        /**
         * 获取短评论数据
         */
        fun requestShortData(id: Int)

        /**
         * 获取长评论数据
         */
        fun requestLongData(id: Int)

    }
}