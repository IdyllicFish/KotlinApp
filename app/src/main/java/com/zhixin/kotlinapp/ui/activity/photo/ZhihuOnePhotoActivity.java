package com.zhixin.kotlinapp.ui.activity.photo;

import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zhixin.kotlinapp.R;
import com.zhixin.ywymvp.base.BaseActivity;


import org.jetbrains.annotations.NotNull;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ZhihuOnePhotoActivity extends BaseActivity implements PhotoViewAttacher.OnPhotoTapListener {
    private PhotoView mPhotoView;
    private ProgressBar mProgressBar;
    private ImageView mSmallImg;

    @Override
    public void initData() {
        Bitmap img = getIntent().getParcelableExtra("bitmap");
        if (img != null) {
            mPhotoView.setImageBitmap(img);
        }
        initPhoto(getIntent().getStringExtra("url"));
    }

    @Override
    public void initView() {
        supportPostponeEnterTransition();
        //隐藏顶部状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPhotoView = (PhotoView) findViewById(R.id.one_image_view);
        mProgressBar = (ProgressBar) findViewById(R.id.one_loading);
        mSmallImg = (ImageView) findViewById(R.id.one_small_img_view);
    }

    private void initPhoto(String url) {
        Glide.with(this)
                .load(url)
                .crossFade(700)//淡出淡入动画
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Toast.makeText(ZhihuOnePhotoActivity.this,"资源加载异常",Toast.LENGTH_SHORT).show();
                        supportStartPostponedEnterTransition();
                        mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    //这个用于监听图片是否加载完成
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mProgressBar.setVisibility(View.GONE);
                        mSmallImg.setVisibility(View.GONE);
                        supportStartPostponedEnterTransition();
                        /**这里应该是加载成功后图片的高*/
                        int height = mPhotoView.getHeight();

                        int wHeight = getWindowManager().getDefaultDisplay().getHeight();
                        if (height > wHeight) {
                            mPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        } else {
                            mPhotoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
                        return false;
                    }
                }).into(mPhotoView);
        mPhotoView.setOnPhotoTapListener(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_zhihu_one_photo;
    }


    /**
     * 退出时要做的动画
     */
    private void back() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        back();
    }

    @Override
    public void onOutsidePhotoTap() {

    }

    @Override
    public boolean setNoStatusBar() {
        return false;
    }

    @NotNull
    @Override
    public String setToolbarTitle() {
        return "";
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
