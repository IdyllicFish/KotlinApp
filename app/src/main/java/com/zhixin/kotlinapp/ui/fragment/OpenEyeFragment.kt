package com.zhixin.kotlinapp.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.classic.common.MultipleStatusView
import com.hazz.kotlinmvp.net.exception.ErrorStatus
import com.zhixin.kotlinapp.R
import com.zhixin.kotlinapp.mvp.contract.OpenEyeHomeContract
import com.zhixin.kotlinapp.mvp.model.bean.OpenEyeHomeBean
import com.zhixin.kotlinapp.mvp.presenter.OpenEyePresenter
import com.zhixin.kotlinapp.ui.adapter.EyeHomeAdapter
import com.zhixin.ywymvp.base.BaseAdapter
import com.zhixin.ywymvp.base.BaseMvpFragment
import com.zhixin.ywymvp.base.BaseViewHolder
import com.zhixin.ywymvp.utils.log
import com.zhixin.ywymvp.utils.showToast
import kotlinx.android.synthetic.main.open_eye_home_layout.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by shine on 2018/3/20.
 */
class OpenEyeFragment : BaseMvpFragment<OpenEyeHomeContract.View, OpenEyePresenter>(), OpenEyeHomeContract.View, BaseAdapter.OnLoadMoreListener, BaseAdapter.OnItemClickListeners<OpenEyeHomeBean.Issue.Item> {
    private var num: Int = 1

    override fun initPresenter(): OpenEyePresenter = OpenEyePresenter()

    override fun setFirstData(data: OpenEyeHomeBean) {
        log("setFirstData")
        mLayoutStatusView?.showContent()

        // Adapter
        //设置 banner 大小
        mAdapter.setBannerSize(data.issueList[0].count)
        mAdapter.setNewData(data.issueList[0].itemList)

        if (open_eye_rv.adapter == null) {
            open_eye_rv.adapter = mAdapter
            open_eye_rv.layoutManager = layoutManager
            open_eye_rv.itemAnimator = DefaultItemAnimator()
        }

    }

    override fun setLoadMoreData(datas: ArrayList<OpenEyeHomeBean.Issue.Item>) {
        mAdapter.setLoadMoreData(datas)
    }

    override fun showError(msg: String, errorCode: Int) {
        showToast(context, msg)
        if (mAdapter.getDatas().size <= 0) {
            if (errorCode == ErrorStatus.NETWORK_ERROR) {
                mLayoutStatusView!!.showNoNetwork()
            } else {
                mLayoutStatusView!!.showError()
            }
        }
    }

    override fun showLoading() {
        if (!mAdapter.isLoading()) {
            mLayoutStatusView?.showLoading()
            mAdapter.isLoading(true)
        }
    }

    override fun dismissLoading() {
        open_eye_srl.finishRefresh()
        mAdapter.isLoading(false)
    }

    override fun onLoadMore(isReload: Boolean) {

    }

    override fun onItemClick(viewHolder: BaseViewHolder, data: OpenEyeHomeBean.Issue.Item, position: Int) {

    }

    val mAdapter by lazy { EyeHomeAdapter(context, true) }

    val layoutManager by lazy { LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) }

    private val simpleDateFormat by lazy {
        SimpleDateFormat("- MMM. dd, 'Brunch' -", Locale.ENGLISH)
    }

    override fun setMultipleStatusView(): MultipleStatusView = open_eye_stv

    override fun setToolbarTitle(): String = ""

    private var mTitle: String? = null

    companion object {
        fun getInstance(title: String): OpenEyeFragment {
            val fragment = OpenEyeFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.open_eye_home_layout


    override fun initView() {
        allpay_input.setText("alipays://platformapi/startapp?appId=20000193&url=%2Fwww%2FeBill.htm%3Freferer%3DGOPAY%26billKey%3D3050209%26instId%3DSMXZLS1281%26subBizType%3DWATER")
        allpay.setOnClickListener {
            if (TextUtils.isEmpty(allpay_input.text.toString())) {
                showToast(context, "空")
                return@setOnClickListener
            }
            try {
                val url = allpay_input.text.toString()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                showToast(context, "TryCatch")
                e.printStackTrace()
            }

        }
    }

    override fun lazyLoad() {
        mPresenter.requestHomeData(num)
    }

    override fun initListens() {
        open_eye_srl.setOnRefreshListener {
            if (!mAdapter.isLoading()) {
                mAdapter.isLoading(true)
                mPresenter.requestHomeData(num)
            } else {
                open_eye_srl.finishRefresh()
            }
        }
    }

}