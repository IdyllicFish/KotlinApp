package com.zhixin.kotlinapp.mvp.model

import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import com.zhixin.ywymvp.net.RetrofitManager
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuHomeListBean
import io.reactivex.Observable

/**
 * Created by shine on 2018/3/27.
 */
class ZhihuHomeModel {
    /**
     * 获取首页 Banner 数据
     */
    fun requestHomeData(): Observable<ZhihuHomeListBean> {
        return RetrofitManager.service.getZhihuHome()
                .compose(SchedulerUtils.ioToMain())
    }

    /**
     * 加载更多
     */
    fun loadMoreData(date: String): Observable<ZhihuHomeListBean> {
        return RetrofitManager.service.getZhiHuHomeBefore(date)
                .compose(SchedulerUtils.ioToMain())
    }
}