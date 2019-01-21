package com.zhixin.kotlinapp.ui.adapter

import android.app.Activity
import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bigkoo.convenientbanner.ConvenientBanner
import com.bigkoo.convenientbanner.holder.Holder
import com.bumptech.glide.Glide
import com.hazz.kotlinmvp.view.recyclerview.ViewHolder
import com.zhixin.kotlinapp.R
import com.zhixin.kotlinapp.mvp.model.bean.OpenEyeHomeBean
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuHomeListBean
import com.zhixin.ywymvp.base.BaseAdapter
import com.zhixin.ywymvp.base.BaseViewHolder
import com.zhixin.ywymvp.utils.durationFormat

class EyeHomeAdapter(mContext: Context, isOpenLoadMore: Boolean) : BaseAdapter<OpenEyeHomeBean.Issue.Item>(mContext, isOpenLoadMore) {

    // banner 作为 RecyclerView 的第一项
    var bannerItemSize = 0

    /**
     * 设置 Banner 大小
     */
    fun setBannerSize(count: Int) {
        bannerItemSize = count
    }

    override fun getItemLayoutId(): Int = R.layout.item_eye_content

    override fun convert(holder: BaseViewHolder, data: OpenEyeHomeBean.Issue.Item) {
        when(holder.itemViewType){
            TYPE_HEAD_VIEW -> {
                var banner = holder.getView<ConvenientBanner<OpenEyeHomeBean.Issue.Item>>(R.id.item_eye_banner)
                createBannerView(banner!!, data.data!!.itemList)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 ->
                BaseAdapter.TYPE_HEAD_VIEW
            (getItem(position + bannerItemSize - 1).type == "textHeader") ->
                BaseAdapter.TYPE_DEFAULT_VIEW//显示日期
            else -> {
                super.getItemViewType(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val itemViewHolder: BaseViewHolder?
        return when (viewType) {
            BaseAdapter.TYPE_DEFAULT_VIEW -> {
                itemViewHolder = BaseViewHolder.create(mContext, R.layout.item_eye_date, parent)
                itemViewHolder
            }
            BaseAdapter.TYPE_HEAD_VIEW -> {
                itemViewHolder = BaseViewHolder.create(mContext, R.layout.item_eye_banner, parent)
                itemViewHolder
            }
            else -> {
                super.onCreateViewHolder(parent, viewType)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder.itemViewType) {
            BaseAdapter.TYPE_DEFAULT_VIEW -> onBindDefaultItem(holder, position)
            BaseAdapter.TYPE_HEAD_VIEW -> onBindDefaultItem(holder, position)
        }
    }

    private fun onBindDefaultItem(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as BaseViewHolder
        convert(viewHolder, getItem(position))
    }

    /**
     * 加载 content item
     */
    private fun setVideoItem(holder: ViewHolder, item: OpenEyeHomeBean.Issue.Item) {
        val itemData = item.data

        val defAvatar = R.mipmap.default_avatar
        val cover = itemData?.cover?.feed
        var avatar = itemData?.author?.icon
        var tagText: String? = "#"

        // 作者出处为空，就显获取提供者的信息
        if (avatar.isNullOrEmpty()) {
            avatar = itemData?.provider?.icon
        }
        // 加载封页图
        Glide.with(mContext)
                .load(cover)
//                .placeholder(R.drawable.placeholder_banner)
//                .transition(DrawableTransitionOptions().crossFade())
                .into(holder.getView(R.id.iv_cover_feed))

        // 如果提供者信息为空，就显示默认
        if (avatar.isNullOrEmpty()) {
            Glide.with(mContext)
                    .load(defAvatar)
//                    .placeholder(R.mipmap.default_avatar).circleCrop()
//                    .transition(DrawableTransitionOptions().crossFade())
                    .into(holder.getView(R.id.iv_avatar))

        } else {
            Glide.with(mContext)
                    .load(avatar)
//                    .placeholder(R.mipmap.default_avatar).circleCrop()
//                    .transition(DrawableTransitionOptions().crossFade())
                    .into(holder.getView(R.id.iv_avatar))
        }
        holder.setText(R.id.tv_title, itemData?.title ?: "")

        //遍历标签
        itemData?.tags?.take(4)?.forEach {
            tagText += (it.name + "/")
        }
        // 格式化时间
        val timeFormat = durationFormat(itemData?.duration)

        tagText += timeFormat

        holder.setText(R.id.tv_tag, tagText!!)

        holder.setText(R.id.tv_category, "#" + itemData?.category)

        holder.setOnItemClickListener(listener = View.OnClickListener {
            //            goToVideoPlayer(mContext as Activity, holder.getView(R.id.iv_cover_feed), item)
        })

    }

    override fun getItemCount(): Int {
        return when {
            getDatas().size > bannerItemSize -> getDatas().size - bannerItemSize + 1
            getDatas().isEmpty() -> 0
            else -> 1
        }
    }

    //创建Banner
    private fun createBannerView(mBanner: ConvenientBanner<OpenEyeHomeBean.Issue.Item>, banners: List<OpenEyeHomeBean.Issue.Item>) {
        mBanner
                .setPages({ BannerImageHolderView() }, banners)
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                //设置轮播时间间隔
                .startTurning(2000)
                .setOnItemClickListener {

                }
                .onPageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }

        }
    }

    class BannerImageHolderView : Holder<OpenEyeHomeBean.Issue.Item> {

        private var imageView: ImageView? = null

        override fun createView(context: Context): View {
            //此处可以根据需求创建任何你想要的布局，不一定是imageView控件
            imageView = ImageView(context)
            imageView!!.scaleType = ImageView.ScaleType.CENTER_CROP
            return imageView as ImageView
        }

        override fun UpdateUI(context: Context, position: Int, data: OpenEyeHomeBean.Issue.Item) {
            Glide.with(context)
                    .load(data.data!!.cover.feed)
                    //.placeholder(R.drawable.default_img)
                    //.error(R.drawable.default_img)
                    .into(imageView)
        }
    }
}