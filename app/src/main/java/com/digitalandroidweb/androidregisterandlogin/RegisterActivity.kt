package com.digitalandroidweb.androidregisterandlogin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.digitalandroidweb.androidregisterandlogin.model.LoginRequest
import com.digitalandroidweb.androidregisterandlogin.model.LoginResponse
import com.digitalandroidweb.androidregisterandlogin.model.RegisterRequest
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants
import com.digitalandroidweb.androidregisterandlogin.util.General
import com.digitalandroidweb.androidregisterandlogin.util.SharedPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private var name: EditText? = null
    private var email: EditText? = null
    private var username: EditText? = null
    private var password: EditText? = null
    private var c_password: EditText? = null
    private var phone: EditText? = null
    private var btn_regist: Button? = null
    private var loading: ProgressBar? = null


    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        loading = findViewById(R.id.loading)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        c_password = findViewById(R.id.c_password)
        phone = findViewById(R.id.phone)
        btn_regist = findViewById(R.id.btn_regist)
        btn_regist!!.setOnClickListener(View.OnClickListener { Regist() })
    }

    private fun Regist() {
        val name = name!!.text.toString().trim { it <= ' ' }
        val email = email!!.text.toString().trim { it <= ' ' }
        val username = username!!.text.toString().trim { it <= ' ' }
        val password = password!!.text.toString().trim { it <= ' ' }
        val confirmPassword = c_password!!.text.toString().trim { it <= ' ' }
        val phoneNumber = phone!!.text.toString().trim { it <= ' ' }
        if (name.isEmpty()) {
            this.name!!.error = getString(R.string.name_error)
            return
        }
        if (email.isEmpty()) {
            this.email!!.error = getString(R.string.email_error)
            return
        }
        if (username.isEmpty()) {
            this.username!!.error = getString(R.string.user_error)
            return
        }
        if (password.isEmpty()) {
            this.password!!.error = getString(R.string.password_error)
            return
        }
        if (confirmPassword.isEmpty()) {
            c_password!!.error = getString(R.string.confirm_pass_error)
            return
        }
        if (!password.equals(confirmPassword, ignoreCase = true)) {
            this.password!!.error = getString(R.string.pass_confirm_error)
            return
        }
        if (phoneNumber.isEmpty()) {
            phone!!.error = getString(R.string.phone_error)
            return
        }

        loading!!.visibility = View.VISIBLE
        btn_regist!!.visibility = View.GONE


        coroutineScope.launch {
            try {
                val registerRequest = RegisterRequest(name,email, username,password,confirmPassword,phoneNumber)
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.userRegister(registerRequest)
                if(response.isSuccessful && response.body()!=null){
                    Log.d(RegisterActivity::class.simpleName, " Success: ${response.body()}")
                    runOnUiThread {
                        loading!!.visibility = View.GONE
                        btn_regist!!.visibility = View.VISIBLE
                        Toast.makeText(applicationContext,getString(R.string.user_register_msg),Toast.LENGTH_LONG).show()
                        finish()
                    }
                }else{
                    Log.d(LoginActivity::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                    runOnUiThread {
                        General.showAlterDialog(getString(R.string.error), getString(R.string.went_wrong_error), this@RegisterActivity)
                        loading!!.visibility = View.GONE
                        btn_regist!!.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.d(LoginActivity::class.simpleName, "callLoginApi: Exception ${e.message} ")
                runOnUiThread {
                    General.showAlterDialog(getString(R.string.error), getString(R.string.went_wrong_error), this@RegisterActivity)
                    loading!!.visibility = View.GONE
                    btn_regist!!.visibility = View.VISIBLE
                }
            }
        }



    }
}