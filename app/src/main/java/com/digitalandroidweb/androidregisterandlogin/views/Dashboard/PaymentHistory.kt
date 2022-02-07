package com.digitalandroidweb.androidregisterandlogin.views.Dashboard

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
import com.digitalandroidweb.androidregisterandlogin.views.Trainings
import kotlinx.android.synthetic.main.fragment_payment_history2.*
import kotlinx.android.synthetic.main.fragment_payment_history2.rv_paymentHistory
import kotlinx.coroutines.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [PaymentHistory.newInstance] factory method to
 * create an instance of this fragment.
 */
class PaymentHistory : Fragment() {
    // TODO: Rename and change types of parameters
    private var mcqTypeId: String = ""

    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    lateinit var mContext: Context
    private lateinit var adapter: PaymentAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(Trainings::class.simpleName, "onAttach: ")
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mcqTypeId = it.getString(ARG_PARAM1)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_history2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(PaymentHistory::class.simpleName, "onViewCreated: ")
        callapi()
        val mLayoutManager = LinearLayoutManager(requireContext())
        rv_paymentHistory.layoutManager = mLayoutManager
    }

    private fun callapi() {
        Log.d(PaymentHistory::class.simpleName, "callapi: ")
        loading.visibility = View.VISIBLE
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getPaymentHistory(General.addHeaders(mContext, true),mcqTypeId.toInt())
                if(response.isSuccessful && response.body()!=null){
                    Log.d(RegisterActivity::class.simpleName, " Success: ${response.body()}")
                    val paymentList = response.body() as ArrayList<Payment>
                    withContext(Dispatchers.Main) {
                        if(paymentList.isNotEmpty()) {
                            tv_no_payment.visibility = View.GONE
                            Log.d(PaymentHistory::class.simpleName, "callApi: ${paymentList.size}")
                            adapter = PaymentAdapter(paymentList, mContext)
                            rv_paymentHistory.adapter = adapter
                            loading.visibility = View.GONE
                        }else{
                            Log.d(PaymentHistory::class.simpleName, "callapi: Payment List is Empty..")
                            rv_paymentHistory.visibility = View.GONE
                            loading.visibility = View.GONE
                            tv_no_payment.visibility = View.VISIBLE
                        }
                    }
                }else{
                    Log.d(PaymentHistory::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                    withContext(Dispatchers.Main) {
                        rv_paymentHistory.visibility = View.GONE
                        loading.visibility = View.GONE
                        tv_no_payment.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.d(PaymentHistory::class.simpleName, "callLoginApi: Exception ${e.message} ")
                withContext(Dispatchers.Main) {
                    rv_paymentHistory.visibility = View.GONE
                    loading.visibility = View.GONE
                    tv_no_payment.visibility = View.VISIBLE
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PaymentHistory.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                PaymentHistory().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                    }
                }
    }
}