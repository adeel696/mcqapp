package com.digitalandroidweb.androidregisterandlogin.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.RegisterActivity
import com.digitalandroidweb.androidregisterandlogin.adapter.PaymentAdapter
import com.digitalandroidweb.androidregisterandlogin.model.Offer
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants.Companion.STATUS_SUBSCRIBED
import com.digitalandroidweb.androidregisterandlogin.util.General
import kotlinx.android.synthetic.main.fragment_offers.*
import kotlinx.android.synthetic.main.fragment_offers.loading
import kotlinx.android.synthetic.main.fragment_offers.tv_time_spend
import kotlinx.android.synthetic.main.fragment_payment_history.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [Offers.newInstance] factory method to
 * create an instance of this fragment.
 */
class Offers : Fragment() {


    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    lateinit var mContext: Context
    private lateinit var adapter: OfferAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(Offers::class.simpleName, "onAttach: ")
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(Offers::class.simpleName, "onViewCreated: ")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(Offers::class.simpleName, "onActivityCreated: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(Offers::class.simpleName, "onResume: ")
//        callApi()
//        val mLayoutManager = LinearLayoutManager(requireContext())
//        rv_offers.layoutManager = mLayoutManager
    }

    private fun callApi() {
        Log.d(Offers::class.simpleName, "callApi: ")
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getOfferList(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(RegisterActivity::class.simpleName, " Success: ${response.body()}")
                    val offerList = response.body() as ArrayList<Offer>
                    Log.d(Offers::class.simpleName, "callApi: ${offerList.size}")
                    coroutineScope.launch(Dispatchers.Main) {
                        adapter = OfferAdapter(offerList, mContext)
                        rv_offers.adapter = adapter
                        loading.visibility = View.GONE
                    }
                }else{
                    Log.d(Offers::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(Offers::class.simpleName, "callLoginApi: Exception ${e.message} ")
            }
        }

        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getTimeSpend(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(Offers::class.simpleName, " Success: ${response.body()}")
                    val time = response.body() as String
                    Log.d(Offers::class.simpleName, "callTimeApi: ${time}")
                    coroutineScope.launch(Dispatchers.Main) {
                        tv_time_spend.text = time
                    }
                }else{
                    Log.d(Offers::class.simpleName, "callTime Api Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(Offers::class.simpleName, "callTime Api: Exception ${e.message} ")
            }
        }
    }


    inner class OfferAdapter (private val offerList: java.util.ArrayList<Offer>, val context: Context) :
            RecyclerView.Adapter<OfferAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.offer_item, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return offerList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val offer = offerList[position]
            try {
                holder.mcqType.text = offer.mcqTypeName
                if(offer.subscription==STATUS_SUBSCRIBED){
                    holder.btnSubscribe.visibility = View.INVISIBLE
                }
                holder.btnSubscribe.setOnClickListener {
                    Log.d(OfferAdapter::class.simpleName, "onBindViewHolder: Go to Subscription Screen.${offer.id}")
                    activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.addToBackStack(null)
                            ?.replace(R.id.content_frame, PaymentScreen.newInstance(offer.id,offer.amount))
                            ?.commit()
                }
            } catch (e: Exception) {
                Log.d(PaymentAdapter::class.simpleName, "onBindViewHolder: ${e.message}")
            }


        }

        //the class is holding the list view
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mcqType = itemView.findViewById(R.id.tv_mcqType) as TextView
            val btnSubscribe = itemView.findViewById(R.id.btn_subscribe) as Button
        }


    }



}