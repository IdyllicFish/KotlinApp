package com.zhixin.ywymvp.net

import android.content.Context
import android.net.ConnectivityManager
import com.hazz.kotlinmvp.utils.NetworkUtil
import com.hazz.kotlinmvp.utils.Preference
import com.zhixin.kotlinapp.api.ApiService
import com.zhixin.kotlinapp.api.UriConstant
import com.zhixin.kotlinapp.MyApplication
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by xuhao on 2017/11/16.
 *
 */

object RetrofitManager {

    private var client: OkHttpClient? = null
    private var retrofit: Retrofit? = null


    val service: ApiService by lazy { getRetrofit()!!.create(ApiService::class.java) }

    val meiziService: ApiService by lazy { getRetrofit(UriConstant.MEIZI_BASE_URL)!!.create(ApiService::class.java) }

    val openEyeService: ApiService by lazy { getRetrofit(UriConstant.OPEN_EYE_BASE_URL)!!.create(ApiService::class.java) }

    private var token: String by Preference("token", "")

    /**
     * 设置公共参数
     */
    private fun addQueryParameterInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val request: Request
            val modifiedUrl = originalRequest.url().newBuilder()
                    // Provide your custom parameter here
                    .addQueryParameter("phoneSystem", "")
                    .addQueryParameter("phoneModel", "")
                    .build()
            request = originalRequest.newBuilder().url(modifiedUrl).build()
            chain.proceed(request)
        }
    }

    /**
     * 设置头
     */
    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                    // Provide your custom header here
                    .addHeader("Content-Type", "application/json;charset=utf-8")
                    .method(originalRequest.method(), originalRequest.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    /**
     * 设置缓存
     */
    private fun addCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!isNetworkReachable(MyApplication.context)) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
            }
            val response1: Response
            val response = chain.proceed(request)
            if (NetworkUtil.isNetworkAvailable(MyApplication.context)) {
                val maxAge = 60 * 60 * 24 * 7
                // 有网络时 设置缓存超时时间1个小时 ,意思就是不读取缓存数据,只对get有用,post没有缓冲
                response1 = response.newBuilder()
                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "max-age=" + maxAge)
                        .build()
            } else {
                // 无网络时，设置超时为4周  只对get有用,post没有缓冲
                val maxStale = 60 * 60 * 24 * 28
                response1 = response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "max-age=" + maxStale)
                        .build()
            }
            response1
        }
    }

    /**
     * 默认BaseUrl
     */
    fun getRetrofit(): Retrofit? {
        return getRetrofit(UriConstant.ZHIHU_BASE_URL)
    }

    /**
     * 自定义BaseUrl
     */
    fun getRetrofit(baseUrl: String): Retrofit? {
        synchronized(RetrofitManager::class.java) {
            if (retrofit == null) {
                //添加一个log拦截器,打印所有的log
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                //可以设置请求过滤的水平,body,basic,headers
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

                //设置 请求的缓存的大小跟位置
                val cacheFile = File(MyApplication.context.cacheDir, "cache")
                val cache = Cache(cacheFile, 1024 * 1024 * 50) //50Mb 缓存的大小

                client = OkHttpClient.Builder()
//                        .addInterceptor(addQueryParameterInterceptor())  //参数添加
//                        .addInterceptor(addHeaderInterceptor()) // token过滤
                        .addNetworkInterceptor(addCacheInterceptor())
                        .addInterceptor(httpLoggingInterceptor) //日志,所有的请求响应度看到
                        .cache(cache)  //添加缓存
                        .connectTimeout(60L, TimeUnit.SECONDS)
                        .readTimeout(60L, TimeUnit.SECONDS)
                        .writeTimeout(60L, TimeUnit.SECONDS)
                        .build()
            }
            // 获取retrofit的实例
            retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)  //自己配置
                    .client(client!!)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit
    }

    /**
     * 判断网络是否可用
     *
     * @param context Context对象
     */
    fun isNetworkReachable(context: Context): Boolean {
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val current = cm.activeNetworkInfo ?: return false
        return current.isAvailable
    }

}
