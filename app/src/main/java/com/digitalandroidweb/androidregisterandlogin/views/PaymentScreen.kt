package com.digitalandroidweb.androidregisterandlogin.views

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.digitalandroidweb.androidregisterandlogin.LoginActivity
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.RegisterActivity
import com.digitalandroidweb.androidregisterandlogin.model.LoginResponse
import com.digitalandroidweb.androidregisterandlogin.model.PaymentRequest
import com.digitalandroidweb.androidregisterandlogin.model.PaymentResponse
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.General
import com.digitalandroidweb.androidregisterandlogin.views.Dashboard.MCQTypeList
import kotlinx.android.synthetic.main.fragment_payment_screen.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PaymentScreen : Fragment() {
    private var mcq_type_id: Int = 0
    private var mcq_amount: Int = 0
    lateinit var mContext: Context

    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mcq_type_id = it.getInt(ARG_PARAM1)
            mcq_amount = it.getInt(ARG_PARAM2)
            Log.d(PaymentScreen::class.simpleName, "onCreate: $mcq_amount")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(MCQTypeList::class.simpleName, "onAttach: ")
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_screen, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(PaymentScreen::class.simpleName, "onActivityCreated: ")
        group_payment_selection.visibility = View.VISIBLE
        group_payment.visibility = View.GONE
        tv_payment_detail.text = getString(R.string.please_dial, mcq_amount.toString())
        btn_payment_select.setOnClickListener {
            Log.d(PaymentScreen::class.simpleName, "onActivityCreated: Payment Mode Selected...")
            group_payment_selection.visibility = View.GONE
            group_payment.visibility = View.VISIBLE
        }
        btn_submit.setOnClickListener {
            Log.d(PaymentScreen::class.simpleName, "onActivityCreated: Button clicked..$mcq_type_id")
            val number = edt_number.text.toString().trim()
            val otp = edt_otp.text.toString().trim()
            if (number.isEmpty()){
                edt_number.setError(getString(R.string.number_error))
                return@setOnClickListener
            }
            if(otp.isEmpty()) {
            edt_otp.setError(getString(R.string.otp_error))
            return@setOnClickListener
            }
            submit(number, otp)
        }
    }

    private fun submit(number: String, otp: String) {
        Log.d(PaymentScreen::class.simpleName, "submit: $number - $otp")
        btn_submit.visibility = View.GONE
        loading!!.visibility = View.VISIBLE
        coroutineScope.launch {
            try {
                val paymentRequest = PaymentRequest(number, "1000", otp, mcq_type_id)
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.addPayment(General.addHeaders(mContext, true), paymentRequest)
                if(response.isSuccessful && response.body()!=null){
                    Log.d(RegisterActivity::class.simpleName, " Success: ${response.body()}")
                    coroutineScope.launch(Dispatchers.Main) {
                        val response = response.body() as PaymentResponse
                        loading!!.visibility = View.GONE
                        btn_submit.visibility = View.VISIBLE
                        if(response.success){
                            AlertDialog.Builder(context)
                                    .setTitle(getString(R.string.payment_success))
                                    .setMessage(response.message)
                                    .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                                        // Continue with delete operation
                                        activity?.supportFragmentManager?.popBackStack()
                                    })
                                    .setIcon(R.drawable.logo)
                                    .show()
                        }else{
                            Log.d(PaymentScreen::class.simpleName, "submit: Payment Fail")
                            AlertDialog.Builder(context)
                                    .setTitle(getString(R.string.payment_fail))
                                    .setMessage(response.message)
                                    .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                                        // Continue with delete operation
                                        activity?.supportFragmentManager?.popBackStack()
                                    })
                                    .setIcon(R.drawable.logo)
                                    .show()
                        }
                    }
                }else{
                    Log.d(LoginActivity::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                    coroutineScope.launch(Dispatchers.Main) {
                        loading!!.visibility = View.GONE
                        btn_submit.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.d(LoginActivity::class.simpleName, "callLoginApi: Exception ${e.message} ")
                coroutineScope.launch(Dispatchers.Main) {
                    loading!!.visibility = View.GONE
                    btn_submit.visibility = View.VISIBLE
                }
            }
        }

    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(id: Int, amount: Int) =
                PaymentScreen().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, id)
                        putInt(ARG_PARAM2, amount)
                    }
                }
    }
}