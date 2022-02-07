package com.digitalandroidweb.androidregisterandlogin.views.Dashboard

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.adapter.PaymentAdapter
import com.digitalandroidweb.androidregisterandlogin.model.McqDetail
import com.digitalandroidweb.androidregisterandlogin.model.McqHistory
import com.digitalandroidweb.androidregisterandlogin.model.PointOfSale
import com.digitalandroidweb.androidregisterandlogin.views.Documents
import com.digitalandroidweb.androidregisterandlogin.views.MCQTest.MCQTest
import com.digitalandroidweb.androidregisterandlogin.views.PaymentScreen
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View
import kotlinx.android.synthetic.main.app_bar_principal.*
import kotlinx.android.synthetic.main.fragment_offers.*

@Layout(R.layout.training_child)
class ChildViewTraining(mContext: Context, mcqDetail: McqDetail?,amount:Int,id:Int) {
    @View(R.id.tv_mcqName)
    var tvMCQName: TextView? = null

    @View(R.id.btn_run)
    var btnRun: Button? = null

    @View(R.id.btn_renew)
    var btnRenew: Button? = null

    @View(R.id.tv_questions)
    var tvQuestions: TextView? = null

    @View(R.id.tv_mcq_history)
    var tvMcqHistory: TextView? = null

    @View(R.id.tv_payment)
    var tvPayment: TextView? = null


    private val mContext: Context
    private val mcqDetail: McqDetail?
    private var id: Int = 0
    private var amount: Int = 0


    init {
        Log.d(ChildView::class.simpleName, "init: ")
        this.mcqDetail = mcqDetail
        this.mContext = mContext
        this.id = id
        this.amount = amount
    }

    @Resolve
    private fun onResolve() {
        Log.d(ChildViewTraining::class.simpleName, "onResolve: ")


        if(mcqDetail == null){
            Log.d(ChildViewTraining::class.simpleName, "onResolve: ")
            tvMCQName?.visibility = android.view.View.GONE
            tvQuestions?.visibility = android.view.View.GONE
            btnRenew!!.visibility = android.view.View.GONE
            btnRun!!.text = mContext.getString(R.string.subscribe_traing)
        }else{
            tvMCQName?.visibility = android.view.View.VISIBLE
            tvQuestions?.visibility = android.view.View.VISIBLE
            btnRenew!!.visibility = android.view.View.VISIBLE
            tvMCQName?.text = mcqDetail?.mcqName
            tvQuestions?.text = mcqDetail?.question.toString()
            btnRun!!.text = mContext.getString(R.string.run)
        }

        btnRun!!.setOnClickListener {
            if(mcqDetail==null){
                Log.d(ChildViewTraining::class.simpleName, "onResolve: Subscribe Go to Payment...")
                gotoPaymentScreen()
            }else{
                Log.d(ChildViewTraining::class.simpleName, "onResolve: ${mcqDetail.id}")
                mContext.startActivity(Intent(mContext, MCQTest::class.java).putExtra("mcqId",mcqDetail.id))
            }
        }

        btnRenew!!.setOnClickListener {
            Log.d(ChildViewTraining::class.simpleName, "onResolve: Go to Payment.. ")
            gotoPaymentScreen()
        }

        tvMcqHistory?.setOnClickListener {
            Log.d(ChildViewTraining::class.simpleName, "onResolve: Show Mcq History...")
            (mContext as DashboardActivity).supportFragmentManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.content_frame, MCQHistory.newInstance(id.toString()))
                    .commit()
        }

        tvPayment?.setOnClickListener {
            Log.d(ChildViewTraining::class.simpleName, "onResolve: Show Payment...")
            (mContext as DashboardActivity).supportFragmentManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.content_frame, PaymentHistory.newInstance(id.toString()))
                    .commit()
        }


    }

    private fun gotoPaymentScreen() {
        Log.d(ChildViewTraining::class.simpleName, "gotoPaymentScreen: ")
            (mContext as DashboardActivity).supportFragmentManager
                            .beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.content_frame, PaymentScreen.newInstance(id,amount))
                            .commit()
    }


    companion object {
        private const val TAG = "ChildView"
    }


}