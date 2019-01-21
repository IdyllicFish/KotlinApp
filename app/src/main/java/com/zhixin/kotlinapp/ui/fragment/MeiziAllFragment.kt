package com.zhixin.kotlinapp.ui.fragment

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import com.classic.common.MultipleStatusView
import com.hazz.kotlinmvp.net.exception.ErrorStatus
import com.hazz.kotlinmvp.utils.DisplayManager
import com.zhixin.kotlinapp.R
import com.zhixin.kotlinapp.mvp.contract.MeiziPicContract
import com.zhixin.kotlinapp.mvp.model.bean.MeiziPicBean
import com.zhixin.kotlinapp.mvp.presenter.MeiziPicPresenter
import com.zhixin.kotlinapp.ui.activity.photo.ZhihuPhotoBrowseActivity
import com.zhixin.kotlinapp.ui.adapter.MeiziAdapter
import com.zhixin.ywymvp.base.BaseAdapter
import com.zhixin.ywymvp.base.BaseMvpFragment
import com.zhixin.ywymvp.base.BaseViewHolder
import com.zhixin.ywymvp.utils.showToast
import kotlinx.android.synthetic.main.fragment_meizi_layout.*

class MeiziAllFragment : BaseMvpFragment<MeiziPicContract.View, MeiziPicPresenter>(), MeiziPicContract.View, BaseAdapter.OnLoadMoreListener, BaseAdapter.OnItemClickListeners<MeiziPicBean.ResultsBean> {
    override fun setMultipleStatusView(): MultipleStatusView = meizi_fragment_statusview

    override fun onItemClick(viewHolder: BaseViewHolder, data: MeiziPicBean.ResultsBean, position: Int) {
        val bundle = Bundle()
        bundle.putInt("select", 2)// 2,大图显示当前页数，1,头像，不显示页数
        bundle.putInt("code", position)//第几张
        bundle.putStringArrayList("imageuri", getUrlList())
        val intent = Intent(context, ZhihuPhotoBrowseActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onLoadMore(isReload: Boolean) {
        mAdapter.isLoading(true)
        mPresenter.requestPicData(classify, page)
    }

    override fun setPicData(data: MeiziPicBean) {
        if (data.results != null) {
            initRv(data)
            page++
            mLayoutStatusView!!.showContent()
            return
        }
        mLayoutStatusView!!.showError()
        showToast(context, resources.getString(R.string.get_giel_error))
    }

    companion object {
        fun getInstance(classify: String): MeiziAllFragment {
            val fragment = MeiziAllFragment()
            fragment.classify = classify
            return fragment
        }
    }

    private var adapterDatas: MutableList<MeiziPicBean.ResultsBean> = mutableListOf()
    private var page = 1
    private var classify: String = "All"
    private val mAdapter: MeiziAdapter by lazy { MeiziAdapter(context, true) }
    private val layoutManager by lazy { StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }

    override fun initPresenter(): MeiziPicPresenter = MeiziPicPresenter()

    override fun getLayoutId(): Int = R.layout.fragment_meizi_layout

    override fun initView() {
        meizi_fragment_statusview.showLoading()
    }

    override fun initListens() {
        meizi_srl.setColorSchemeResources(R.color.colorPrimaryDark)
        meizi_srl.setOnRefreshListener {
            mAdapter.isLoading(true)
            page = 1
            mPresenter.requestPicData(classify, page)
        }
    }

    override fun lazyLoad() {
        if (adapterDatas.size > 0) {
            var data = MeiziPicBean()
            data.page = page - 1
            data.results = adapterDatas
            initRv(data)
        } else {
            mPresenter.requestPicData(classify, page)
        }

    }

    override fun setToolbarTitle(): String = ""


    override fun showError(msg: String, errorCode: Int) {
        meizi_srl.isRefreshing = false
        showToast(context, msg)
        if (mAdapter.getDatas().size<=0){
            if (errorCode == ErrorStatus.NETWORK_ERROR) {
                mLayoutStatusView!!.showNoNetwork()
            } else {
                mLayoutStatusView!!.showError()
            }
        }
    }

    override fun showLoading() {
        if (!mAdapter.isLoading()) {
            if (mLayoutStatusView!!.viewStatus != MultipleStatusView.STATUS_LOADING)
                mLayoutStatusView!!.showLoading()
            mAdapter.isLoading(true)
        }
    }

    override fun dismissLoading() {
        mAdapter.isLoading(false)
        meizi_srl.isRefreshing = false
    }

    /**
     * 图片数据
     *
     * @param data
     */
    private fun initRv(data: MeiziPicBean) {
        if (meizi_rv.adapter == null) {
            mAdapter.setNewData(data.results)
            mAdapter.setOnItemClickListener(this)
            mAdapter.setOnLoadMoreListener(this)
            mAdapter.setLoadingView(R.layout.recycler_foot_item)
            meizi_rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            meizi_rv.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                    outRect.set(DisplayManager.dip2px(4.0f)!!, DisplayManager.dip2px(4.0f)!!, DisplayManager.dip2px(4.0f)!!, DisplayManager.dip2px(4.0f)!!)
                }
            })
            meizi_rv.adapter = mAdapter
        } else {
            if (data.page == 1) {
                mAdapter.setNewData(data.results)
            } else {
                mAdapter.setLoadMoreData(data.results)
            }
        }
        adapterDatas.addAll(mAdapter.getDatas())
    }

    private fun getUrlList(): ArrayList<String> {
        val imgList = ArrayList<String>()
        for (bean in mAdapter.getDatas()) {
            if (bean.image_url != null)
                imgList.add(bean.image_url)
        }
        return imgList
    }

}