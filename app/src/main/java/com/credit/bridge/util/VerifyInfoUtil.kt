package com.credit.bridge.util

import com.credit.bridge.remote.bean.CommonBean

object VerifyInfoUtil {

    val workTypeFormatList = mutableListOf(
        "GOVERNMENT", "EMPLOYEE", "OWNBUSINESS", "INDEPENDENT",
        "STUDENT", "RETIRED", "UNEMPLOYED", "PARTTIME"
    )

    val monthlyIncomeFormatList = mutableListOf(
        "ICOME_10000_OR_BELOW", "ICOME_10001_20000", "ICOME_20001_30000",
        "ICOME_30001_50000", "ICOME_50001_AND_ABOVE"
    )

    val educationFormatList = mutableListOf(
        "PRIMAY_SCHOOL", "MIDDLE_SCHOOL", "HIGH_SCHOOL",
        "BACHELOR", "MASTER", "PHD", "OTHERS"
    )

    val maritalFormatList = mutableListOf(
        "MARRIED", "SINGLE", "DIVORCED", "WIDOWED"
    )

    val numOfChildrenFormatList = mutableListOf(
        "ZERO", "ONE", "TWO", "THREE", "FOUR", "OVER_FOUR"
    )

    val contact1FormatList = mutableListOf(
        "PARENT", "COUPLE", "CHILD", "BROTHER", "SISTER"
    )

    val contact2FormatList = mutableListOf(
        "PARENT", "COUPLE", "CHILD", "BROTHER", "SISTER", "COLLEAGUE", "FRIEND"
    )

    val genderFormatList = mutableListOf(
        "MALE", "FEMALE"
    )

    fun getWorkTypeList(): ArrayList<CommonBean> {
        val items: ArrayList<CommonBean> = arrayListOf(
            CommonBean(name = "Government"), CommonBean(name = "Employee"),
            CommonBean(name = "Own Business"), CommonBean(name = "Independent"),
            CommonBean(name = "Student"), CommonBean(name = "Retired"),
            CommonBean(name = "Unemployed"), CommonBean(name = "Part-time")
        )
        return items
    }


    fun getMonthlyIncomeList(): ArrayList<CommonBean> {
        val items: ArrayList<CommonBean> = arrayListOf(
            CommonBean(name = "≤10,000"),CommonBean(name = "10,001-20,000"),
            CommonBean(name = "20,001-30,000"),CommonBean(name = "30,001-50,000"),
            CommonBean(name = "≥50,001")
        )
        return items
    }

    fun getEducationList(): ArrayList<CommonBean> {
        val items: ArrayList<CommonBean> = arrayListOf(
            CommonBean(name = "Primay School"), CommonBean(name = "Middle School"),
            CommonBean(name = "High School"), CommonBean(name = "Bachelor"),
            CommonBean(name = "Master"), CommonBean(name = "PHD"),
            CommonBean(name = "Others")
        )
        return items
    }

    fun getMaritalList(): ArrayList<CommonBean> {
        val items: ArrayList<CommonBean> = arrayListOf(
            CommonBean(name = "Married"), CommonBean(name = "Single"),
            CommonBean(name = "Divorced"), CommonBean(name = "Widowed"),
        )
        return items
    }

    fun getNumOfChildrenList(): ArrayList<CommonBean> {
        val items: ArrayList<CommonBean> = arrayListOf(
            CommonBean(name = "No Children"), CommonBean(name = "1 Child"),
            CommonBean(name = "2 Children"), CommonBean(name = "3 Children"),
            CommonBean(name = "4 or More Children"),
        )
        return items
    }

    fun getContact1List(): ArrayList<CommonBean> {
        val items: ArrayList<CommonBean> = arrayListOf(
            CommonBean(name = "Parents"), CommonBean(name = "Couple"),
            CommonBean(name = "Child"), CommonBean(name = "Brother"),
            CommonBean(name = "Sister"),
        )
        return items
    }

    fun getContact2List(): ArrayList<CommonBean> {
        val items: ArrayList<CommonBean> = arrayListOf(
            CommonBean(name = "Parents"), CommonBean(name = "Couple"),
            CommonBean(name = "Child"), CommonBean(name = "Brother"),
            CommonBean(name = "Sister"),CommonBean(name = "Colleague"),
            CommonBean(name = "Friend"),
        )
        return items
    }

    fun getGenderList(): ArrayList<CommonBean> {
        val items: ArrayList<CommonBean> = arrayListOf(
            CommonBean(name = "Male"), CommonBean(name = "Female"),
        )
        return items
    }


}