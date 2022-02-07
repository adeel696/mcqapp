package com.digitalandroidweb.androidregisterandlogin.views.Dashboard

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.digitalandroidweb.androidregisterandlogin.R
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View
import com.mindorks.placeholderview.annotations.expand.Collapse
import com.mindorks.placeholderview.annotations.expand.Expand
import com.mindorks.placeholderview.annotations.expand.Parent
import com.mindorks.placeholderview.annotations.expand.SingleTop

@Parent
@SingleTop
@Layout(R.layout.training_item)
class HeaderViewTraining(context: Context, headerText: String) {
    @View(R.id.tv_training_type)
    var headerText: TextView? = null
    private val mContext: Context = context
    private val mHeaderText: String = headerText

    @Resolve
    private fun onResolve() {
        Log.d(HeaderView::class.simpleName, "onResolve: ")
        headerText!!.text = mHeaderText
    }

    @Expand
    private fun onExpand() {
        Log.d(HeaderView::class.simpleName, "onExpand: $mHeaderText")
    }

    @Collapse
    private fun onCollapse() {
        Log.d(HeaderView::class.simpleName, "onCollapse:$mHeaderText ")
    }

    companion object {
        private const val TAG = "HeaderView"
    }

}