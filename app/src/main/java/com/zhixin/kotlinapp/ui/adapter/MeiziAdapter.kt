package com.zhixin.kotlinapp.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.zhixin.kotlinapp.R
import com.zhixin.kotlinapp.mvp.model.bean.MeiziPicBean
import com.zhixin.ywymvp.base.BaseAdapter
import com.zhixin.ywymvp.base.BaseViewHolder

class MeiziAdapter(mContext: Context, isOpenLoadMore: Boolean) : BaseAdapter<MeiziPicBean.ResultsBean>(mContext, isOpenLoadMore) {
    private var mutableMap =  mutableMapOf<Int,Int>()

    override fun convert(holder: BaseViewHolder, data: MeiziPicBean.ResultsBean) {
        val pic = holder.getView<ImageView>(R.id.item_meizi_img)
//        val pb = holder.getView<ProgressBar>(R.id.item_meizi_pb)
        if (mutableMap.containsKey(holder.adapterPosition)){
            val params = pic!!.layoutParams as FrameLayout.LayoutParams
            params.height = mutableMap.getValue(holder.adapterPosition)
            pic.layoutParams = params
        }
        if (data.image_url != null) {
            Glide.with(mContext)
                    .load(data.image_url)
                    .asBitmap()
//                    .error(R.mipmap.ic_launcher)
                    .listener(object : RequestListener<String, Bitmap> {
                        override fun onException(e: Exception, model: String, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        //这个用于监听图片是否加载完成
                        override fun onResourceReady(resource: Bitmap, model: String, target: Target<Bitmap>, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {

                            return false
                        }
                    })
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                            if (!mutableMap.containsKey(holder.adapterPosition)) {
//                                log("pic.width:---" + resource!!.width + "+++++++++++++++++pic.height:---" + resource.height)
                                val params = pic!!.layoutParams as FrameLayout.LayoutParams
                                params.height = pic.width / resource!!.width * resource.height
                                mutableMap[holder.adapterPosition] = params.height
                                pic.layoutParams = params
                            }
                            pic!!.setImageBitmap(resource)
                        }
                    })
        }
    }

    override fun getItemLayoutId(): Int = R.layout.item_meizi

}