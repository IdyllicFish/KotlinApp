package com.zhixin.kotlinapp.ui.activity.photo;

import android.os.Build;
import android.view.KeyEvent;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

/**
 * 用于查看多张大图
 *
 * @author jingbin
 */
public class ZhihuPhotoBrowseActivity extends uk.co.senab.photoview.viewbigimage.ViewBigImageActivity {

    /**
     * 点击屏幕退出
     *
     * @param view
     * @param x
     * @param y
     */
    @Override
    public void onPhotoTap(View view, float x, float y) {
        EventBus.getDefault().post(getPage());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    /**
     * 重写返回键
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            EventBus.getDefault().post(getPage());
        }
        return super.dispatchKeyEvent(event);
    }
}
