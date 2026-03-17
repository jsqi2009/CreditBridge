package com.credit.bridge.util

import android.content.Context
import android.content.SharedPreferences
import java.util.concurrent.atomic.AtomicReference

object SPUtil {

    private val sharedPreferencesRef = AtomicReference<SharedPreferences>()


    fun init(context: Context) {
        if (sharedPreferencesRef.get() == null) {
            val appContext = context.applicationContext
            val prefs = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPreferencesRef.compareAndSet(null, prefs)
        }
    }

    private val sharedPreferences: SharedPreferences
        get() = sharedPreferencesRef.get()
            ?: throw IllegalStateException("SPUtil has not been initialized. Call SPUtil.init(context) first.")

    fun put(key: String, value: Any) {
        edit { editor ->
            when (value) {
                is String -> editor.putString(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Set<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    editor.putStringSet(key, value as Set<String>)
                }
                else -> {
                    throw IllegalArgumentException("Unsupported value type: ${value.javaClass.name}")
                }
            }
        }
    }

    fun getInt(key: String, defaultValue: Int = 0): Int = sharedPreferences.getInt(key, defaultValue)

    fun getLong(key: String, defaultValue: Long = 0L): Long = sharedPreferences.getLong(key, defaultValue)

    fun getFloat(key: String, defaultValue: Float = 0f): Float = sharedPreferences.getFloat(key, defaultValue)

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean = sharedPreferences.getBoolean(key, defaultValue)

    fun getString(key: String, defaultValue: String = ""): String =
        sharedPreferences.getString(key, defaultValue) ?: defaultValue

    fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String> =
        sharedPreferences.getStringSet(key, defaultValue) ?: defaultValue

    fun putDouble(key: String, value: Double) {
        put(key, value.toString())
    }

    fun getDouble(key: String, defaultValue: Double = 0.0): Double {
        return getString(key, defaultValue.toString()).toDoubleOrNull() ?: defaultValue
    }

    fun remove(key: String) {
        edit { it.remove(key) }
    }

    fun clearAll() {
        edit { it.clear() }
    }

    fun contains(key: String): Boolean = sharedPreferences.contains(key)

    private inline fun edit(block: (SharedPreferences.Editor) -> Unit) {
        val editor = sharedPreferences.edit()
        block(editor)
        editor.apply()
    }
}