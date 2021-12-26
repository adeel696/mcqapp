package com.digitalandroidweb.androidregisterandlogin.views.Dashboard

import android.content.Context
import android.content.Intent
import android.graphics.Movie
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.digitalandroidweb.androidregisterandlogin.LoginActivity
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.RegisterActivity
import com.digitalandroidweb.androidregisterandlogin.model.GetMcqResponse
import com.digitalandroidweb.androidregisterandlogin.model.McqDetail
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.General
import com.digitalandroidweb.androidregisterandlogin.views.MCQTest.MCQTest
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View
import kotlinx.android.synthetic.main.primer_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.ArrayList


@Layout(R.layout.mcq_type_child)
class ChildView(mContext: Context, mcqDetail: McqDetail) {
    @View(R.id.tv_mcqName)
    var tvMCQName: TextView? = null

    @View(R.id.tv_questions)
    var tvQuestions: TextView? = null
    private val mContext: Context
    private val mcqDetail: McqDetail

    init {
        Log.d(ChildView::class.simpleName, "init: ")
        this.mcqDetail = mcqDetail
        this.mContext = mContext
    }

    @Resolve
    private fun onResolve() {
        Log.d(ChildView::class.simpleName, "onResolve: ")
        tvMCQName?.text = mcqDetail.mcqName
        tvQuestions?.text = mcqDetail.question.toString()

        tvMCQName!!.setOnClickListener {
            Log.d(ChildView::class.simpleName, "onResolve: ${mcqDetail.id}")
            mContext.startActivity(Intent(mContext, MCQTest::class.java).putExtra("mcqId",mcqDetail.id))
        }
    }



    companion object {
        private const val TAG = "ChildView"
    }


}