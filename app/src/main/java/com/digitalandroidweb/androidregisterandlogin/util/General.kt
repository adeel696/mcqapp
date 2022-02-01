package com.digitalandroidweb.androidregisterandlogin.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants.Companion.ACCEPT_LABEL
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants.Companion.ACCEPT_LANGUAGE
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants.Companion.CONTENT_TYPE
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants.Companion.ENGLISH
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants.Companion.JSON_TYPE
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class General {

    companion object {
        fun addHeaders(context: Context, isRequiredBoth: Boolean): HashMap<String?, String?> {
            val header = HashMap<String?, String?>()
            if (isRequiredBoth) {
                header[ApplicationConstants.AUTHORIZATION_LABEL] = SharedPreference.getUserToken(context)
                header[CONTENT_TYPE] = JSON_TYPE
                header[ACCEPT_LABEL] = JSON_TYPE
                getCurrentLang(context, header)
            } else {
                getCurrentLang(context, header)
            }
            return header
        }

        private fun getCurrentLang(context: Context, header: HashMap<String?, String?>) {
            header[ACCEPT_LANGUAGE] = ENGLISH
        }

         fun showAlterDialog(title: String,msg:String,context: Context) {
            AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(msg) // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes){ dialog, which ->
                        // Continue with delete operation
                        dialog.dismiss()
                    } // A null listener allows the button to dismiss the dialog and take no further action.
                    //.setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        }

        fun getFormatedDateResults(date: String): String {
            val outputFormat: DateFormat = SimpleDateFormat("dd, MMM yyyy", Locale.US)
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val inputText = date
            val Date = inputFormat.parse(inputText)
            val outputText = outputFormat.format(Date)
            Log.e("format ", outputText)
            return outputText
        }


        fun getFormatedDateHistory(date: String): String {
            val outputFormat: DateFormat = SimpleDateFormat("dd, MMM yyyy", Locale.US)
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val inputText = date
            val Date = inputFormat.parse(inputText)
            val outputText = outputFormat.format(Date)
            Log.e("format ", outputText)
            return outputText
        }


    }



}