package com.mtp.tsmkit.kotlin

/**
 *@desc: 扩展函数
 * @author: 991167006@qq.com
 * @date: 2021/11/17 : 下午10:19
 *@copyright: xujixiong.ps
 */

val Float.dp: Int
    get() = (this * Utils.density()).toInt()

val Int.dp: Int
    get() = (this * Utils.density()).toInt()
