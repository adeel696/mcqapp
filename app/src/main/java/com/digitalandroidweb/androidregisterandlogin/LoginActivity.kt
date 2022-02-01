package com.digitalandroidweb.androidregisterandlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.digitalandroidweb.androidregisterandlogin.model.LoginRequest
import com.digitalandroidweb.androidregisterandlogin.model.LoginResponse
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants
import com.digitalandroidweb.androidregisterandlogin.util.General.Companion.showAlterDialog
import com.digitalandroidweb.androidregisterandlogin.util.SharedPreference
import com.digitalandroidweb.androidregisterandlogin.views.Dashboard.DashboardActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    var sessionManager: SessionManager? = null
    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sessionManager = SessionManager(this)

        btn_login.setOnClickListener(View.OnClickListener {
            val mEmail = email.getText().toString().trim { it <= ' ' }
            val mPass = password.getText().toString().trim { it <= ' ' }


            if (!mEmail.isEmpty() || !mPass.isEmpty()) {
                Login(mEmail, mPass);
            } else {
                email.error = getString(R.string.email_error)
                password.error = getString(R.string.password_error)
            }
        })
        link_regist.setOnClickListener(View.OnClickListener { startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) })
    }

    private fun Login(email: String, password: String) {
        loading!!.visibility = View.VISIBLE
        btn_login!!.visibility = View.INVISIBLE
        callLoginApi(email, password)
    }

    private fun callLoginApi(email: String, password: String) {
        Log.d(LoginActivity::class.simpleName, "callLoginApi: ")
        coroutineScope.launch {
            try {
                val loginRequest = LoginRequest(email, password)
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.userLogin(loginRequest)
                if(response.isSuccessful && response.body()!=null){
                    Log.d(LoginActivity::class.simpleName, "callLoginApi Success: ${response.body()}")
                    val loginResponse = response.body() as LoginResponse
                    SharedPreference.setUserToken(this@LoginActivity,loginResponse.token_type+" "+loginResponse.access_token)
                    SharedPreference.setImageUrl(this@LoginActivity,loginResponse.user_image)
                    SharedPreference.setName(this@LoginActivity,loginResponse.name)
                    runOnUiThread {
                        loading!!.visibility = View.GONE
                        btn_login!!.visibility = View.VISIBLE
                        gotoDashboard(email)
                    }
                }else{
                    Log.d(LoginActivity::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                    runOnUiThread {
                        if(response.code() == ApplicationConstants.UNAUTHORIZE_CODE) {
                            showAlterDialog(getString(R.string.error), getString(R.string.username_error), this@LoginActivity)
                        }else{
                            Log.d(LoginActivity::class.simpleName, "callLoginApi:${response.code()} ")
                        }
                        loading!!.visibility = View.GONE
                        btn_login!!.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.d(LoginActivity::class.simpleName, "callLoginApi: Exception ${e.message} ")
                runOnUiThread {
                    showAlterDialog(getString(R.string.error), getString(R.string.username_error),this@LoginActivity)
                    loading!!.visibility = View.GONE
                    btn_login!!.visibility = View.VISIBLE
                }
            }
        }
    }



    private fun gotoDashboard(email: String){
        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
        intent.putExtra("name", email)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

}