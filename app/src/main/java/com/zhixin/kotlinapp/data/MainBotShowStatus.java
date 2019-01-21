package com.zhixin.kotlinapp.data;

public class MainBotShowStatus {
    public MainBotShowStatus(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    private boolean isShow;
}
