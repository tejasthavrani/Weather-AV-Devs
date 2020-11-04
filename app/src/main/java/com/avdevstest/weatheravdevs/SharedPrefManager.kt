package com.avdevstest.weatheravdevs

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager private constructor() {

    companion object {
        private val sharePref = SharedPrefManager()
        private lateinit var sharedPreferences: SharedPreferences


        fun getInstance(context: Context): SharedPrefManager {
            if (!::sharedPreferences.isInitialized) {
                synchronized(SharedPrefManager::class.java) {
                    if (!::sharedPreferences.isInitialized) {
                        sharedPreferences =
                            context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
                    }
                }
            }
            return sharePref
        }
    }


    fun setTimeTemperature(time: String, temperature: String) {
        sharedPreferences.edit().putString(time, temperature).apply()
    }


    //    val firstName: String?
//        get() = sharedPreferences.getString(FIRST_NAME, "")
//
//    fun removeFirstName() {
//        sharedPreferences.edit().remove(FIRST_NAME).apply()
//    }
    fun getAllData(): Map<String, String> {
        return sharedPreferences.all as Map<String, String>
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}