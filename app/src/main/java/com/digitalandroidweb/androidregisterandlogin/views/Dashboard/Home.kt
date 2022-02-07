package com.digitalandroidweb.androidregisterandlogin.views.Dashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.digitalandroidweb.androidregisterandlogin.LoginActivity
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.RegisterActivity
import com.digitalandroidweb.androidregisterandlogin.model.GetMcqHistoryResponse
import com.digitalandroidweb.androidregisterandlogin.model.GetMcqResponse
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.General
import kotlinx.android.synthetic.main.primer_fragment.*
import kotlinx.android.synthetic.main.primer_fragment.loading
import kotlinx.android.synthetic.main.primer_fragment.tv_footer
import kotlinx.android.synthetic.main.primer_fragment.tv_time_spend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


class Home : Fragment() {
    var myView: View? = null

    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)
    lateinit var mContext: Context


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(Home::class.simpleName, "onAttach: ")
        this.mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(Home::class.simpleName, "onViewCreated: ")
        val year = Calendar.getInstance()[Calendar.YEAR]
        tv_footer.text = getString(R.string.copyright_text, year.toString())
//        val mLayoutManager = LinearLayoutManager(requireContext())
//        rv_mcqType.layoutManager = mLayoutManager
        //callApi()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(Home::class.simpleName, "onActivityCreated: ")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(Home::class.simpleName, "onCreateView: ")
        myView = inflater.inflate(R.layout.primer_fragment, container, false)
        return myView
    }

    private fun callApi() {
        Log.d(Home::class.simpleName, "callApi: ")

        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getMCQHistoryList(General.addHeaders(mContext, true),1)
                if(response.isSuccessful && response.body()!=null){
                    Log.d(Home::class.simpleName, " Success: ${response.body()}")
                    val mcqHistoryList = response.body() as ArrayList<GetMcqHistoryResponse>
                    coroutineScope.launch(Dispatchers.Main) {
                        if (mcqHistoryList.isNotEmpty()) {
                            Log.d(Home::class.simpleName, "callApi: ${mcqHistoryList.size}")
                            for (mcqHistory in mcqHistoryList) {
                                rv_mcqHistoryD.addView(HeaderView(mContext, mcqHistory.mcqName))
                                if (mcqHistory.mcqHistory.isNotEmpty()) {
                                    for (mqDetail in mcqHistory.mcqHistory) {
                                        rv_mcqHistoryD.addView(ChildViewMCQHistory(mContext, mqDetail))
                                    }
                                }
                            }
                            loading.visibility = View.GONE
                        } else{
                            Log.d(Home::class.simpleName, "callApi: MCQList is Empty...")
                            loading.visibility = View.GONE
                            rv_mcqHistoryD.visibility = View.GONE
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
                val response = retrofitService.getMCQsList(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(RegisterActivity::class.simpleName, " Success: ${response.body()}")
                    val mcqList = response.body() as ArrayList<GetMcqResponse>
                    Log.d(Home::class.simpleName, "callApi: ${mcqList.size}")
                    coroutineScope.launch(Dispatchers.Main) {
                        for(mcq in mcqList){
                            rv_mcqType.addView(HeaderView(mContext,mcq.mcqType))
                            if(mcq.mcqDetail.isNotEmpty()) {
                                for(mqDetail in mcq.mcqDetail) {
                                    rv_mcqType.addView(ChildView(mContext, mqDetail))
                                }
                            }
                        }
                        loading.visibility = View.GONE
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
                    Log.d(Home::class.simpleName, " Success: ${response.body()}")
                    val time = response.body() as String
                    Log.d(Home::class.simpleName, "callTimeApi: ${time}")
                    coroutineScope.launch(Dispatchers.Main) {
                        tv_time_spend.text = time
                    }
                }else{
                    Log.d(Home::class.simpleName, "callTime Api Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(Home::class.simpleName, "callTime Api: Exception ${e.message} ")
            }
        }


    }

    override fun onResume() {
        super.onResume()
//        val mLayoutManager = LinearLayoutManager(requireContext())
//        rv_mcqHistoryD.layoutManager = mLayoutManager
    }

}
