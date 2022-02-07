package com.digitalandroidweb.androidregisterandlogin.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.RegisterActivity
import com.digitalandroidweb.androidregisterandlogin.adapter.PaymentAdapter
import com.digitalandroidweb.androidregisterandlogin.model.DocYear
import com.digitalandroidweb.androidregisterandlogin.model.PointOfSale
import com.digitalandroidweb.androidregisterandlogin.model.YearPublication
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.General
import kotlinx.android.synthetic.main.fragment_offers.*
import kotlinx.android.synthetic.main.fragment_offers.loading
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Use the [Documents.newInstance] factory method to
 * create an instance of this fragment.
 */
class Documents : Fragment() {


    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    lateinit var mContext: Context
    private lateinit var adapter: OfferAdapter
    private lateinit var docYearAdapter: DocYearAdapter
    private lateinit var pointOfSaleAdapter: PointOfSaleAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(Documents::class.simpleName, "onAttach: ")
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
        Log.d(Documents::class.simpleName, "onViewCreated: ")
        val year = Calendar.getInstance()[Calendar.YEAR]
        tv_footer.text = getString(R.string.copyright_text, year.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(Documents::class.simpleName, "onActivityCreated: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(Documents::class.simpleName, "onResume: ")
        callApi()
        val mLayoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        rv_years.layoutManager = mLayoutManager
        val mLayoutManager1 = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        rv_years_desc.layoutManager = mLayoutManager1
        val mLayoutManager2 = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        rv_point_sale.layoutManager = mLayoutManager2
    }

    private fun callApi() {
        Log.d(Documents::class.simpleName, "callApi: ")
        loading.visibility = View.VISIBLE
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getYearsList(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(RegisterActivity::class.simpleName, " Success: ${response.body()}")
                    val offerList = response.body() as ArrayList<YearPublication>
                    Log.d(Documents::class.simpleName, "callApi: ${offerList.size}")
                    callYearDoc(offerList.get(0).year.toInt())
                    withContext(Dispatchers.Main) {
                        adapter = OfferAdapter(offerList, mContext)
                        rv_years.adapter = adapter
                    }
                }else{
                    Log.d(Documents::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Log.d(Documents::class.simpleName, "callLoginApi: Exception ${e.message} ")
                withContext(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }
            }
        }


        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getPointOfSale(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(RegisterActivity::class.simpleName, " Success: ${response.body()}")
                    val pointOfSaleList = response.body() as ArrayList<PointOfSale>
                    Log.d(Documents::class.simpleName, "callApi: ${pointOfSaleList.size}")
                    withContext(Dispatchers.Main) {
                        pointOfSaleAdapter = PointOfSaleAdapter(pointOfSaleList, mContext)
                        rv_point_sale.adapter = pointOfSaleAdapter
                    }
                }else{
                    Log.d(Documents::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Log.d(Documents::class.simpleName, "callLoginApi: Exception ${e.message} ")
                withContext(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }
            }
        }

//        coroutineScope.launch {
//            try {
//                val retrofitService = RetrofitClient.GetService()
//                val response = retrofitService.getTimeSpend(General.addHeaders(mContext, true))
//                if(response.isSuccessful && response.body()!=null){
//                    Log.d(Offers::class.simpleName, " Success: ${response.body()}")
//                    val time = response.body() as String
//                    Log.d(Offers::class.simpleName, "callTimeApi: ${time}")
//                    coroutineScope.launch(Dispatchers.Main) {
//                        tv_time_spend.text = time
//                    }
//                }else{
//                    Log.d(Offers::class.simpleName, "callTime Api Fail: ${response.errorBody()}")
//                }
//            } catch (e: Exception) {
//                Log.d(Offers::class.simpleName, "callTime Api: Exception ${e.message} ")
//            }
//        }
    }

    private fun callYearDoc(docId:Int){
        Log.d(Documents::class.simpleName, "callYearDoc: ")
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getYearDoc(General.addHeaders(mContext, true),docId)
                if(response.isSuccessful && response.body()!=null){
                    Log.d(RegisterActivity::class.simpleName, " Success: ${response.body()}")
                    val docYears = response.body() as ArrayList<DocYear>
                    Log.d(Documents::class.simpleName, "callApi: ${docYears.size}")

                    withContext(Dispatchers.Main) {
                        docYearAdapter = DocYearAdapter(docYears, mContext)
                        rv_years_desc.adapter = docYearAdapter
                        loading.visibility = View.GONE
                    }
                }else{
                    Log.d(Documents::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                    withContext(Dispatchers.Main) {
                        loading.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Log.d(Documents::class.simpleName, "callLoginApi: Exception ${e.message} ")
                withContext(Dispatchers.Main) {
                    loading.visibility = View.GONE
                }
            }
        }
    }


    inner class OfferAdapter (private val offerList: ArrayList<YearPublication>, val context: Context) :
            RecyclerView.Adapter<OfferAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.year_item, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return offerList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val offer = offerList[position]
            try {
                holder.year.text = offer.year
                holder.year.setOnClickListener {
                    Log.d(OfferAdapter::class.simpleName, "onBindViewHolder: ${offer.year}")
                    callYearDoc(offer.year.toInt())
                }
//                holder.mcqType.text = offer.mcqTypeName
//                if(offer.subscription==STATUS_SUBSCRIBED){
//                    holder.btnSubscribe.visibility = View.INVISIBLE
//                }
//                holder.btnSubscribe.setOnClickListener {
//                    Log.d(OfferAdapter::class.simpleName, "onBindViewHolder: Go to Subscription Screen.${offer.id}")
//                    activity?.supportFragmentManager
//                            ?.beginTransaction()
//                            ?.addToBackStack(null)
//                            ?.replace(R.id.content_frame, PaymentScreen.newInstance(offer.id,offer.amount))
//                            ?.commit()
//                }
            } catch (e: Exception) {
                Log.d(PaymentAdapter::class.simpleName, "onBindViewHolder: ${e.message}")
            }


        }

        //the class is holding the list view
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val year = itemView.findViewById(R.id.tv_year) as TextView
            //val btnSubscribe = itemView.findViewById(R.id.btn_subscribe) as Button
        }


    }


    inner class DocYearAdapter (private val offerList: ArrayList<DocYear>, val context: Context) :
            RecyclerView.Adapter<DocYearAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.year_desc_item, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return offerList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val offer = offerList[position]
            try {
                holder.imageTitle.text = offer.year
                holder.imageDesc.text = offer.description
                Glide.with(mContext).load(offer.image).into(holder.image)
            } catch (e: Exception) {
                Log.d(PaymentAdapter::class.simpleName, "onBindViewHolder: ${e.message}")
            }


        }

        //the class is holding the list view
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageTitle = itemView.findViewById(R.id.tv_image_title) as TextView
            val imageDesc = itemView.findViewById(R.id.tv_image_desc) as TextView
            val image = itemView.findViewById(R.id.iv_image) as ImageView
            //val btnSubscribe = itemView.findViewById(R.id.btn_subscribe) as Button
        }


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