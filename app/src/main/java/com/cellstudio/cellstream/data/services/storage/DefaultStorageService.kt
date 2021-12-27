package com.cellstudio.cellstream.data.services.storage

import android.content.SharedPreferences

class DefaultStorageService(private val prefs: SharedPreferences) : StorageService {

    override fun getBoolean(key: String): Boolean = prefs.getBoolean(key, false)

    override fun setBoolean(key: String, value: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    override fun setLong(key: String, value: Long) {
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    override fun commitString(key: String, value: String) {
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.commit()
    }

    override fun getLong(key: String): Long {
        return prefs.getLong(key, 0)
    }

    override fun getAllKeys(): Set<String> {
        return prefs.all.keys
    }

    override fun removeString(key: String) {
        val editor = prefs.edit()
        editor.remove(key)
        editor.apply()
    }

    override fun getString(key: String): String? {
        return prefs.getString(key, "")
    }

    override fun setString(key: String, value: String) {
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

}