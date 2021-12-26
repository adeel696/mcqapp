package com.digitalandroidweb.androidregisterandlogin.views.History

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitalandroidweb.androidregisterandlogin.LoginActivity
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.RegisterActivity
import com.digitalandroidweb.androidregisterandlogin.model.GetMcqHistoryResponse
import com.digitalandroidweb.androidregisterandlogin.model.GetMcqResponse
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.General
import com.digitalandroidweb.androidregisterandlogin.views.Dashboard.ChildView
import com.digitalandroidweb.androidregisterandlogin.views.Dashboard.ChildViewMCQHistory
import com.digitalandroidweb.androidregisterandlogin.views.Dashboard.HeaderView
import com.digitalandroidweb.androidregisterandlogin.views.Dashboard.MCQTypeList
import com.digitalandroidweb.androidregisterandlogin.views.Subscriptions
import kotlinx.android.synthetic.main.fragment_m_c_q_history.*
import kotlinx.android.synthetic.main.fragment_m_c_q_history.loading
import kotlinx.android.synthetic.main.fragment_m_c_q_history.tv_time_spend
import kotlinx.android.synthetic.main.fragment_subscriptions.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.ArrayList

class MCQHistory : Fragment() {

    var myView: View? = null

    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(MCQHistory::class.simpleName, "onAttach: ")
        this.mContext = context
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(MCQHistory::class.simpleName, "onCreateView: ")
        myView = inflater.inflate(R.layout.fragment_m_c_q_history, null, false)
        return myView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(MCQHistory::class.simpleName, "onActivityCreated: ")
        callApi()
        val mLayoutManager = LinearLayoutManager(requireContext())
        rv_mcqHistory.layoutManager = mLayoutManager
    }


    private fun callApi() {
        Log.d(MCQHistory::class.simpleName, "callApi: ")
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getMCQHistoryList(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(MCQHistory::class.simpleName, " Success: ${response.body()}")
                    val mcqHistoryList = response.body() as ArrayList<GetMcqHistoryResponse>
                    coroutineScope.launch(Dispatchers.Main) {
                        if (mcqHistoryList.isNotEmpty()) {
                            Log.d(MCQHistory::class.simpleName, "callApi: ${mcqHistoryList.size}")
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
                            Log.d(MCQHistory::class.simpleName, "callApi: MCQList is Empty...")
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
                    Log.d(Subscriptions::class.simpleName, " Success: ${response.body()}")
                    val time = response.body() as String
                    Log.d(Subscriptions::class.simpleName, "callTimeApi: ${time}")
                    coroutineScope.launch(Dispatchers.Main) {
                        tv_time_spend.text = time
                    }
                }else{
                    Log.d(Subscriptions::class.simpleName, "callTime Api Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(Subscriptions::class.simpleName, "callTime Api: Exception ${e.message} ")
            }
        }

    }


}