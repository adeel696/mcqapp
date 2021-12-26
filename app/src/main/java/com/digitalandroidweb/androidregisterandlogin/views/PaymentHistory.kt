package com.digitalandroidweb.androidregisterandlogin.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.RegisterActivity
import com.digitalandroidweb.androidregisterandlogin.adapter.PaymentAdapter
import com.digitalandroidweb.androidregisterandlogin.model.Payment
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.General
import com.digitalandroidweb.androidregisterandlogin.views.Dashboard.MCQTypeList
import kotlinx.android.synthetic.main.fragment_payment_history.*
import kotlinx.android.synthetic.main.fragment_payment_history.loading
import kotlinx.android.synthetic.main.fragment_payment_history.tv_no_payment
import kotlinx.android.synthetic.main.fragment_payment_history.tv_time_spend
import kotlinx.android.synthetic.main.primer_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PaymentHistory : Fragment() {
    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    lateinit var mContext: Context
    private lateinit var adapter: PaymentAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(PaymentHistory::class.simpleName, "onAttach: ")
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(PaymentHistory::class.simpleName, "onCreate: ")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(PaymentHistory::class.simpleName, "onCreateView: ")
        return inflater.inflate(R.layout.fragment_payment_history, container, false)
    }

    private fun callapi() {
        Log.d(PaymentHistory::class.simpleName, "callapi: ")
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getPaymentHistory(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(RegisterActivity::class.simpleName, " Success: ${response.body()}")
                    val paymentList = response.body() as ArrayList<Payment>
                    coroutineScope.launch(Dispatchers.Main) {
                        if(paymentList.isNotEmpty()) {
                            Log.d(PaymentHistory::class.simpleName, "callApi: ${paymentList.size}")
                            adapter = PaymentAdapter(paymentList, mContext)
                            rv_paymentHistory.adapter = adapter
                            loading.visibility = View.GONE
                        }else{
                            Log.d(PaymentHistory::class.simpleName, "callapi: Payment List is Empty..")
                            rv_paymentHistory.visibility = View.GONE
                            tv_no_payment.visibility = View.VISIBLE
                            loading.visibility = View.GONE
                        }
                    }
                }else{
                    Log.d(PaymentHistory::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(PaymentHistory::class.simpleName, "callLoginApi: Exception ${e.message} ")
            }
        }

        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getTimeSpend(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(PaymentHistory::class.simpleName, " Success: ${response.body()}")
                    val time = response.body() as String
                    Log.d(PaymentHistory::class.simpleName, "callTimeApi: ${time}")
                    coroutineScope.launch(Dispatchers.Main) {
                        tv_time_spend.text = time
                    }
                }else{
                    Log.d(PaymentHistory::class.simpleName, "callTime Api Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(PaymentHistory::class.simpleName, "callTime Api: Exception ${e.message} ")
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(PaymentHistory::class.simpleName, "onActivityCreated: ")
        callapi()
        val mLayoutManager = LinearLayoutManager(requireContext())
        rv_paymentHistory.layoutManager = mLayoutManager

    }




}