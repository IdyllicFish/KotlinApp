package com.zhixin.kotlinapp.mvp.model.bean

import com.flyco.tablayout.listener.CustomTabEntity

/**
 * Created by shine on 2018/3/20.
 */
class TabEntity(var title: String, private var selectedIcon: Int, private var unSelectedIcon: Int) : CustomTabEntity {

    override fun getTabTitle(): String {
        return title
    }

    override fun getTabSelectedIcon(): Int {
        return selectedIcon
    }

    override fun getTabUnselectedIcon(): Int {
        return unSelectedIcon
    }
}