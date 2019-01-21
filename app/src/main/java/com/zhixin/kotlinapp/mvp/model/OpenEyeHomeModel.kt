package com.zhixin.kotlinapp.mvp.model

import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import com.zhixin.kotlinapp.mvp.model.bean.OpenEyeHomeBean
import com.zhixin.ywymvp.net.RetrofitManager
import io.reactivex.Observable

class OpenEyeHomeModel {

    /**
     * 获取首页Banner数据
     */
    fun requestFirstData(num: Int): Observable<OpenEyeHomeBean> {
        return RetrofitManager.openEyeService.getFirstHomeData(num)
                .compose(SchedulerUtils.ioToMain())
    }

    /**
     * 获取更多数据
     */
    fun loadMoreData(url: String): Observable<OpenEyeHomeBean> {
        return RetrofitManager.openEyeService.getMoreHomeData(url)
                .compose(SchedulerUtils.ioToMain())
    }


}