package com.credit.bridge.util

import com.credit.bridge.remote.bean.CommonBean

/**
 * author : Jason
 * desc   :
 */
object VerifyInfoUtil {

    fun getWorkTypeList(): ArrayList<CommonBean> {
        val items: ArrayList<CommonBean> = arrayListOf(
            CommonBean(name = "Government"), CommonBean(name = "Employee"),
            CommonBean(name = "Own Business"), CommonBean(name = "Independent"),
            CommonBean(name = "Student"), CommonBean(name = "Retired"),
            CommonBean(name = "Unemployed"), CommonBean(name = "Part-time")
        )
        return items
    }

}