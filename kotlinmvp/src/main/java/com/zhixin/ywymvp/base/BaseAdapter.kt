package com.zhixin.ywymvp.base

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import java.util.ArrayList

/**
 * Created by shine on 2018/4/28.
 */
abstract class BaseAdapter<T>(var mContext: Context,  var mOpenLoadMore: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var mDatas: MutableList<T> = mutableListOf()
    private var mLoadingView: View? = null
    private var mLoadFailedView: View? = null
    private val mLoadEndView: View? = null
    private var mEmptyView: View? = null
    private var mFooterLayout: RelativeLayout? = null
    private var mLoadMoreListener: OnLoadMoreListener? = null
    private var mItemClickListener: OnItemClickListeners<T>? = null

    var isNeedSpan: Boolean = false

    private var mItemLongClickListeners: OnItemLongClickListeners<T>? = null
    private val isAutoLoadMore = true

    protected abstract fun getItemLayoutId() : Int

    private val footViewCount: Int
        get() = if (mOpenLoadMore) 1 else 0

    //是否正在加载中
    private var isLoading: Boolean = false
    //请求数据源已经全部请求完毕
    private var isEnd: Boolean = false

    val footerViewCount: Int
        get() = if (mOpenLoadMore) 1 else 0


    fun setItemLongClickListeners(itemLongClickListener: OnItemLongClickListeners<T>) {
        this.mItemLongClickListeners = itemLongClickListener
    }

    protected abstract fun convert(holder: BaseViewHolder, data: T)

    interface OnLoadMoreListener {
        fun onLoadMore(isReload: Boolean)
    }

    interface OnItemClickListeners<in T> {
        fun onItemClick(viewHolder: BaseViewHolder, data: T, position: Int)
    }

    interface OnItemLongClickListeners<in T> {
        fun onItemLongClick(viewHolder: BaseViewHolder, data: T, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        var viewHolder: BaseViewHolder? = null
        when (viewType) {
            TYPE_COMMON_VIEW -> viewHolder = BaseViewHolder.create(mContext, getItemLayoutId(), parent)
            TYPE_FOOTER_VIEW -> {
                if (mFooterLayout == null) {
                    mFooterLayout = RelativeLayout(mContext)
                }
                viewHolder = BaseViewHolder.create(mFooterLayout!!)
            }
            TYPE_EMPTY_VIEW -> viewHolder = BaseViewHolder.create(mEmptyView!!)
        }
        return viewHolder

    }

    init {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_COMMON_VIEW -> bindCommonItem(holder, position)
        }
    }

    private fun bindCommonItem(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as BaseViewHolder
        convert(viewHolder, mDatas!![position])
        if (mItemClickListener != null) {
            viewHolder.convertView.setOnClickListener { mItemClickListener!!.onItemClick(viewHolder, mDatas!![position], position) }
        }
        if (mItemLongClickListeners != null) {
            viewHolder.convertView.setOnLongClickListener {
                mItemLongClickListeners!!.onItemLongClick(viewHolder, mDatas!![position], position)
                false
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (isFooterView(position)) {
            TYPE_FOOTER_VIEW
        } else TYPE_COMMON_VIEW
    }

    override fun getItemCount(): Int {
        return mDatas.size + footViewCount
    }


    fun getItem(position: Int): T {
        return mDatas[position]
    }

    /**
     *
     */
    private fun setEmptyView(view: View) {
        mEmptyView = view
    }

    fun setEmptyView(emptyViewId: Int) {
        setEmptyView(LayoutInflater.from(mContext).inflate(emptyViewId, null))
    }

    /**
     * 是否是FooterView
     *
     * @param position
     * @return
     */
    private fun isFooterView(position: Int): Boolean {
        return mOpenLoadMore && itemCount > 1 && position >= itemCount - 1
    }


    /**
     * StaggeredGridLayoutManager模式时，FooterView可占据一行
     *
     * @param holder
     */
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder?) {
        super.onViewAttachedToWindow(holder)
        if (isFooterView(holder!!.layoutPosition)) {
            val lp = holder.itemView.layoutParams

            if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {
                lp.isFullSpan = true
            }
        }
    }

    /*
     * GridLayoutManager模式时， FooterView可占据一行，判断RecyclerView是否到达底部
     *
     * @param recyclerView
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView!!.layoutManager
        if (layoutManager is GridLayoutManager && isNeedSpan) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    /**
                     * 这里返回并不是列数  而是Item的占比 类似于权重
                     */
                    return if (isFooterView(position)) {
                        layoutManager.spanCount
                    } else {
                        when (position % 6) {
                            5 -> 3
                            3 -> 2
                            else -> 1
                        }
                    }
                }
            }
        }
        //为什么只有这一个方法开启加载更多
        startLoadMore(recyclerView, layoutManager)
    }

    fun isEnd(isEnd: Boolean) {
        this.isEnd = isEnd
    }

    fun isLoading(isLoading: Boolean) {
        this.isLoading = isLoading
    }

    fun isLoading():Boolean = isLoading

    /**
     * 判断列表是否滑动到底部
     *
     * @param recyclerView
     * @param layoutManager
     */
    private fun startLoadMore(recyclerView: RecyclerView, layoutManager: RecyclerView.LayoutManager) {
        if (!mOpenLoadMore || mLoadMoreListener == null) {
            return
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //滑动静止
                    if (findLastVisibleItemPosition(layoutManager) + 1 == itemCount) {
                        if (!isLoading && !isEnd) {
                            //避免重复多次请求
                            scrollLoadMore()
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //滑动中
            }

        })
        recyclerView.setOnScrollChangeListener { _, _, _, _, _ ->  }

    }

    /**
     * 到达底部开始刷新
     */
    private fun scrollLoadMore() {
        if (mFooterLayout!!.getChildAt(0) === mLoadingView) {
            mLoadMoreListener!!.onLoadMore(false)
        }
    }

    private fun findLastVisibleItemPosition(layoutManager: RecyclerView.LayoutManager): Int {
        if (layoutManager is LinearLayoutManager) {
            return layoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
            return findMax(lastVisibleItemPositions)
        }
        return -1
    }

    /**
     * 找到瀑布流布局中最后一个可见的子元素的position
     *
     * @param lastVisiblePositions
     * @return
     */
    private fun findMax(lastVisiblePositions: IntArray): Int {
        var max = lastVisiblePositions[0]
        for (value in lastVisiblePositions) {
            if (value > max) {
                max = value
            }
        }
        return max
    }

    /**
     * 移出底部布局中的所有View
     */
    private fun removeFooterView() {
        mFooterLayout!!.removeAllViews()
    }

    /**
     * 添加View至底部布局
     *
     * @param footerView
     */
    private fun addFooterView(footerView: View?) {
        if (footerView == null) {
            return
        }

        if (mFooterLayout == null) {
            mFooterLayout = RelativeLayout(mContext)
        }
        removeFooterView()
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        mFooterLayout!!.addView(footerView, params)
    }

    fun setLoadMoreData(datas: List<T>) {
        val size = mDatas.size
        mDatas.addAll(datas)
        notifyItemInserted(size)
    }

    fun setData(datas: List<T>) {
        mDatas.addAll(0, datas)
        notifyDataSetChanged()
    }

    fun setNewData(datas: List<T>) {
        mDatas.clear()
        mDatas.addAll(datas)
        notifyDataSetChanged()
    }

    /**
     * 初始化加载中布局
     *
     * @param loadingView
     */
    fun setLoadingView(loadingView: View) {
        mLoadingView = loadingView
        addFooterView(mLoadingView)
    }

    fun setLoadingView(loadingId: Int) {
        setLoadingView(LayoutInflater.from(mContext).inflate(loadingId, null))
    }

    /**
     * 初始化全部加载完成布局
     *
     * @param loadEndView
     */
    fun setLoadEndView(loadEndView: View?) {
        if (loadEndView == null) {
            return
        }
        mLoadFailedView = loadEndView
        addFooterView(mLoadFailedView)
        if (!isEnd) {
            mLoadFailedView!!.setOnClickListener {
                addFooterView(mLoadingView)
                mLoadMoreListener!!.onLoadMore(true)
            }
        }
    }

    fun setLoadEndView(loadEndId: Int) {
        setLoadEndView(LayoutInflater.from(mContext).inflate(loadEndId, null))
    }


    fun setOnLoadMoreListener(loadMoreListener: OnLoadMoreListener) {
        mLoadMoreListener = loadMoreListener
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListeners<T>) {
        mItemClickListener = itemClickListener
    }

    companion object {
        val TYPE_COMMON_VIEW = 100001
        val TYPE_FOOTER_VIEW = 100002
        val TYPE_EMPTY_VIEW = 100003
        val TYPE_DEFAULT_VIEW = 100004
        val TYPE_HEAD_VIEW = 100005
    }

//    init {
//        mDatas = datas as MutableList<T>?
//    }

    fun getDatas():MutableList<T>{
        return mDatas
    }

}