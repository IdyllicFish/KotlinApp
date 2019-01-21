package com.zhixin.ywymvp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Shine on 2018/4/28.
 */
fun log(tag: String = "ywy",msg: Any) = Log.e(tag, msg.toString())
fun log(msg: Any) = Log.e("ywy", msg.toString())

val sdf_tz = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").apply { timeZone = TimeZone.getTimeZone("UTC") }
val sdf_nyr = SimpleDateFormat("yyyy年MM月dd日 HH:mm")
val sdf__ = SimpleDateFormat("yyyy-MM-dd")

fun showToast(mContext: Context,msg: String) = Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show()

fun durationFormat(duration: Long?): String {
    val minute = duration!! / 60
    val second = duration % 60
    return if (minute <= 9) {
        if (second <= 9) {
            "0$minute' 0$second''"
        } else {
            "0$minute' $second''"
        }
    } else {
        if (second <= 9) {
            "$minute' 0$second''"
        } else {
            "$minute' $second''"
        }
    }
}


