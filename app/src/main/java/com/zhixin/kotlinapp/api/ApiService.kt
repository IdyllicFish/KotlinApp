package com.zhixin.kotlinapp.api

import com.zhixin.kotlinapp.mvp.model.bean.*
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Api 接口
 */

interface ApiService{
    /**
     * 获取知乎首页新闻列表
     */
    @GET("4/news/latest")
    fun getZhihuHome(): Observable<ZhihuHomeListBean>

    /**
     * 根据日期获取历史数据
     */
    @GET("4/news/before/{date}")
    fun getZhiHuHomeBefore(@Path("date") date: String): Observable<ZhihuHomeListBean>

    /**
     * 根据日期获取历史数据
     */
    @GET("4/news/{id}")
    fun getZhihuDetail(@Path("id") id: Int): Observable<ZhihuDetailBean>

    /**
     * 获取新闻额外信息，赞数，评论数
     *
     * @param id
     * @return
     */
    @GET("4/story-extra/{id}")
    fun getZhihuExtra(@Path("id") id: Int): Observable<ZhihuInfoExtraBean>

    /**
     * 获取知乎短评论
     * @param id
     * @return
     */
    @GET("4/story/{id}/short-comments")
    fun getZhiHuShortComments(@Path("id") id: Int): Observable<ZhihuInfoCommentBean>

    /**
     * 获取知乎长评论
     * @param id
     * @return
     */
    @GET("4/story/{id}/long-comments")
    fun getZhiHuLongComments(@Path("id") id: Int): Observable<ZhihuInfoCommentBean>

    /**
     * 妹子图片
     */
    @GET("{classify}/page/{page}")
    fun getMeiziPicList(@Path("classify") classify: String,@Path("page") page: Int): Observable<MeiziPicBean>

    /**
     * 开眼视频首页精选
     */
    @GET("v2/feed?")
    fun getFirstHomeData(@Query("num") num:Int): Observable<OpenEyeHomeBean>

    /**
     * 根据 nextPageUrl 请求数据下一页数据
     */
    @GET
    fun getMoreHomeData(@Url url: String): Observable<OpenEyeHomeBean>

}