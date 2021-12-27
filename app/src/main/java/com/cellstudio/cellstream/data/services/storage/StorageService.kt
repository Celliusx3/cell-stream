package com.cellstudio.cellstream.data.services.storage

interface StorageService {
    fun getString(key: String): String?
    fun commitString(key: String, value: String)
    fun setString(key: String, value: String)
    fun removeString(key: String)
    fun getAllKeys(): Set<String>
    fun getBoolean(key: String): Boolean
    fun setBoolean(key: String, value: Boolean)
    fun getLong(key: String): Long
    fun setLong(key: String, value: Long)
}
