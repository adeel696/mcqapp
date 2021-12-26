package com.digitalandroidweb.androidregisterandlogin.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.adapter.PaymentAdapter
import com.digitalandroidweb.androidregisterandlogin.model.Subscription
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.General
import kotlinx.android.synthetic.main.fragment_offers.*
import kotlinx.android.synthetic.main.fragment_subscriptions.*
import kotlinx.android.synthetic.main.fragment_subscriptions.loading
import kotlinx.android.synthetic.main.fragment_subscriptions.tv_time_spend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class Subscriptions : Fragment() {

    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    lateinit var mContext: Context

    private lateinit var adapter: SubscriptionAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(Subscriptions::class.simpleName, "onAttach: ")
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscriptions, container, false)
    }


    override fun onResume() {
        super.onResume()
        Log.d(Subscriptions::class.simpleName, "onResume: ")
        callApi()
        val mLayoutManager = LinearLayoutManager(requireContext())
        rv_subscription.layoutManager = mLayoutManager
    }

    private fun callApi() {
        Log.d(Subscriptions::class.simpleName, "callApi: ")
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getSubscriptionList(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(Subscriptions::class.simpleName, " Success: ${response.body()}")
                    val subscriptionList = response.body() as ArrayList<Subscription>
                    Log.d(PaymentHistory::class.simpleName, "callApi: ${subscriptionList.size}")
                    coroutineScope.launch(Dispatchers.Main) {
                        adapter = SubscriptionAdapter (subscriptionList, mContext)
                        rv_subscription.adapter = adapter
                        loading.visibility = View.GONE
                    }
                }else{
                    Log.d(Subscriptions::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(Subscriptions::class.simpleName, "callLoginApi: Exception ${e.message} ")
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

    inner class SubscriptionAdapter (private val subscriptionList: ArrayList<Subscription>, val context: Context) :
            RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.subscription_item, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return subscriptionList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val subscription = subscriptionList[position]
            try {
                holder.mcqType.text = subscription.mcqTypeName
                holder.date.text = subscription.expire
                holder.status.text = subscription.status
            } catch (e: Exception) {
                Log.d(PaymentAdapter::class.simpleName, "onBindViewHolder: ${e.message}")
            }


        }

        //the class is holding the list view
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mcqType = itemView.findViewById(R.id.tv_mcqType) as TextView
            val status = itemView.findViewById(R.id.tv_status) as TextView
            val date = itemView.findViewById(R.id.tv_date) as TextView
        }


    }


}