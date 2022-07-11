package com.mtp.tsmkit.kotlin

import com.mtp.tsmkit.AppApplication

/**
 *@desc:
 * @author: 991167006@qq.com
 * @date: 2021/11/17 : 下午10:28
 *@copyright: xujixiong.ps
 */


class Utils {

    companion object {
        fun density(): Float {
            return AppApplication.mApplication?.resources?.displayMetrics?.density ?: 1f
        }
    }

}