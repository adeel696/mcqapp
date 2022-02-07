package com.digitalandroidweb.androidregisterandlogin.views.Dashboard

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
import com.digitalandroidweb.androidregisterandlogin.model.GetMcqHistoryResponse
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.General
import com.digitalandroidweb.androidregisterandlogin.views.ContactUs
import kotlinx.android.synthetic.main.fragment_m_c_q_history2.*
import kotlinx.android.synthetic.main.fragment_m_c_q_history2.rv_mcqHistory
import kotlinx.coroutines.*
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [MCQHistory.newInstance] factory method to
 * create an instance of this fragment.
 */
class MCQHistory : Fragment() {
    // TODO: Rename and change types of parameters
    private var mcqTypeId: String = ""

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mcqTypeId = it.getString(ARG_PARAM1)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_m_c_q_history2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(MCQHistory::class.simpleName, "onViewCreated: ")
        callApi()
        val mLayoutManager = LinearLayoutManager(requireContext())
        rv_mcqHistory.layoutManager = mLayoutManager
    }

    private fun callApi() {
        Log.d(MCQHistory::class.simpleName, "callApi: ")
        loading.visibility = View.VISIBLE
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getMCQHistoryList(General.addHeaders(mContext, true),mcqTypeId.toInt())
                if(response.isSuccessful && response.body()!=null){
                    Log.d(MCQHistory::class.simpleName, " Success: ${response.body()}")
                    val mcqHistoryList = response.body() as ArrayList<GetMcqHistoryResponse>
                    withContext(Dispatchers.Main) {
                        if (mcqHistoryList.isNotEmpty()) {
                            tv_no_history.visibility = View.GONE
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
                    withContext(Dispatchers.Main){
                        loading.visibility = View.GONE
                        rv_mcqHistory.visibility = View.GONE
                        tv_no_history.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.d(LoginActivity::class.simpleName, "callLoginApi: Exception ${e.message} ")
                withContext(Dispatchers.Main){
                    loading.visibility = View.GONE
                    rv_mcqHistory.visibility = View.GONE
                }
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
                    withContext(Dispatchers.Main) {
                        //tv_time_spend.text = time
                    }
                }else{
                    Log.d(ContactUs::class.simpleName, "callTime Api Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(ContactUs::class.simpleName, "callTime Api: Exception ${e.message} ")
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
         * @return A new instance of fragment MCQHistory.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                MCQHistory().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                    }
                }
    }
}