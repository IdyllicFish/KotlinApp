package com.zhixin.kotlinapp.mvp.model

import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import com.zhixin.ywymvp.net.RetrofitManager
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuDetailBean
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuInfoExtraBean
import io.reactivex.Observable

/**
 * Created by shine on 2018/3/27.
 */
class ZhihuDetailModel {
    /**
     * 获取富文本数据
     */
    fun requestNewsData(id: Int): Observable<ZhihuDetailBean> {
        return RetrofitManager.service.getZhihuDetail(id)
                .compose(SchedulerUtils.ioToMain())
    }

    /**
     * 获取点赞评论数据
     */
    fun requestExtraData(id: Int): Observable<ZhihuInfoExtraBean> {
        return RetrofitManager.service.getZhihuExtra(id)
                .compose(SchedulerUtils.ioToMain())
    }

}