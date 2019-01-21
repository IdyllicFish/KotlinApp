package com.zhixin.kotlinapp.ui.fragment

import android.support.v7.widget.LinearLayoutManager
import com.classic.common.MultipleStatusView
import com.hazz.kotlinmvp.net.exception.ErrorStatus
import com.zhixin.kotlinapp.R
import com.zhixin.kotlinapp.mvp.contract.ZhihuCommentContract
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuInfoCommentBean
import com.zhixin.kotlinapp.mvp.presenter.ZhihuCommentPresenter
import com.zhixin.kotlinapp.ui.adapter.ZhihuInfoCommentAdapter
import com.zhixin.ywymvp.base.BaseAdapter
import com.zhixin.ywymvp.base.BaseMvpFragment
import com.zhixin.ywymvp.base.BaseViewHolder
import com.zhixin.ywymvp.utils.log
import com.zhixin.ywymvp.utils.showToast
import kotlinx.android.synthetic.main.fragment_comment_layout.*

class ZhihuCommentFragment : BaseMvpFragment<ZhihuCommentContract.View, ZhihuCommentPresenter>(), ZhihuCommentContract.View, ZhihuInfoCommentAdapter.OnClickHeadListener, BaseAdapter.OnItemClickListeners<ZhihuInfoCommentBean.CommentsBean> {
    override fun setMultipleStatusView(): MultipleStatusView = zhihu_comment_statusview

    private val detailId: Int by lazy { activity.intent.getIntExtra("id", 0) }
    private val mAdapter: ZhihuInfoCommentAdapter by lazy { ZhihuInfoCommentAdapter(context, true) }
    private val layoutManager by lazy { LinearLayoutManager(context) }
    private var type: Int = 0

    companion object {
        fun getInstance(type: Int): ZhihuCommentFragment {
            val fragment = ZhihuCommentFragment()
            fragment.type = type
            return fragment
        }
    }

    override fun initPresenter(): ZhihuCommentPresenter = ZhihuCommentPresenter()

    override fun getLayoutId(): Int = R.layout.fragment_comment_layout

    override fun initView() {
        mLayoutStatusView!!.showLoading()
    }

    override fun initListens() {
        comment_rfl.setColorSchemeResources(R.color.colorPrimaryDark)
        comment_rfl.setOnRefreshListener {
            mAdapter.isLoading(true)
            lazyLoad()
        }
    }

    override fun lazyLoad() {
        if (detailId == 0)
            return
        if (type == 0) {
            mPresenter.requestShortData(detailId)
        } else {
            mPresenter.requestLongData(detailId)
        }
    }

    override fun setToolbarTitle(): String = ""

    override fun setShortData(data: ZhihuInfoCommentBean) {
        if (data.comments.size>0){
            ininRv(data.comments)
            mLayoutStatusView?.showContent()
        }else{
            mLayoutStatusView?.showEmpty()
        }
    }

    override fun setLongData(data: ZhihuInfoCommentBean) {
        if (data.comments.size>0){
            ininRv(data.comments)
            mLayoutStatusView?.showContent()
        }else{
            mLayoutStatusView?.showEmpty()
        }

    }

    override fun showError(msg: String, errorCode: Int) {
        comment_rfl.isRefreshing = false
        showToast(context, msg)
        if (errorCode == ErrorStatus.NETWORK_ERROR) {
            mLayoutStatusView?.showNoNetwork()
        } else {
            mLayoutStatusView?.showError()
        }
    }

    override fun showLoading() {
        if (!mAdapter.isLoading()) {
            mLayoutStatusView?.showLoading()
            mAdapter.isLoading(true)
        }
    }

    override fun dismissLoading() {
        comment_rfl.isRefreshing = false
        mAdapter.isLoading(false)
    }

    /**
     * 初始化评论列表
     *
     * @param comments
     */
    private fun ininRv(comments: List<ZhihuInfoCommentBean.CommentsBean>) {
        if (zhihu_comment_rv.adapter == null) {
            mAdapter.isEnd(true)
            mAdapter.setLoadEndView(R.layout.load_end_layout)
            zhihu_comment_rv.layoutManager = layoutManager
            zhihu_comment_rv.adapter = mAdapter
            zhihu_comment_rv.isNestedScrollingEnabled = false//禁止rv滑动
            mAdapter.setOnClickHeadListener(this)
            mAdapter.setOnItemClickListener(this)
        }
        mAdapter.setNewData(comments)
    }

    override fun headOnClick(url: String, holder: BaseViewHolder) {

    }

    override fun onItemClick(viewHolder: BaseViewHolder, data: ZhihuInfoCommentBean.CommentsBean, position: Int) {

    }
}