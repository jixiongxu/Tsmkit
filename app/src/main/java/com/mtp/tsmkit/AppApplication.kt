package com.mtp.tsmkit

import android.app.Application

/**
 *@desc:
 * @author: 991167006@qq.com
 * @date: 2021/11/17 : 下午10:20
 *@copyright: xujixiong.ps
 */
class AppApplication : Application() {

    companion object {
        var mApplication: Application? = null
    }

    override fun onCreate() {
        super.onCreate()
        mApplication = this
    }
}