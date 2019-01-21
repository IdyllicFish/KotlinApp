package com.zhixin.kotlinapp.ui.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.zhixin.kotlinapp.R
import com.zhixin.kotlinapp.common.GlideCircleTransform
import com.zhixin.kotlinapp.mvp.model.bean.ZhihuInfoCommentBean
import com.zhixin.ywymvp.base.BaseAdapter
import com.zhixin.ywymvp.base.BaseViewHolder

class ZhihuInfoCommentAdapter(mContext: Context, isOpenLoadMore: Boolean) : BaseAdapter<ZhihuInfoCommentBean.CommentsBean>(mContext, isOpenLoadMore) {
    private var onClickHeadListener: OnClickHeadListener? = null

    override fun convert(holder: BaseViewHolder, data: ZhihuInfoCommentBean.CommentsBean) {
        val head = holder.getView<ImageView>(R.id.zhihu_comment_head)//评论者头像
        val author = holder.getView<TextView>(R.id.zhihu_comment_name)//评论者name
        val time = holder.getView<TextView>(R.id.zhihu_comment_time)//评论时间
        val praise = holder.getView<TextView>(R.id.zhihu_comment_praise_num)//被赞数量
        val toComment = holder.getView<RelativeLayout>(R.id.zhihu_comment_replyto_rl)//被评论者布局，没有就隐藏
        val toAuthor = holder.getView<TextView>(R.id.zhihu_to_comment_name)//被评论者name
        val toContent = holder.getView<TextView>(R.id.zhihu_to_comment_content)//被评论者评论
        val content = holder.getView<TextView>(R.id.zhihu_comment_content)//评论者评论
        val cutLine = holder.getView<View>(R.id.zhihu_comment_cutline)//分割线
        if (data.avatar != null) {
            Glide.with(mContext)
                    .load(data.avatar)
                    .transform(GlideCircleTransform(mContext))
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(head)
        }
        if (data.author != null) {//评论者name
            author!!.text = data.author
        }
        time!!.text = "${data.time}"//评论时间
        praise!!.text = "${data.likes}"//被赞次数
        if (data.reply_to != null) {//有被评论者信息
            toComment!!.visibility = View.VISIBLE//先打开此布局
            if (data.reply_to.status == 0) {//评论正常
                if (data.reply_to.author != null) {//被评论者名字不为空
                    toAuthor!!.text = data.reply_to.author
                }
                if (data.reply_to.content != null) {//被评论者评论
                    toContent!!.text = data.reply_to.content
                }
            }else if (data.reply_to.status == 1){//原评论已删除
                if (data.reply_to.error_msg != null){
                    toContent!!.text = data.reply_to.error_msg
                }
            }
        } else {//没有被评论者信息就隐藏此布局
            toComment!!.visibility = View.GONE
        }
        if (data.content != null) {//评论内容
            content!!.text = data.content
        }
        if (holder.adapterPosition + 1 === itemCount) {//最后一条隐藏分割线
            cutLine!!.visibility = View.GONE
        }
    }

    override fun getItemLayoutId(): Int = R.layout.item_zhihu_comment_layout

    /**
     * 头像的点击事件
     */
    interface OnClickHeadListener {
        fun headOnClick(url: String, holder: BaseViewHolder)
    }

    fun setOnClickHeadListener(onClickHeadListener: OnClickHeadListener) {
        this.onClickHeadListener = onClickHeadListener
    }
}