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
import com.digitalandroidweb.androidregisterandlogin.LoginActivity
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.RegisterActivity
import com.digitalandroidweb.androidregisterandlogin.adapter.PaymentAdapter
import com.digitalandroidweb.androidregisterandlogin.model.GetMcqHistoryResponse
import com.digitalandroidweb.androidregisterandlogin.model.GetMcqResponse
import com.digitalandroidweb.androidregisterandlogin.model.PointOfSale
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.General
import com.digitalandroidweb.androidregisterandlogin.views.Dashboard.*
import kotlinx.android.synthetic.main.fragment_offers.*
import kotlinx.android.synthetic.main.fragment_payment_history.*
import kotlinx.android.synthetic.main.fragment_payment_history.loading
import kotlinx.android.synthetic.main.fragment_payment_history.rv_mcqHistoryD
import kotlinx.android.synthetic.main.fragment_payment_history.rv_point_sale
import kotlinx.android.synthetic.main.fragment_payment_history.tv_footer
import kotlinx.android.synthetic.main.fragment_payment_history.tv_point_sale
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class Trainings : Fragment() {
    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    lateinit var mContext: Context
    private lateinit var adapter: PaymentAdapter
    private var textVisibility = false
    private var onlineVisibility = false
    private lateinit var pointOfSaleAdapter: PointOfSaleAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(Trainings::class.simpleName, "onAttach: ")
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(Trainings::class.simpleName, "onCreate: ")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(Trainings::class.simpleName, "onCreateView: ")
        return inflater.inflate(R.layout.fragment_payment_history, container, false)
    }

    private fun callApi() {
        Log.d(Trainings::class.simpleName, "callapi: ")
        loading.visibility = View.VISIBLE

//        coroutineScope.launch {
//            try {
//                val retrofitService = RetrofitClient.GetService()
//                val response = retrofitService.getMCQHistoryList(General.addHeaders(mContext, true))
//                if(response.isSuccessful && response.body()!=null){
//                    Log.d(Home::class.simpleName, " Success: ${response.body()}")
//                    val mcqHistoryList = response.body() as java.util.ArrayList<GetMcqHistoryResponse>
//                    coroutineScope.launch(Dispatchers.Main) {
//                        if (mcqHistoryList.isNotEmpty()) {
//                            Log.d(Home::class.simpleName, "callApi: ${mcqHistoryList.size}")
//                            for (mcqHistory in mcqHistoryList) {
//                                rv_mcqHistoryD.addView(HeaderView(mContext, mcqHistory.mcqName))
//                                if (mcqHistory.mcqHistory.isNotEmpty()) {
//                                    for (mqDetail in mcqHistory.mcqHistory) {
//                                        rv_mcqHistoryD.addView(ChildViewMCQHistory(mContext, mqDetail))
//                                    }
//                                }
//                            }
//                            loading.visibility = View.GONE
//                        } else{
//                            Log.d(Home::class.simpleName, "callApi: MCQList is Empty...")
//                            loading.visibility = View.GONE
//                            rv_mcqHistoryD.visibility = View.GONE
//                        }
//                    }
//                }else{
//                    Log.d(LoginActivity::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
//                }
//            } catch (e: Exception) {
//                Log.d(LoginActivity::class.simpleName, "callLoginApi: Exception ${e.message} ")
//            }
//        }

        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getMCQsList(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(RegisterActivity::class.simpleName, " Success: ${response.body()}")
                    val mcqList = response.body() as java.util.ArrayList<GetMcqResponse>
                    Log.d(Trainings::class.simpleName, "callApi: ${mcqList.size}")
                    withContext(Dispatchers.Main) {
                        for(mcq in mcqList){
                            rv_mcqHistoryD.addView(HeaderViewTraining(mContext," ${mcq.mcqType} \n${mcq.description}"))
                            if(mcq.mcqDetail.isNotEmpty()) {
                                for(mqDetail in mcq.mcqDetail) {
                                    rv_mcqHistoryD.addView(ChildViewTraining(mContext, mqDetail,mcq.amount.toInt(),mcq.id.toInt()))
                                }
                            }else{
                                Log.d(Trainings::class.simpleName, "callApi: detail is empty..")
                                rv_mcqHistoryD.addView(ChildViewTraining(mContext, null,mcq.amount.toInt(),mcq.id.toInt()))
                            }
                        }
                        loading.visibility = View.GONE
                    }
                }else{
                    Log.d(Trainings::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(Trainings::class.simpleName, "callLoginApi: Exception ${e.message} ")
            }
        }


        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getTrainingList(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    withContext(Dispatchers.Main) {
                        Log.d(Trainings::class.simpleName, " Success: ${response.body()}")
                        val pointOfSaleList = response.body() as ArrayList<PointOfSale>
                        Log.d(Documents::class.simpleName, "callApi: ${pointOfSaleList.size}")
                        pointOfSaleAdapter = PointOfSaleAdapter(pointOfSaleList, mContext)
                        rv_point_sale.adapter = pointOfSaleAdapter
                        loading.visibility = View.GONE
                    }
                }else{
                    Log.d(Trainings::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Log.d(Trainings::class.simpleName, "callLoginApi: Exception ${e.message} ")
                withContext(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(Trainings::class.simpleName, "onViewCreated: ")
        val year = Calendar.getInstance()[Calendar.YEAR]
        tv_footer.text = getString(R.string.copyright_text, year.toString())
        callApi()
        tv_training_type.setOnClickListener {
            Log.d(Trainings::class.simpleName, "onViewCreated: ")
            if(textVisibility){
                textVisibility = false
                tv_point_sale.visibility = View.GONE
                rv_point_sale.visibility = View.GONE
            }else{
                if(onlineVisibility){
                    onlineVisibility = false
                    rv_mcqHistoryD.visibility = View.GONE
                }
                textVisibility = true
                tv_point_sale.visibility = View.VISIBLE
                rv_point_sale.visibility = View.VISIBLE
            }
        }
        tv_training_online.setOnClickListener {
            Log.d(Trainings::class.simpleName, "onViewCreated: ")
            if(onlineVisibility){
                onlineVisibility = false
                rv_mcqHistoryD.visibility = View.GONE
            }else{
                if(textVisibility){
                    textVisibility = false
                    tv_point_sale.visibility = View.GONE
                    rv_point_sale.visibility = View.GONE
                }
                onlineVisibility = true
                rv_mcqHistoryD.visibility = View.VISIBLE
            }
        }
        val mLayoutManager = LinearLayoutManager(requireContext())
        rv_point_sale.layoutManager = mLayoutManager
        val mLayoutManager1 = LinearLayoutManager(requireContext())
        rv_mcqHistoryD.layoutManager = mLayoutManager1
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(Trainings::class.simpleName, "onActivityCreated: ")
    }


    inner class PointOfSaleAdapter (private val offerList: ArrayList<PointOfSale>, val context: Context) :
            RecyclerView.Adapter<PointOfSaleAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.point_sale_item, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return offerList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val offer = offerList[position]
            try {
                holder.city.text = offer.city
                holder.name.text = offer.name
                holder.address.text = offer.address
                holder.phone1.text = offer.phone_number_1
                holder.phone2.text = offer.phone_number_2
                holder.phone3.text = offer.phone_number_3
            } catch (e: Exception) {
                Log.d(PaymentAdapter::class.simpleName, "onBindViewHolder: ${e.message}")
            }


        }

        //the class is holding the list view
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val city = itemView.findViewById(R.id.tv_city) as TextView
            val name = itemView.findViewById(R.id.tv_name) as TextView
            val address = itemView.findViewById(R.id.tv_address) as TextView
            val phone1 = itemView.findViewById(R.id.tv_phone1) as TextView
            val phone2 = itemView.findViewById(R.id.tv_phone2) as TextView
            val phone3 = itemView.findViewById(R.id.tv_phone3) as TextView
        }


    }




}