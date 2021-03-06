package com.example.notforgot

import android.content.Context
import android.preference.PreferenceManager


object PreferenceUtils {
    fun saveEmail(email: String?, context: Context?): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val prefsEditor = prefs.edit()
        prefsEditor.putString(Constants.KEY_EMAIL, email)
        prefsEditor.apply()
        return true
    }

    fun getEmail(context: Context?): String? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(Constants.KEY_EMAIL, null)
    }

    fun deleteEmail(context: Context?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().remove(Constants.KEY_EMAIL).apply()
    }

    fun savePassword(password: String?, context: Context?): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val prefsEditor = prefs.edit()
        prefsEditor.putString(Constants.KEY_PASSWORD, password)
        prefsEditor.apply()
        return true
    }

    fun getPassword(context: Context?): String? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(Constants.KEY_PASSWORD, null)
    }

    fun deletePassword(context: Context?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().remove(Constants.KEY_PASSWORD).apply()
    }

    fun saveName(surname: String?, context: Context?): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val prefsEditor = prefs.edit()
        prefsEditor.putString(Constants.KEY_USER_NAME, surname)
        prefsEditor.apply()
        return true
    }

    fun getName(context: Context?): String? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(Constants.KEY_USER_NAME, null)
    }
}