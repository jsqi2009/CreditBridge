package com.credit.bridge.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.Stack

class AppActivityManager private constructor() {
    /**
     */
    fun addActivity(activity: Activity?) {
        if (activityStack == null) {
            activityStack = Stack<Activity>()
        }
        activityStack!!.add(activity)
    }

    /**
     */
    fun currentActivity(): Activity? {
        val activity: Activity? = activityStack!!.lastElement()
        return activity
    }

    /**
     */
    fun finishActivity() {
        val activity: Activity? = activityStack!!.lastElement()
        finishActivity(activity)
    }

    /**
     *
     */
    fun removeActivity(activity: Activity?) {
        if (activity != null) {
            activityStack?.remove(activity)
        }
    }

    fun finishActivity(activity: Activity?) {
        var activity = activity
        if (activity != null) {
            activityStack?.remove(activity)
            if (!activity.isFinishing) {
                activity.finish()
            }
            activity = null
        }
    }

    /**
     *
     */
    fun finishActivity(cls: Class<*>?) {
        for (activity in activityStack!!) {
            if (activity.javaClass == cls) {
                finishActivity(activity)
            }
        }
    }


    fun finishAllActivity() {
        try {
            val activities = activityStack?.toList().orEmpty()
            activityStack?.clear()
            activities.forEach { activity ->
                if (!activity.isFinishing) {
                    activity.finish()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun AppExit(context: Context) {
        try {
            finishAllActivity()
            val activityMgr = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityMgr.restartPackage(context.getPackageName())
            System.exit(0)
        } catch (e: Exception) {
        }
    }

    companion object {
        private var activityStack: Stack<Activity>? = null
        private var instance: AppActivityManager? = null

        val appManager: AppActivityManager
            /**
             */
            get() {
                if (instance == null) {
                    instance = AppActivityManager()
                }
                return instance!!
            }
    }
}