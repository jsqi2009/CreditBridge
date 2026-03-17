package com.credit.bridge.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class SPCache<T>(
    private val defaultValue: T,
    private val key: String? = null
) : ReadWriteProperty<Any?, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val finalKey = key ?: property.name

        return when (defaultValue) {
            is Boolean -> SPUtil.getBoolean(finalKey, defaultValue) as T
            is Int -> SPUtil.getInt(finalKey, defaultValue) as T
            is Long -> SPUtil.getLong(finalKey, defaultValue) as T
            is Float -> SPUtil.getFloat(finalKey, defaultValue) as T
            is String -> SPUtil.getString(finalKey, defaultValue) as T
            is Set<*> -> SPUtil.getStringSet(finalKey, defaultValue as Set<String>) as T
            is Double -> SPUtil.getDouble(finalKey, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported type: ${(defaultValue as Any).javaClass.name}")
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val finalKey = key ?: property.name

        when (value) {
            is Boolean -> SPUtil.put(finalKey, value)
            is Int -> SPUtil.put(finalKey, value)
            is Long -> SPUtil.put(finalKey, value)
            is Float -> SPUtil.put(finalKey, value)
            is String -> SPUtil.put(finalKey, value)
            is Set<*> -> {
                @Suppress("UNCHECKED_CAST")
                SPUtil.put(finalKey, value as Set<String>)
            }
            is Double -> SPUtil.putDouble(finalKey, value)
            else -> throw IllegalArgumentException("Unsupported type: ${(value as Any).javaClass.name}")
        }
    }
}