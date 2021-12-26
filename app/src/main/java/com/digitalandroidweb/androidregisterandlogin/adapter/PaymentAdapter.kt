package com.digitalandroidweb.androidregisterandlogin.adapter

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.model.Payment
import com.digitalandroidweb.androidregisterandlogin.util.General
import java.util.ArrayList

class PaymentAdapter (private val paymentList: ArrayList<Payment>, val context: Context) :
        RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.payment_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val payment = paymentList[position]
        try {
            holder.msisdn.text = payment.msisdn
            holder.subscribe.text = payment.subscribe
            holder.amount.text = payment.amount
            holder.status.text = payment.status
            holder.date.text = General.getFormatedDateResults(payment.date)
        } catch (e: Exception) {
            Log.d(PaymentAdapter::class.simpleName, "onBindViewHolder: ${e.message}")
        }


    }

    //the class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date = itemView.findViewById(R.id.tv_date) as TextView
        val msisdn = itemView.findViewById(R.id.tv_msisdn) as TextView
        val subscribe = itemView.findViewById(R.id.tv_subscribe) as TextView
        val amount = itemView.findViewById(R.id.tv_amount) as TextView
        val status = itemView.findViewById(R.id.tv_status) as TextView

    }


}

