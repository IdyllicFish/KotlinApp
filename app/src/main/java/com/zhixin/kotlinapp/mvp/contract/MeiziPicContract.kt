package com.zhixin.kotlinapp.mvp.contract

import com.zhixin.kotlinapp.mvp.model.bean.MeiziPicBean
import com.zhixin.ywymvp.base.IBaseView

interface MeiziPicContract {
    interface View : IBaseView {

        /**
         * 设置图片数据
         */
        fun setPicData(data: MeiziPicBean)


        /**
         * 显示错误信息
         */
        fun showError(msg: String, errorCode: Int)

    }

    interface Presenter {

        /**
         * 获取短评论数据
         */
        fun requestPicData(classify: String, page: Int)

    }
}