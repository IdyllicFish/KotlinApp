package com.zhixin.ywymvp.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by shine on 2018/4/28.
 */
class BaseViewHolder(val convertView: View) : RecyclerView.ViewHolder(convertView) {
    private var mViews: SparseArray<View> = SparseArray()
    private var mConvertView: View? = null

    init {
        mConvertView = convertView
    }

    /**
     * 通过id获得控件
     *
     * @param viewId
     * @param <T>
     * @return
    </T> */
    fun <T : View> getView(viewId: Int): T? {
        var view: View? = mViews.get(viewId)
        if (view == null) {
            view = mConvertView!!.findViewById(viewId)
            mViews.put(viewId, view)
        }
        return view as T?
    }

    companion object {
        fun create(context: Context, layoutId: Int, parent: ViewGroup): BaseViewHolder {
            val itemView = LayoutInflater.from(context).inflate(layoutId, parent, false)
            return BaseViewHolder(itemView)
        }

        fun create(itemView: View): BaseViewHolder {
            return BaseViewHolder(itemView)
        }


    }

    fun getMconvertView(): View?{
        return mConvertView
    }
}