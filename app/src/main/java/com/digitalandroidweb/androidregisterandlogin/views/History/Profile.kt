package com.digitalandroidweb.androidregisterandlogin.views.History

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.digitalandroidweb.androidregisterandlogin.LoginActivity
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.RegisterActivity
import com.digitalandroidweb.androidregisterandlogin.model.GetMcqHistoryResponse
import com.digitalandroidweb.androidregisterandlogin.model.ProfileRequest
import com.digitalandroidweb.androidregisterandlogin.model.RegisterResponse
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.General
import com.digitalandroidweb.androidregisterandlogin.util.SharedPreference
import com.digitalandroidweb.androidregisterandlogin.views.ContactUs
import com.digitalandroidweb.androidregisterandlogin.views.Dashboard.ChildViewMCQHistory
import com.digitalandroidweb.androidregisterandlogin.views.Dashboard.HeaderView
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.fragment_m_c_q_history.*
import kotlinx.android.synthetic.main.fragment_m_c_q_history.loading
import kotlinx.android.synthetic.main.fragment_m_c_q_history.tv_footer
import kotlinx.android.synthetic.main.fragment_m_c_q_history.tv_time_spend
import kotlinx.android.synthetic.main.primer_fragment.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*


class Profile : Fragment() {

    var myView: View? = null

    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)
    lateinit var mContext: Context
    var encodedImage = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(Profile::class.simpleName, "onAttach: ")
        this.mContext = context
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(Profile::class.simpleName, "onCreateView: ")
        myView = inflater.inflate(R.layout.fragment_m_c_q_history, container, false)
        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(Profile::class.simpleName, "onViewCreated: ")
        val year = Calendar.getInstance()[Calendar.YEAR]
        tv_footer.text = getString(R.string.copyright_text, year.toString())
        val name = SharedPreference.getName(mContext)
        edt_name.setText(name)
        btn_image.setOnClickListener {
            Log.d(Profile::class.simpleName, "onViewCreated: ")
            ImagePicker.with(this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
        }
        btn_submit.setOnClickListener {
            Log.d(Profile::class.simpleName, "onViewCreated: ")
            val name = edt_name.text.toString().trim { it <= ' ' }
            val password = edt_password.text.toString().trim { it <= ' ' }
            val confirmPassword = confirm_password.text.toString().trim { it <= ' ' }

            if (name.isEmpty()) {
                this.edt_name.error = getString(R.string.name_error)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                edt_password.error = getString(R.string.password_error)
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                confirm_password.error = getString(R.string.confirm_pass_error)
                return@setOnClickListener
            }
            if (!password.equals(confirmPassword, ignoreCase = true)) {
                edt_password.error = getString(R.string.pass_confirm_error)
                return@setOnClickListener
            }

            if(encodedImage == ""){
                Log.d(Profile::class.simpleName, "onViewCreated: Image is Empty...")
                Toast.makeText(mContext, "Please Select Image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loading!!.visibility = View.VISIBLE
            btn_submit.visibility = View.GONE

            coroutineScope.launch {
                try {
                    val profileRequest = ProfileRequest(name,encodedImage,password)
                    val retrofitService = RetrofitClient.GetService()
                    val response = retrofitService.editProfile(General.addHeaders(mContext, true),profileRequest)
                    if(response.isSuccessful && response.body()!=null){
                        Log.d(RegisterActivity::class.simpleName, " Success: ${response.body()}")
                        val registerResponse = response.body() as RegisterResponse
                        withContext(Dispatchers.Main) {
                            loading!!.visibility = View.GONE
                            btn_submit!!.visibility = View.VISIBLE
                            Toast.makeText(mContext,registerResponse.message,Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Log.d(LoginActivity::class.simpleName, "callEditProfile Fail: ${response.errorBody()}")
                        withContext(Dispatchers.Main) {
                            General.showAlterDialog(getString(R.string.error), getString(R.string.went_wrong_error), mContext)
                            loading!!.visibility = View.GONE
                            btn_submit!!.visibility = View.VISIBLE
                        }
                    }
                } catch (e: Exception) {
                    Log.d(LoginActivity::class.simpleName, "callEditProfile: Exception ${e.message} ")
                    withContext(Dispatchers.Main) {
                        General.showAlterDialog(getString(R.string.error), getString(R.string.went_wrong_error), mContext)
                        loading!!.visibility = View.GONE
                        btn_submit!!.visibility = View.VISIBLE
                    }
                }
            }



        }
//        callApi()
//        val mLayoutManager = LinearLayoutManager(requireContext())
//        rv_mcqHistory.layoutManager = mLayoutManager
    }


    private fun callApi() {
        Log.d(Profile::class.simpleName, "callApi: ")
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getMCQHistoryList(General.addHeaders(mContext, true),1)
                if(response.isSuccessful && response.body()!=null){
                    Log.d(Profile::class.simpleName, " Success: ${response.body()}")
                    val mcqHistoryList = response.body() as ArrayList<GetMcqHistoryResponse>
                    coroutineScope.launch(Dispatchers.Main) {
                        if (mcqHistoryList.isNotEmpty()) {
                            Log.d(Profile::class.simpleName, "callApi: ${mcqHistoryList.size}")
                            for (mcqHistory in mcqHistoryList) {
                                rv_mcqHistory.addView(HeaderView(mContext, mcqHistory.mcqName))
                                if (mcqHistory.mcqHistory.isNotEmpty()) {
                                    for (mqDetail in mcqHistory.mcqHistory) {
                                        rv_mcqHistory.addView(ChildViewMCQHistory(mContext, mqDetail))
                                    }
                                }
                            }
                            loading.visibility = View.GONE
                        } else{
                            Log.d(Profile::class.simpleName, "callApi: MCQList is Empty...")
                            loading.visibility = View.GONE
                            rv_mcqHistory.visibility = View.GONE
                            tv_no_history.visibility = View.VISIBLE
                        }
                    }
                }else{
                    Log.d(LoginActivity::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(LoginActivity::class.simpleName, "callLoginApi: Exception ${e.message} ")
            }
        }


        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getTimeSpend(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(ContactUs::class.simpleName, " Success: ${response.body()}")
                    val time = response.body() as String
                    Log.d(ContactUs::class.simpleName, "callTimeApi: ${time}")
                    coroutineScope.launch(Dispatchers.Main) {
                        tv_time_spend.text = time
                    }
                }else{
                    Log.d(ContactUs::class.simpleName, "callTime Api Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(ContactUs::class.simpleName, "callTime Api: Exception ${e.message} ")
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!

            // Use Uri object instead of File to avoid storage permissions
            iv_preview.setImageURI(uri)
            val imageStream: InputStream = mContext.contentResolver.openInputStream(uri)!!
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            encodedImage = encodeImage(selectedImage)
            Log.d(Profile::class.simpleName, "onActivityResult: $encodedImage")
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(mContext, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(mContext, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }


}