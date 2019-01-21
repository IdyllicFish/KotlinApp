package com.zhixin.kotlinapp.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.zhixin.kotlinapp.R
import com.zhixin.ywymvp.base.BaseAdapter
import com.zhixin.kotlinapp.MyApplication.Companion.context
import com.zhixin.ywymvp.base.BaseViewHolder
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuHomeListBean

/**
 * Created by shine on 2018/4/28.
 */
class ZhihuHomeAdapter(mContext: Context, isOpenLoadMore: Boolean) : BaseAdapter<ZhihuHomeListBean.StoriesBean>(mContext, isOpenLoadMore) {

    override fun convert(holder: BaseViewHolder, data: ZhihuHomeListBean.StoriesBean) {
        if (holder.itemViewType == BaseAdapter.TYPE_COMMON_VIEW) {

            val img = holder.getView<ImageView>(R.id.item_zhihu_home_img)
            val title = holder.getView<TextView>(R.id.item_zhihu_home_title)
            if (data.images != null) {
                if (data.images[0] != null) {
                    Glide.with(mContext)
                            .load(data.images[0])
                            .asBitmap()
                            .into(img)
                }
            }
            if (data.title != null) {
                title!!.text = data.title
            }
        } else if (holder.itemViewType == BaseAdapter.TYPE_DEFAULT_VIEW) {
            val textView = holder.getView<TextView>(R.id.item_zhihu_home_date)
            textView!!.text = data.date
        }


    }

    override fun getItemLayoutId(): Int = R.layout.item_zhihu_home_layout

    override fun getItemViewType(position: Int): Int {
        if (mOpenLoadMore && itemCount > 1 && position >= itemCount - 1) {
            return BaseAdapter.TYPE_FOOTER_VIEW
        }
        return if (getItem(position).type == 10086) {
            BaseAdapter.TYPE_DEFAULT_VIEW
        } else super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        var dateViewHolder: BaseViewHolder?
        if (viewType == BaseAdapter.TYPE_DEFAULT_VIEW) {
            dateViewHolder = BaseViewHolder.create(mContext, R.layout.item_zhihu_home_date_layout, parent)
            return dateViewHolder
        }
        return super.onCreateViewHolder(parent, viewType)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder.itemViewType) {
            BaseAdapter.TYPE_DEFAULT_VIEW -> onBindDefaultItem(holder, position)
        }
    }

    private fun onBindDefaultItem(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as BaseViewHolder
        convert(viewHolder, getItem(position))
    }
}