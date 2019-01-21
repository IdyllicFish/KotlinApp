package com.zhixin.kotlinapp.mvp.model

import com.hazz.kotlinmvp.rx.scheduler.SchedulerUtils
import com.zhixin.kotlinapp.api.ApiService
import com.zhixin.kotlinapp.api.UriConstant
import com.zhixin.kotlinapp.mvp.model.bean.MeiziPicBean
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuInfoCommentBean
import com.zhixin.ywymvp.net.RetrofitManager
import io.reactivex.Observable

class MeiziPicModel {

    /**
     * 获取妹子数据
     */
    fun requestPicData(classify: String, page: Int): Observable<MeiziPicBean> {
        return RetrofitManager.meiziService.getMeiziPicList(classify, page)
                .compose(SchedulerUtils.ioToMain())
    }

}