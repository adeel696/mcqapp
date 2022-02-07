package com.digitalandroidweb.androidregisterandlogin.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.adapter.PaymentAdapter
import com.digitalandroidweb.androidregisterandlogin.model.ContactRequest
import com.digitalandroidweb.androidregisterandlogin.model.Subscription
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.General
import kotlinx.android.synthetic.main.fragment_subscriptions.*
import kotlinx.coroutines.*
import java.util.*


class ContactUs : Fragment() {

    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    lateinit var mContext: Context

    private lateinit var adapter: SubscriptionAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(ContactUs::class.simpleName, "onAttach: ")
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(ContactUs::class.simpleName, "onViewCreated: ")
        val year = Calendar.getInstance()[Calendar.YEAR]
        tv_footer.text = getString(R.string.copyright_text, year.toString())

        btn_submit.setOnClickListener {
            Log.d(ContactUs::class.simpleName, "onViewCreated: ")
            val name = edt_name!!.text.toString().trim { it <= ' ' }
            val email = edt_email!!.text.toString().trim { it <= ' ' }
            val message = edt_message!!.text.toString().trim { it <= ' ' }

            if (name.isEmpty()) {
                this.edt_name!!.error = getString(R.string.name_error)
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                this.edt_email!!.error = getString(R.string.email_error)
                return@setOnClickListener
            }

            if (message.isEmpty()) {
                this.edt_message!!.error = getString(R.string.empty_message)
                return@setOnClickListener
            }

            loading!!.visibility = View.VISIBLE
            btn_submit!!.visibility = View.GONE

            coroutineScope.launch {
                try {
                    val contactRequest = ContactRequest(name, email, message)
                    val retrofitService = RetrofitClient.GetService()
                    val response = retrofitService.contact(General.addHeaders(mContext, true), contactRequest)
                    if(response.isSuccessful && response.body()!=null){
                        Log.d(ContactUs::class.simpleName, " Success: ${response.body()}")
                        withContext(Dispatchers.Main) {
                            loading!!.visibility = View.GONE
                            btn_submit!!.visibility = View.VISIBLE
                            Toast.makeText(mContext, getString(R.string.message_success), Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Log.d(ContactUs::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                        withContext(Dispatchers.Main)  {
                            General.showAlterDialog(getString(R.string.error), getString(R.string.went_wrong_error), mContext)
                            loading!!.visibility = View.GONE
                            btn_submit!!.visibility = View.VISIBLE
                        }
                    }
                } catch (e: Exception) {
                    Log.d(ContactUs::class.simpleName, "callLoginApi: Exception ${e.message} ")
                    withContext(Dispatchers.Main)  {
                        General.showAlterDialog(getString(R.string.error), getString(R.string.went_wrong_error), mContext)
                        loading!!.visibility = View.GONE
                        btn_submit!!.visibility = View.VISIBLE
                    }
                }
            }


        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(ContactUs::class.simpleName, "onResume: ")
        //callApi()
//        val mLayoutManager = LinearLayoutManager(requireContext())
//        rv_subscription.layoutManager = mLayoutManager
    }

    private fun callApi() {
        Log.d(ContactUs::class.simpleName, "callApi: ")
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getSubscriptionList(General.addHeaders(mContext, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(ContactUs::class.simpleName, " Success: ${response.body()}")
                    val subscriptionList = response.body() as ArrayList<Subscription>
                    Log.d(ContactUs::class.simpleName, "callApi: ${subscriptionList.size}")
                    coroutineScope.launch(Dispatchers.Main) {
                        adapter = SubscriptionAdapter(subscriptionList, mContext)
                        rv_subscription.adapter = adapter
                        loading.visibility = View.GONE
                    }
                }else{
                    Log.d(ContactUs::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(ContactUs::class.simpleName, "callLoginApi: Exception ${e.message} ")
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
                    coroutineScope.launch(Dispatchers.Main) {
                        tv_time_spend.text = time
                    }
                }else{
                    Log.d(ContactUs::class.simpleName, "callTime Api Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(ContactUs::class.simpleName, "callTime Api: Exception ${e.message} ")
            }
        }
    }

    inner class SubscriptionAdapter(private val subscriptionList: ArrayList<Subscription>, val context: Context) :
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