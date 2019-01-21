package com.zhixin.kotlinapp.mvp.contract

import com.zhixin.kotlinapp.mvp.model.bean.ZhihuDetailBean
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuInfoExtraBean
import com.zhixin.ywymvp.base.IBaseView

interface ZhihuDetailContract {
    interface View : IBaseView {

        /**
         * 设置数据
         */
        fun setNewsData(data: ZhihuDetailBean)

        /**
         * 设置其他数据
         */
        fun setExtraData(data: ZhihuInfoExtraBean)


        /**
         * 显示错误信息
         */
        fun showError(msg: String,errorCode:Int)

    }

    interface Presenter {

        /**
         * 获取富文本数据
         */
        fun requestNewsData(id: Int)

        /**
         * 获取其他数据
         */
        fun requestExtraData(id: Int)

    }
}