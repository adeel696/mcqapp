package com.digitalandroidweb.androidregisterandlogin.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants.Companion.USER_ID
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants.Companion.USER_PASS
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants.Companion.USER_TOKEN
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class SharedPreference {
    companion object {
        var mSharedPreferences: SharedPreferences? = null

        private fun initShardPreference(context: Context): SharedPreferences? {
            if (mSharedPreferences == null) {
                mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            }
            return mSharedPreferences
        }

        fun setUserID(context: Context, userid: String) {
            val editor: SharedPreferences.Editor = initShardPreference(context)!!.edit()
            editor.putString(USER_ID, userid)
            editor.apply()
        }

        fun setUserPass(context: Context, pass: String) {
            val editor: SharedPreferences.Editor = initShardPreference(context)!!.edit()
            editor.putString(USER_PASS, pass)
            editor.apply()
        }

        fun setUserToken(context: Context, userToken: String) {
            val editor: SharedPreferences.Editor = initShardPreference(context)!!.edit()
            Log.d(SharedPreference::class.simpleName, "setUserToken: $userToken")
            editor.putString(USER_TOKEN, userToken)
            editor.apply()
        }



        fun getUserID(context: Context): String? {
            val msharedPreferences: SharedPreferences? = initShardPreference(context)
            if (msharedPreferences != null) {
                return msharedPreferences.getString(USER_ID, "")
            }
            return ""
        }

        fun getUserPass(context: Context): String? {
            val msharedPreferences: SharedPreferences? = initShardPreference(context)
            if (msharedPreferences != null) {
                return msharedPreferences.getString(USER_PASS, "")
            }
            return ""
        }

        fun getUserToken(context: Context): String? {
            val msharedPreferences: SharedPreferences? = initShardPreference(context)
            if (msharedPreferences != null) {
                return msharedPreferences.getString(USER_TOKEN, "")
            }
            return ""
        }


    }
}