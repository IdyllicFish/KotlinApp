package com.zhixin.kotlinapp.mvp.model

import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuInfoCommentBean
import com.zhixin.ywymvp.net.RetrofitManager
import io.reactivex.Observable

class ZhihuCommentModel {

    /**
     * 获取短评论
     */
    fun requestShort(id: Int): Observable<ZhihuInfoCommentBean> {
        return RetrofitManager.service.getZhiHuShortComments(id)
                .compose(SchedulerUtils.ioToMain())
    }

    /**
     * 获取长评论
     */
    fun requestLong(id: Int): Observable<ZhihuInfoCommentBean> {
        return RetrofitManager.service.getZhiHuLongComments(id)
                .compose(SchedulerUtils.ioToMain())
    }
}