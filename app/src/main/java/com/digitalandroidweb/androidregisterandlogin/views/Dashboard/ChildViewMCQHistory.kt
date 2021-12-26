package com.digitalandroidweb.androidregisterandlogin.views.Dashboard

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.model.McqHistory
import com.digitalandroidweb.androidregisterandlogin.util.General
import com.digitalandroidweb.androidregisterandlogin.views.MCQTest.MCQTest
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View

@Layout(R.layout.mcq_history_child)
class ChildViewMCQHistory(mContext: Context, mcqHistory: McqHistory) {
    @View(R.id.tv_date_txt)
    var tvDateTxt: TextView? = null
    @View(R.id.tv_status_txt)
    var tvStatusTxt: TextView? = null

    @View(R.id.tv_score_txt)
    var tvScoreTxt: TextView? = null


    private val mContext: Context
    private val mcqHistory: McqHistory

    init {
        Log.d(ChildView::class.simpleName, "init: ")
        this.mcqHistory = mcqHistory
        this.mContext = mContext
    }

    @Resolve
    private fun onResolve() {
        Log.d(ChildView::class.simpleName, "onResolve: ")
        tvDateTxt?.text = General.getFormatedDateHistory(mcqHistory.date)
        tvScoreTxt?.text = mcqHistory.score
        tvStatusTxt?.text = mcqHistory.status

    }



}