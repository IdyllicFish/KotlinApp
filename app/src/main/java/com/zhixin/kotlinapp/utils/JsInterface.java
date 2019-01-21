package com.zhixin.kotlinapp.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.util.ArrayList;

import uk.co.senab.photoview.viewbigimage.ViewBigImageActivity;

/**
 * Created by shine on 2017/5/3.
 */

public class JsInterface extends Object {
    private Context mContext;
    private ArrayList<String> listimg = new ArrayList<>();


    public JsInterface(Context mContext) {
        this.mContext = mContext;
    }

    //把所有图片的url保存在ArrayList<String>中
    @JavascriptInterface
    public void readImageUrl(String img) {
        listimg.add(img);
    }


    /**
     * JS打开图片浏览器
     *
     * @param
     */
    @JavascriptInterface
    public void openImage(String clickimg) {
        int index = 0;
        for(String url:listimg)
            if(url.equals(clickimg)) index = listimg.indexOf(clickimg);//获取点击图片在整个页面图片中的位置
        Bundle bundle = new Bundle();
        bundle.putInt("select", 3);// 2,大图显示当前页数，1,头像，不显示页数 3.显示圆点指示器
        bundle.putInt("code", index);//第几张
        bundle.putStringArrayList("imageuri", listimg);
        Intent intent = new Intent(mContext, ViewBigImageActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void clearImages(){
        listimg.clear();
    }
}
