package com.zhixin.kotlinapp.ui.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bigkoo.convenientbanner.ConvenientBanner
import com.bigkoo.convenientbanner.holder.Holder
import com.bumptech.glide.Glide
import com.classic.common.MultipleStatusView
import com.hazz.kotlinmvp.net.exception.ErrorStatus
import com.hazz.kotlinmvp.utils.DisplayManager
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.zhixin.kotlinapp.R
import com.zhixin.kotlinapp.data.MainBotShowStatus
import com.zhixin.kotlinapp.mvp.contract.ZhihuHomeContract
import com.zhixin.kotlinapp.mvp.presenter.ZhihuHomePresenter
import com.zhixin.kotlinapp.ui.activity.ZhihuDetailActivity
import com.zhixin.kotlinapp.ui.activity.photo.ZhihuPhotoBrowseActivity
import com.zhixin.kotlinapp.ui.adapter.ZhihuHomeAdapter
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuHomeListBean
import com.zhixin.ywymvp.base.BaseAdapter
import com.zhixin.ywymvp.base.BaseMvpFragment
import com.zhixin.ywymvp.base.BaseViewHolder
import com.zhixin.ywymvp.utils.DateUtils
import com.zhixin.ywymvp.utils.log
import com.zhixin.ywymvp.utils.showToast
import kotlinx.android.synthetic.main.zhihu_home_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by shine on 2018/3/20.
 */
class ZhihuFragment : BaseMvpFragment<ZhihuHomeContract.View, ZhihuHomePresenter>(), ZhihuHomeContract.View, BaseAdapter.OnItemClickListeners<ZhihuHomeListBean.StoriesBean>, BaseAdapter.OnLoadMoreListener {
    override fun setMultipleStatusView(): MultipleStatusView = multipleStatusView
    //官方没有提供CollapsingToolbarLayout展开状态的方法
    private enum class CollapsingToolbarLayoutState {
        EXPANDED, //打开
        COLLAPSED,
        INTERNEDIATE
    }

    private var state: CollapsingToolbarLayoutState? = null
    //    private var mAdapter: ZhihuHomeAdapter? = null
    private val mAdapter: ZhihuHomeAdapter by lazy { ZhihuHomeAdapter(context, true) }
    private val layoutManager: LinearLayoutManager by lazy { LinearLayoutManager(context) }
    private var currentBannerTitle: String? = null
    private var indexCalendar = Calendar.getInstance()//保存最后一次加载数据的日期
    private var mTitle: String? = null
    private lateinit var mBanner: ConvenientBanner<ZhihuHomeListBean.TopStoriesBean>

    companion object {
        fun getInstance(title: String): ZhihuFragment {
            val fragment = ZhihuFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    /**
     * 加载更多
     */
    override fun onLoadMore(isReload: Boolean) {
        val df = SimpleDateFormat("yyyyMMdd")
        val date = df.format(indexCalendar.time)
        mAdapter.isLoading(true)
        mPresenter.loadMoreData(date)
    }

    /**
     * rv item点击事件
     */
    override fun onItemClick(viewHolder: BaseViewHolder, data: ZhihuHomeListBean.StoriesBean, position: Int) {
        val intent = Intent(activity, ZhihuDetailActivity::class.java)
        intent.putExtra("id", data.id)
        intent.putExtra("title", data.title)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val img = viewHolder.getMconvertView()!!.findViewById<ImageView>(R.id.item_zhihu_home_img) as ImageView
//            var bitmap: Bitmap? = null
//            val drawable = img.drawable as BitmapDrawable
//            if (drawable != null) {
//                bitmap = drawable.bitmap
//            }
//            intent.putExtra("bitmap", bitmap)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, img, "head")
            ActivityCompat.startActivity(context, intent, options.toBundle())
        } else {
            startActivity(intent)
        }
//        startActivity(intent)
    }

    override fun initPresenter(): ZhihuHomePresenter = ZhihuHomePresenter()


    override fun getLayoutId(): Int = R.layout.zhihu_home_layout


    override fun initView() {
        mLayoutStatusView!!.showLoading()
        mBanner = view!!.findViewById(R.id.convenientBanner)
        collapsingToolbarLayout.title = getString(R.string.zhihu_daily)
        collapsingToolbarLayout.setExpandedTitleColor(Color.GREEN)
        if (null != toolbar) {
            toolbar.title = setToolbarTitle()
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            (activity as AppCompatActivity).supportActionBar!!.setHomeButtonEnabled(true)
            (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun initListens() {
//        swipe_refresh_widget.setColorSchemeResources(R.color.colorPrimary)
        mRefreshLayout.setOnRefreshListener {
            if (!mAdapter.isLoading()) {
                mAdapter.isLoading(true)
                lazyLoad()
            } else {
                mRefreshLayout.finishRefresh()
            }
        }
        appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {//实际状态为展开
                if (state != CollapsingToolbarLayoutState.EXPANDED) {
                    state = CollapsingToolbarLayoutState.EXPANDED//修改状态标记为展开
                    if (currentBannerTitle != null)
                        collapsingToolbarLayout.title = currentBannerTitle//设置title为之前记录下来的title
                }
            } else if (Math.abs(verticalOffset) >= appBarLayout.totalScrollRange) {//实际状态为合上
                if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                    collapsingToolbarLayout.title = getString(R.string.zhihu_daily)//设置title为知乎日报
                    state = CollapsingToolbarLayoutState.COLLAPSED//修改状态标记为折叠
                }
            } else {
                if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                    if (state == CollapsingToolbarLayoutState.COLLAPSED) {
                        collapsingToolbarLayout.title = currentBannerTitle//设置title为之前记录下来的title,由折叠变为中间状态时
                    }
                    state = CollapsingToolbarLayoutState.INTERNEDIATE//修改状态标记为中间
                    fab_bot.hide()
                }
            }
        }
//        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            log("setOnScrollChangeListener")
//            if (scrollY - oldScrollY > 0) {
//                EventBus.getDefault().post(MainBotShowStatus(false))
//            } else {
//                EventBus.getDefault().post(MainBotShowStatus(true))
//            }
//        }

        fab_bot.setOnClickListener {
            recyclerView.scrollToPosition(0)
            collapsingToolbarLayout.title = mAdapter.getItem(0).date
        }
    }

    override fun lazyLoad() {
        mPresenter.requestHomeData()
    }

    override fun setToolbarTitle(): String {
        return resources.getString(R.string.zhihu_daily)
    }

    override fun showLoading() {
        if (!mAdapter.isLoading()) {
            mLayoutStatusView?.showLoading()
            mAdapter.isLoading(true)
        }
    }

    override fun dismissLoading() {
        mRefreshLayout.finishRefresh()
        mAdapter.isLoading(false)
    }

    override fun setHomeData(data: ZhihuHomeListBean) {
        createBannerView(data.top_stories)
        initRv(data.stories)
        mLayoutStatusView?.showContent()
    }

    override fun setMoreData(data: ZhihuHomeListBean) {
        val storiesBean = ZhihuHomeListBean.StoriesBean()
        storiesBean.date = DateUtils.getDate(DateUtils.getCalendar(data.date, DateUtils.FORMAT_DATE2), "yyyy年MM月dd日") + "  星期" + DateUtils.getDayOfWeek(DateUtils.getCalendar(data.date, DateUtils.FORMAT_DATE2))
        storiesBean.type = 10086
        val storiesBeanList = data.stories
        storiesBeanList.add(0, storiesBean)
        mAdapter.setLoadMoreData(storiesBeanList)
        indexCalendar.add(Calendar.DATE, -1)
    }

    override fun showError(msg: String, errorCode: Int) {
        showToast(context, msg)
        if (mAdapter.getDatas().size<=0){
            if (errorCode == ErrorStatus.NETWORK_ERROR) {
                mLayoutStatusView?.showNoNetwork()
            } else {
                mLayoutStatusView?.showError()
            }
        }

    }

    private fun initRv(beanList: MutableList<ZhihuHomeListBean.StoriesBean>) {
        val storiesBean = ZhihuHomeListBean.StoriesBean()
        if (recyclerView.adapter == null) {
            storiesBean.date = "今日热文"
            storiesBean.type = 10086
            beanList.add(0, storiesBean)
            mAdapter.setNewData(beanList)
            mAdapter.setOnItemClickListener(this)
            mAdapter.setOnLoadMoreListener(this)
            mAdapter.setLoadingView(R.layout.recycler_foot_item)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = mAdapter
            recyclerView.addOnScrollListener(RecyclerViewScrollDetector())
        } else {
            indexCalendar = Calendar.getInstance()
            storiesBean.date = "今日热文"
            storiesBean.type = 10086
            beanList.add(0, storiesBean)
            mAdapter.setNewData(beanList)
        }
        mAdapter.isEnd(false)
    }

    internal inner class RecyclerViewScrollDetector : RecyclerView.OnScrollListener() {
        private val mScrollThreshold = DisplayManager.dip2px(1.0f)

        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val isSignificantDelta = Math.abs(dy) > this.mScrollThreshold!!

            if (isSignificantDelta) {
                if (dy > 0) {
                    if (fab_bot != null) {
                        fab_bot.hide()
                        EventBus.getDefault().post(MainBotShowStatus(false))
                    }
                } else {
                    if (fab_bot != null) {
                        if (state == CollapsingToolbarLayoutState.COLLAPSED) {
                            fab_bot.show()
                            EventBus.getDefault().post(MainBotShowStatus(true))
                        }
                    }
                }
            }
            val currentVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            if (dy > 0) {
                if (mAdapter.getItemViewType(currentVisibleItemPosition) == BaseAdapter.TYPE_DEFAULT_VIEW) {//是日期
                    collapsingToolbarLayout.title = mAdapter.getItem(currentVisibleItemPosition).date
                }
            } else {
                if (mAdapter.getItemViewType(currentVisibleItemPosition + 1) == BaseAdapter.TYPE_DEFAULT_VIEW) {//是当日最后一条 即后面一条是日期
                    var position = currentVisibleItemPosition

                    while (mAdapter.getItemViewType(position) != BaseAdapter.TYPE_DEFAULT_VIEW) {
                        position--
                    }
                    collapsingToolbarLayout.title = mAdapter.getItem(position).date
                }
            }
        }
    }

    //创建Banner
    private fun createBannerView(banners: List<ZhihuHomeListBean.TopStoriesBean>) {
        currentBannerTitle = banners[0].title
        collapsingToolbarLayout.title = banners[0].title
        mBanner
                .setPages({ BannerImageHolderView() }, banners)
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                //设置轮播时间间隔
                .startTurning(2000)
                .setOnItemClickListener {
                    val bundle = Bundle()
                    bundle.putInt("select", 2)// 2,大图显示当前页数，1,头像，不显示页数
                    bundle.putInt("code", it)//第几张
                    bundle.putStringArrayList("imageuri", getHeadImgList(banners))
                    val intent = Intent(context, ZhihuPhotoBrowseActivity::class.java)
                    intent.putExtras(bundle)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, mBanner, "banner")
                        ActivityCompat.startActivity(context, intent, options.toBundle())
                    } else {
                        startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(mBanner, mBanner.width / 2, mBanner.height / 2, 0, 0).toBundle())
                    }
                }
                .onPageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                    currentBannerTitle = banners[position].title
                    if (collapsingToolbarLayout != null)
                        collapsingToolbarLayout.title = currentBannerTitle
                }
            }

        }
    }

    private fun getHeadImgList(storiesBeanList: List<ZhihuHomeListBean.TopStoriesBean>): ArrayList<String> {
        val imgList = ArrayList<String>()
        for (bean in storiesBeanList) {
            if (bean.image != null)
                imgList.add(bean.image)
        }
        return imgList
    }

    class BannerImageHolderView : Holder<ZhihuHomeListBean.TopStoriesBean> {

        private var imageView: ImageView? = null

        override fun createView(context: Context): View {
            //此处可以根据需求创建任何你想要的布局，不一定是imageView控件
            imageView = ImageView(context)
            imageView!!.scaleType = ImageView.ScaleType.CENTER_CROP
            return imageView as ImageView
        }

        override fun UpdateUI(context: Context, position: Int, data: ZhihuHomeListBean.TopStoriesBean) {
            Glide.with(context)
                    .load(data.image)
                    //.placeholder(R.drawable.default_img)
                    //.error(R.drawable.default_img)
                    .into(imageView)
        }
    }

    /**
     * 返回获取顶部Banner Position
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getHeadPosition(position: Int) {
        mBanner.setcurrentitem(position)
    }
}