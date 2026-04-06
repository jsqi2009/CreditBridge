package com.credit.bridge.util

import com.credit.bridge.remote.bean.CommonBean
import com.credit.bridge.remote.bean.QuestionInfoResponse
import com.credit.bridge.remote.body.RequestSaveQuestionBody

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
            CommonBean(name = "4 Children"),CommonBean(name = "More than 4 Children")
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

    fun getStep1RequestBody(workTypeIndex: Int, monthlyIncomeIndex: Int, educationIndex: Int, maritalIndex: Int,
                            numberOfChildIndex: Int, email: String, whatsapp: String,
                            step1QuestionInfo: QuestionInfoResponse): ArrayList<RequestSaveQuestionBody> {
        val questionList = arrayListOf<RequestSaveQuestionBody>()

        val workerBody = RequestSaveQuestionBody()
        workerBody.vesrq = step1QuestionInfo.ffuyqtcgfw[0].qpwjbrdvuq   //group
        workerBody.snqsj = step1QuestionInfo.ffuyqtcgfw[0].xjcli   //order
        workerBody.vwqveibeqa = step1QuestionInfo.ffuyqtcgfw[0].iinfzdhhhv   //question id
        workerBody.wiuj = 1   //step
        workerBody.qprib = workTypeFormatList[workTypeIndex]   //value

        val incomeBody = RequestSaveQuestionBody()
        incomeBody.vesrq = step1QuestionInfo.ffuyqtcgfw[1].qpwjbrdvuq   //group
        incomeBody.snqsj = step1QuestionInfo.ffuyqtcgfw[1].xjcli   //order
        incomeBody.vwqveibeqa = step1QuestionInfo.ffuyqtcgfw[1].iinfzdhhhv   //question id
        incomeBody.wiuj = 1   //step
        incomeBody.qprib = monthlyIncomeFormatList[monthlyIncomeIndex]   //value

        val educationBody = RequestSaveQuestionBody()
        educationBody.vesrq = step1QuestionInfo.ffuyqtcgfw[2].qpwjbrdvuq   //group
        educationBody.snqsj = step1QuestionInfo.ffuyqtcgfw[2].xjcli   //order
        educationBody.vwqveibeqa = step1QuestionInfo.ffuyqtcgfw[2].iinfzdhhhv   //question id
        educationBody.wiuj = 1   //step
        educationBody.qprib = educationFormatList[educationIndex]   //value

        val maritalBody = RequestSaveQuestionBody()
        maritalBody.vesrq = step1QuestionInfo.ffuyqtcgfw[3].qpwjbrdvuq   //group
        maritalBody.snqsj = step1QuestionInfo.ffuyqtcgfw[3].xjcli   //order
        maritalBody.vwqveibeqa = step1QuestionInfo.ffuyqtcgfw[3].iinfzdhhhv   //question id
        maritalBody.wiuj = 1   //step
        maritalBody.qprib = maritalFormatList[maritalIndex]   //value

        val childBody = RequestSaveQuestionBody()
        childBody.vesrq = step1QuestionInfo.ffuyqtcgfw[4].qpwjbrdvuq   //group
        childBody.snqsj = step1QuestionInfo.ffuyqtcgfw[4].xjcli   //order
        childBody.vwqveibeqa = step1QuestionInfo.ffuyqtcgfw[4].iinfzdhhhv   //question id
        childBody.wiuj = 1   //step
        childBody.qprib = numOfChildrenFormatList[numberOfChildIndex]   //value

        val emailBody = RequestSaveQuestionBody()
        emailBody.vesrq = step1QuestionInfo.ffuyqtcgfw[5].qpwjbrdvuq   //group
        emailBody.snqsj = step1QuestionInfo.ffuyqtcgfw[5].xjcli   //order
        emailBody.vwqveibeqa = step1QuestionInfo.ffuyqtcgfw[5].iinfzdhhhv   //question id
        emailBody.wiuj = 1   //step
        emailBody.qprib = email   //value

        val whatsappBody = RequestSaveQuestionBody()
        whatsappBody.vesrq = step1QuestionInfo.ffuyqtcgfw[6].qpwjbrdvuq   //group
        whatsappBody.snqsj = step1QuestionInfo.ffuyqtcgfw[6].xjcli   //order
        whatsappBody.vwqveibeqa = step1QuestionInfo.ffuyqtcgfw[6].iinfzdhhhv   //question id
        whatsappBody.wiuj = 1   //step
        whatsappBody.qprib = whatsapp   //value

        questionList.add(workerBody)
        questionList.add(incomeBody)
        questionList.add(educationBody)
        questionList.add(maritalBody)
        questionList.add(childBody)
        questionList.add(emailBody)
        questionList.add(whatsappBody)

        return questionList
    }

    fun getStep2RequestBody(relation1: String, relation2: String, contactName1: String, contactName2: String,
                            contactNumber1: String, contactNumber2: String,
                            step2QuestionInfo: QuestionInfoResponse): ArrayList<RequestSaveQuestionBody> {
        val questionList = arrayListOf<RequestSaveQuestionBody>()

        val relation1Body = RequestSaveQuestionBody()
        relation1Body.vesrq = step2QuestionInfo.ffuyqtcgfw[0].rmuon   //group
        relation1Body.snqsj = step2QuestionInfo.ffuyqtcgfw[0].xjcli   //order
        relation1Body.vwqveibeqa = step2QuestionInfo.ffuyqtcgfw[0].iinfzdhhhv   //question id
        relation1Body.wiuj = 2   //step
        relation1Body.qprib = relation1   //value

        val relation2Body = RequestSaveQuestionBody()
        relation2Body.vesrq = step2QuestionInfo.ffuyqtcgfw[1].rmuon   //group
        relation2Body.snqsj = step2QuestionInfo.ffuyqtcgfw[1].xjcli   //order
        relation2Body.vwqveibeqa = step2QuestionInfo.ffuyqtcgfw[1].iinfzdhhhv   //question id
        relation2Body.wiuj = 2   //step
        relation2Body.qprib = relation2   //value

        val contactName1Body = RequestSaveQuestionBody()
        contactName1Body.vesrq = step2QuestionInfo.ffuyqtcgfw[2].rmuon   //group
        contactName1Body.snqsj = step2QuestionInfo.ffuyqtcgfw[2].xjcli   //order
        contactName1Body.vwqveibeqa = step2QuestionInfo.ffuyqtcgfw[2].iinfzdhhhv   //question id
        contactName1Body.wiuj = 2   //step
        contactName1Body.qprib = contactName1   //value

        val contactName2Body = RequestSaveQuestionBody()
        contactName2Body.vesrq = step2QuestionInfo.ffuyqtcgfw[3].rmuon   //group
        contactName2Body.snqsj = step2QuestionInfo.ffuyqtcgfw[3].xjcli   //order
        contactName2Body.vwqveibeqa = step2QuestionInfo.ffuyqtcgfw[3].iinfzdhhhv   //question id
        contactName2Body.wiuj = 2   //step
        contactName2Body.qprib = contactName2   //value

        val contactNumber1Body = RequestSaveQuestionBody()
        contactNumber1Body.vesrq = step2QuestionInfo.ffuyqtcgfw[4].rmuon   //group
        contactNumber1Body.snqsj = step2QuestionInfo.ffuyqtcgfw[4].xjcli   //order
        contactNumber1Body.vwqveibeqa = step2QuestionInfo.ffuyqtcgfw[4].iinfzdhhhv   //question id
        contactNumber1Body.wiuj = 2   //step
        contactNumber1Body.qprib = contactNumber1   //value

        val contactNumber2Body = RequestSaveQuestionBody()
        contactNumber2Body.vesrq = step2QuestionInfo.ffuyqtcgfw[5].rmuon   //group
        contactNumber2Body.snqsj = step2QuestionInfo.ffuyqtcgfw[5].xjcli   //order
        contactNumber2Body.vwqveibeqa = step2QuestionInfo.ffuyqtcgfw[5].iinfzdhhhv   //question id
        contactNumber2Body.wiuj = 2   //step
        contactNumber2Body.qprib = contactNumber2   //value

        questionList.add(relation1Body)
        questionList.add(relation2Body)
        questionList.add(contactName1Body)
        questionList.add(contactName2Body)
        questionList.add(contactNumber1Body)
        questionList.add(contactNumber2Body)

        return questionList
    }

    fun getStep3RequestBody(accountNum: String, confirmAccountNum: String, ifscCode: String,
                            step3QuestionInfo: QuestionInfoResponse): ArrayList<RequestSaveQuestionBody> {
        val questionList = arrayListOf<RequestSaveQuestionBody>()

        val accountNumBody = RequestSaveQuestionBody()
        //accountNumBody.vesrq = step3QuestionInfo.ffuyqtcgfw[0].xjcli.toString()   //group
        accountNumBody.vesrq = ""   //group
        accountNumBody.snqsj = step3QuestionInfo.ffuyqtcgfw[0].xjcli   //order
        accountNumBody.vwqveibeqa = step3QuestionInfo.ffuyqtcgfw[0].iinfzdhhhv   //question id
        accountNumBody.wiuj = 3   //step
        accountNumBody.qprib = accountNum   //value

        val confirmAccountNumBody = RequestSaveQuestionBody()
        //confirmAccountNumBody.vesrq = step3QuestionInfo.ffuyqtcgfw[1].xjcli.toString()   //group
        accountNumBody.vesrq = ""   //group
        confirmAccountNumBody.snqsj = step3QuestionInfo.ffuyqtcgfw[1].xjcli   //order
        confirmAccountNumBody.vwqveibeqa = step3QuestionInfo.ffuyqtcgfw[1].iinfzdhhhv   //question id
        confirmAccountNumBody.wiuj = 3   //step
        confirmAccountNumBody.qprib = confirmAccountNum   //value

        val ifscCodeBody = RequestSaveQuestionBody()
        //ifscCodeBody.vesrq = step3QuestionInfo.ffuyqtcgfw[2].xjcli.toString()   //group
        accountNumBody.vesrq = ""   //group
        ifscCodeBody.snqsj = step3QuestionInfo.ffuyqtcgfw[2].xjcli   //order
        ifscCodeBody.vwqveibeqa = step3QuestionInfo.ffuyqtcgfw[2].iinfzdhhhv   //question id
        ifscCodeBody.wiuj = 3   //step
        ifscCodeBody.qprib = ifscCode   //value

        questionList.add(accountNumBody)
        questionList.add(confirmAccountNumBody)
        questionList.add(ifscCodeBody)

        return questionList
    }

    fun getStep4RequestBody(panNumber: String, fullName: String, birthDate: String, gender: String,
                            step4QuestionInfo: QuestionInfoResponse): ArrayList<RequestSaveQuestionBody> {
        val questionList = arrayListOf<RequestSaveQuestionBody>()

        val panNumberBody = RequestSaveQuestionBody()
        panNumberBody.vesrq = step4QuestionInfo.ffuyqtcgfw[0].qpwjbrdvuq   //group
        panNumberBody.snqsj = step4QuestionInfo.ffuyqtcgfw[0].xjcli   //order
        panNumberBody.vwqveibeqa = step4QuestionInfo.ffuyqtcgfw[0].iinfzdhhhv   //question id
        panNumberBody.wiuj = 4   //step
        panNumberBody.qprib = panNumber   //value

        val fullNameBody = RequestSaveQuestionBody()
        fullNameBody.vesrq = step4QuestionInfo.ffuyqtcgfw[1].qpwjbrdvuq   //group
        fullNameBody.snqsj = step4QuestionInfo.ffuyqtcgfw[1].xjcli   //order
        fullNameBody.vwqveibeqa = step4QuestionInfo.ffuyqtcgfw[1].iinfzdhhhv   //question id
        fullNameBody.wiuj = 4   //step
        fullNameBody.qprib = fullName   //value

        val birthDateBody = RequestSaveQuestionBody()
        birthDateBody.vesrq = step4QuestionInfo.ffuyqtcgfw[2].qpwjbrdvuq   //group
        birthDateBody.snqsj = step4QuestionInfo.ffuyqtcgfw[2].xjcli   //order
        birthDateBody.vwqveibeqa = step4QuestionInfo.ffuyqtcgfw[2].iinfzdhhhv   //question id
        birthDateBody.wiuj = 4   //step
        birthDateBody.qprib = birthDate   //value

        val genderBody = RequestSaveQuestionBody()
        genderBody.vesrq = step4QuestionInfo.ffuyqtcgfw[3].qpwjbrdvuq   //group
        genderBody.snqsj = step4QuestionInfo.ffuyqtcgfw[3].xjcli   //order
        genderBody.vwqveibeqa = step4QuestionInfo.ffuyqtcgfw[3].iinfzdhhhv   //question id
        genderBody.wiuj = 4   //step
        genderBody.qprib = gender   //value

        questionList.add(panNumberBody)
        questionList.add(fullNameBody)
        questionList.add(birthDateBody)
        questionList.add(genderBody)

        return questionList
    }

}