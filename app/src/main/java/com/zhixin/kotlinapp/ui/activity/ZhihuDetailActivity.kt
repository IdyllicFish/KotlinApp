package com.zhixin.kotlinapp.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.FileProvider
import android.support.v4.widget.NestedScrollView
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zhixin.kotlinapp.BuildConfig
import com.zhixin.kotlinapp.R
import com.zhixin.kotlinapp.mvp.contract.ZhihuDetailContract
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuDetailBean
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuInfoExtraBean
import com.zhixin.kotlinapp.mvp.presenter.ZhihuDetailPresenter
import com.zhixin.kotlinapp.ui.activity.photo.ZhihuOnePhotoActivity
import com.zhixin.kotlinapp.utils.JsInterface
import com.zhixin.ywymvp.base.BaseMvpActivity
import com.zhixin.ywymvp.utils.HtmlUtil
import com.zhixin.ywymvp.utils.showToast
import kotlinx.android.synthetic.main.activity_zhihu_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.concurrent.ExecutionException


class ZhihuDetailActivity : BaseMvpActivity<ZhihuDetailContract.View, ZhihuDetailPresenter>(), ZhihuDetailContract.View, View.OnClickListener {

    private val detailId: Int by lazy { intent.getIntExtra("id", 0) }
    private val newsTitle: String by lazy { intent.getStringExtra("title") }
//    private val imgBitmap: Bitmap by lazy { intent.getParcelableExtra<Bitmap>("bitmap") }
    private var newsData: ZhihuDetailBean? = null

    private var botIsShow = true//底部按钮显示还是隐藏

    override fun initPresenter(): ZhihuDetailPresenter = ZhihuDetailPresenter()

    override fun setNewsData(data: ZhihuDetailBean) {
        newsData = data
        Glide.with(this)
                .load(data.image)
                .listener(object : RequestListener<String, GlideDrawable> {
                    override fun onException(e: java.lang.Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                })
                .into(zhihuinfo_head_img)
        val htmlData = HtmlUtil.createHtmlData(data.body, data.css, data.js)
        initWebView(htmlData, data.share_url)
        mLayoutStatusView?.showContent()
    }

    override fun setExtraData(data: ZhihuInfoExtraBean) {
        zhihuinfo_bottom_like.text = String.format("%d赞", data.popularity)
        zhihuinfo_bottom_comment.text = String.format("%d评论", data.comments)
    }

    override fun showError(msg: String, errorCode: Int) {
        showToast(this, msg)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        } else {
            finish()
        }
//        if (errorCode == ErrorStatus.NETWORK_ERROR) {
//            mLayoutStatusView?.showNoNetwork()
//        } else {
//            mLayoutStatusView?.showError()
//        }
    }


    override fun showLoading() {
        mLayoutStatusView?.showLoading()
    }


    override fun dismissLoading() {

    }

    override fun initLayout(): Int = R.layout.activity_zhihu_detail

    override fun setToolbarTitle(): String = newsTitle

    override fun initView() {
        EventBus.getDefault().register(this)
        supportPostponeEnterTransition()
        mLayoutStatusView = zhihu_detail_statusview
//        zhihuinfo_head_img.setImageBitmap(imgBitmap)
        zhihuinfo_collapsingToolbarLayout.setExpandedTitleColor(Color.GREEN)
        zhihuinfo_collapsingToolbarLayout.title = setToolbarTitle()
        if (zhihu_detail_toolbar != null) {
            zhihu_detail_toolbar.title = setToolbarTitle()
            setSupportActionBar(zhihu_detail_toolbar)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        zhihuinfo_bottom_like.setOnClickListener(this)
        zhihuinfo_bottom_comment.setOnClickListener(this)
        val webSettings = zhihuinfo_wv.settings
        webSettings.javaScriptEnabled = true//允许js
        webSettings.domStorageEnabled = true//打开H5缓存
        webSettings.databaseEnabled = true//启用数据库
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT//缓存模式
        webSettings.loadWithOverviewMode = true//缩放至屏幕大小

        zhihuinfo_sc.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY - oldScrollY > 0 && botIsShow) {
                botIsShow = false
                zhihuinfo_bottom.animate().translationY(zhihuinfo_bottom.height.toFloat())
            } else if (scrollY - oldScrollY < 0 && !botIsShow) {
                botIsShow = true
                zhihuinfo_bottom.animate().translationY(0f)
            }
        })

        zhihud_news_share.setOnClickListener(this)
        zhihuinfo_head_img.setOnClickListener(this)
    }

    /**
     * 获取Glide缓存图片
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun getPhotoUrl(data: ZhihuDetailBean) {
        val future = Glide.with(this)
                .load(data.image)
                .downloadOnly(500, 500)
        try {
            val cacheFile = future.get()
            val path = cacheFile.absolutePath
            val oleFile = File(path)
            val newFile = File(path + ".jpg")
            //执行重命名
            oleFile.renameTo(newFile)
            //由文件得到uri
//            val imageUri = Uri.fromFile(File(path))
            val imageUri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    newFile)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"
//            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, setToolbarTitle())
            shareIntent.putExtra(Intent.EXTRA_TEXT, data.title + ":" + data.share_url + "\n分享来自知乎日报")
            shareIntent.putExtra("Kdescription", data.title + ":" + data.share_url + "\n分享来自知乎日报")
            shareIntent.putExtra("describe", data.title + ":" + data.share_url + "\n分享来自知乎日报")
//            var chooserIntent = Intent.createChooser(shareIntent, "分享到：")
            startActivity(shareIntent)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }

    override fun initData() {
        if (detailId == 0) {
            showToast(this, resources.getString(R.string.get_news_error))
            return
        }
        mPresenter.requestNewsData(detailId)
        mPresenter.requestExtraData(detailId)
    }

    override fun setNoStatusBar(): Boolean = true

    private fun initWebView(html: String, url: String) {
        zhihuinfo_wv.loadData(html, HtmlUtil.MIME_TYPE, HtmlUtil.ENCODING)
        zhihuinfo_wv.addJavascriptInterface(JsInterface(this), "imagelistner")//绑定javasrcipt接口，imagelistner为接口的别名
        zhihuinfo_wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                try {
                    // 必须添加才能用支付宝支付
                    if (url.startsWith("zhihu://")) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    }
                } catch (e: Exception) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return false
                }

                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                addImageListner()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.zhihud_news_share -> {
                if (newsData != null)
                    EventBus.getDefault().post(newsData)
            }
            R.id.zhihuinfo_head_img -> {
                if (newsData == null)
                    return
                val intent = Intent(this, ZhihuOnePhotoActivity::class.java)
                intent.putExtra("url", newsData!!.image)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val bitmap = zhihuinfo_head_img.drawingCache
                    intent.putExtra("bitmap", bitmap)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, zhihuinfo_head_img, "head")
                    ActivityCompat.startActivity(this, intent, options.toBundle())
                } else {
                    startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(zhihuinfo_head_img, zhihuinfo_head_img.width / 2, zhihuinfo_head_img.height / 2, 0, 0).toBundle())
                }
            }
            R.id.zhihuinfo_bottom_like -> {

            }
            R.id.zhihuinfo_bottom_comment -> {
                val intent = Intent(this, ZhihuCommentActivity::class.java)
                intent.putExtra("id", detailId)
                intent.putExtra("title", newsTitle)
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition()
                } else {
                    finish()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (zhihuinfo_wv.canGoBack()) {
            zhihuinfo_wv.goBack()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition()
            } else {
                finish()
            }
        }
    }


    override fun onDestroy() {
        if (zhihuinfo_wv != null) {

            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            var parent = zhihuinfo_wv.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(zhihuinfo_wv)
            }

            zhihuinfo_wv.stopLoading()
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            zhihuinfo_wv.settings.javaScriptEnabled = false
            zhihuinfo_wv.clearHistory()
            zhihuinfo_wv.clearView()
            zhihuinfo_wv.removeAllViews()
            zhihuinfo_wv.destroy()
        }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun addImageListner() {
        //遍历页面中所有img的节点，因为节点里面的图片的url即objs[i].src，保存所有图片的src.
        //为每个图片设置点击事件，objs[i].onclick
        zhihuinfo_wv.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{" +
                "window.imagelistner.readImageUrl(objs[i].src);  " +
                " objs[i].onclick=function()  " +
                " {  " +
                " window.imagelistner.openImage(this.src);  " +
                "  }  " +
                "}" +
                "})()")
    }


}
