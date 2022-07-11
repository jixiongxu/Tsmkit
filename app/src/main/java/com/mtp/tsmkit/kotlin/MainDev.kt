package com.mtp.tsmkit.kotlin

import android.util.Log
import com.mtp.tsmkit_core.annotation.TsmKit
import com.mtp.tsmkit_core.annotation.RunType


/**
 *@desc:
 * @author: 991167006@qq.com
 * @date: 2021/11/9 : 下午11:20
 *@copyright: xujixiong.ps
 */
class MainDev {

    @TsmKit(dispatcher = RunType.IO)
    fun hello() {
        Thread.sleep(5000)
        Log.d("xujixiong", "hello:" + Thread.currentThread().name)
        14f.dp
    }

}