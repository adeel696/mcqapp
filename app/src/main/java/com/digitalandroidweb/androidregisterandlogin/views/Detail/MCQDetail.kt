package com.digitalandroidweb.androidregisterandlogin.views.Detail

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.digitalandroidweb.androidregisterandlogin.LoginActivity
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.RegisterActivity
import com.digitalandroidweb.androidregisterandlogin.adapter.PaymentAdapter
import com.digitalandroidweb.androidregisterandlogin.model.DetailMCQResponse
import com.digitalandroidweb.androidregisterandlogin.model.MCQAnswers
import com.digitalandroidweb.androidregisterandlogin.model.Offer
import com.digitalandroidweb.androidregisterandlogin.model.PaymentRequest
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants
import com.digitalandroidweb.androidregisterandlogin.util.General
import com.digitalandroidweb.androidregisterandlogin.views.MCQTest.MCQTest
import io.github.kexanie.library.MathView
import kotlinx.android.synthetic.main.activity_m_c_q_detail.*
import kotlinx.android.synthetic.main.activity_m_c_q_detail.loading
import kotlinx.android.synthetic.main.activity_m_c_q_detail.tv_score
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class MCQDetail : AppCompatActivity() {
    var sessionId = 0
    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private lateinit var adapter: DetailAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_m_c_q_detail)
        if(intent!=null && intent.hasExtra("sessionId")){
            sessionId = intent.getIntExtra("sessionId",0)
            Log.d(MCQDetail::class.simpleName, "onCreate: $sessionId")
        }
        val mLayoutManager = LinearLayoutManager(this@MCQDetail)
        rv_detail.layoutManager = mLayoutManager
        callDetailMCQApi()
    }

    private fun callDetailMCQApi() {
        Log.d(MCQDetail::class.simpleName, "callDetailMCQApi: ")
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.getQuestionDetail(sessionId,General.addHeaders(this@MCQDetail, true))
                if(response.isSuccessful && response.body()!=null){
                    Log.d(RegisterActivity::class.simpleName, " Success: ${response.body()}")
                    val mcqDetail = response.body() as DetailMCQResponse
                    Log.d(MCQDetail::class.simpleName, "callDetailMCQApi: ${mcqDetail.info_McqsScore.correct}")
                    coroutineScope.launch(Dispatchers.Main) {
                        adapter = DetailAdapter(mcqDetail.info_McqsAnswer, this@MCQDetail)
                        rv_detail.adapter = adapter
                        loading!!.visibility = View.GONE
                        tv_test_name.text = getString(R.string.name_detail)+": "+mcqDetail.info_McqsAnswer.get(0).mcqName
                        tv_score.text = getString(R.string.score)+": "+mcqDetail.info_McqsScore.correct+"/"+mcqDetail.info_McqsScore.total
                    }
                }else{
                    Log.d(LoginActivity::class.simpleName, "callDetailMCQApi Fail: ${response.errorBody()}")
                    coroutineScope.launch(Dispatchers.Main) {
                        loading!!.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Log.d(LoginActivity::class.simpleName, "callDetailMCQApi: Exception ${e.message} ")
                coroutineScope.launch(Dispatchers.Main) {
                    loading!!.visibility = View.GONE
                }
            }
        }

    }


    inner class DetailAdapter (private val offerList: List<MCQAnswers>, val context: Context) :
            RecyclerView.Adapter<DetailAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.detail_item, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return offerList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val mcqAnswer = offerList[position]
            try {
                Log.d(DetailAdapter::class.simpleName, "onBindViewHolder: ")
                if(mcqAnswer.isCorrect == "1"){
                    Log.d(DetailAdapter::class.simpleName, "onBindViewHolder: Answer is Correct Hide Cross arrows")
                    holder.iv_cross1.visibility = View.GONE
                    holder.iv_cross2.visibility = View.GONE
                    holder.iv_cross3.visibility = View.GONE
                    holder.iv_cross4.visibility = View.GONE
                    if(mcqAnswer.correctAnswer == "1"){
                        holder.iv_tick1.visibility = View.VISIBLE
                        holder.iv_tick2.visibility = View.GONE
                        holder.iv_tick3.visibility = View.GONE
                        holder.iv_tick4.visibility = View.GONE
                    }else if(mcqAnswer.correctAnswer == "2"){
                        holder.iv_tick1.visibility = View.GONE
                        holder.iv_tick2.visibility = View.VISIBLE
                        holder.iv_tick3.visibility = View.GONE
                        holder.iv_tick4.visibility = View.GONE
                    }else if(mcqAnswer.correctAnswer == "3"){
                        holder.iv_tick1.visibility = View.GONE
                        holder.iv_tick2.visibility = View.GONE
                        holder.iv_tick3.visibility = View.VISIBLE
                        holder.iv_tick4.visibility = View.GONE
                    }else if (mcqAnswer.correctAnswer == "4"){
                        holder.iv_tick1.visibility = View.GONE
                        holder.iv_tick2.visibility = View.GONE
                        holder.iv_tick3.visibility = View.GONE
                        holder.iv_tick4.visibility = View.VISIBLE
                    }
                }else{
                    if(mcqAnswer.correctAnswer == "1"){
                        holder.iv_tick1.visibility = View.VISIBLE
                        holder.iv_tick2.visibility = View.GONE
                        holder.iv_tick3.visibility = View.GONE
                        holder.iv_tick4.visibility = View.GONE

                    }else if(mcqAnswer.correctAnswer == "2"){
                        holder.iv_tick1.visibility = View.GONE
                        holder.iv_tick2.visibility = View.VISIBLE
                        holder.iv_tick3.visibility = View.GONE
                        holder.iv_tick4.visibility = View.GONE

                    }else if(mcqAnswer.correctAnswer == "3"){
                        holder.iv_tick1.visibility = View.GONE
                        holder.iv_tick2.visibility = View.GONE
                        holder.iv_tick3.visibility = View.VISIBLE
                        holder.iv_tick4.visibility = View.GONE

                    }else if (mcqAnswer.correctAnswer == "4"){
                        holder.iv_tick1.visibility = View.GONE
                        holder.iv_tick2.visibility = View.GONE
                        holder.iv_tick3.visibility = View.GONE
                        holder.iv_tick4.visibility = View.VISIBLE
                    }


                    if(mcqAnswer.userAnser == "1"){

                        holder.iv_cross1.visibility = View.VISIBLE
                        holder.iv_cross2.visibility = View.GONE
                        holder.iv_cross3.visibility = View.GONE
                        holder.iv_cross4.visibility = View.GONE
                    }else if(mcqAnswer.userAnser == "2"){

                        holder.iv_cross1.visibility = View.GONE
                        holder.iv_cross2.visibility = View.VISIBLE
                        holder.iv_cross3.visibility = View.GONE
                        holder.iv_cross4.visibility = View.GONE
                    }else if(mcqAnswer.userAnser == "3"){

                        holder.iv_cross1.visibility = View.GONE
                        holder.iv_cross2.visibility = View.GONE
                        holder.iv_cross3.visibility = View.VISIBLE
                        holder.iv_cross4.visibility = View.GONE
                    }else if (mcqAnswer.userAnser == "4"){

                        holder.iv_cross1.visibility = View.GONE
                        holder.iv_cross2.visibility = View.GONE
                        holder.iv_cross3.visibility = View.GONE
                        holder.iv_cross4.visibility = View.VISIBLE
                    }
                }
                if (mcqAnswer.is_image_question == 1) {
                    holder.iv_question.visibility = View.VISIBLE
                    holder.tv_question.visibility = View.VISIBLE
                    val arrItems = mcqAnswer.question.split("[SPLIT]")
                    if(arrItems.isNotEmpty()){
                        if(arrItems.size == 3){
                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Parse Formula as well..")
                            if (arrItems.get(0).contains("IMG")){
                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                                val img = arrItems.get(0).split("IMG")
                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                                val options: RequestOptions = RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.logo)
                                        .error(R.drawable.logo)

                                Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_question)
                                val formulas = arrItems.get(1).split("FORMULA")
                                if(formulas.isNotEmpty()){
                                    holder.tv_question.text = img.get(0) + formulas.get(0)+ formulas.get(1)
                                }else{
                                    holder.tv_question.text = img.get(0)
                                }
                            }else {
                                val img = arrItems.get(1).split("IMG")
                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                val options: RequestOptions = RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.logo)
                                        .error(R.drawable.logo)

                                Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_question)
                                val formulas = arrItems.get(0).split("FORMULA")
                                if(formulas.isNotEmpty()){
                                    holder.tv_question.text = img.get(0) + formulas.get(0)+ formulas.get(1)
                                }else{
                                    holder.tv_question.text = img.get(0)
                                }
                            }
                        }else {
                            if (arrItems.get(0).contains("IMG")){
                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                                val img = arrItems.get(0).split("IMG")
                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                                val options: RequestOptions = RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.logo)
                                        .error(R.drawable.logo)

                                Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_question)
                                holder.tv_question.text = img.get(0)
                            }else {
                                val img = arrItems.get(1).split("IMG")
                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                val options: RequestOptions = RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.logo)
                                        .error(R.drawable.logo)

                                Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_question)
                            }
                        }
                    }else{
                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                    }
                } else {
                    holder.iv_question.visibility = View.GONE
                    holder.tv_question.visibility = View.VISIBLE
                    holder.tv_question.text = mcqAnswer.question
                }

                if (mcqAnswer.is_image_Option1 == 1) {
                    val arrItems = mcqAnswer.option1.split("[SPLIT]")
                    if(arrItems.isNotEmpty()){
                        if(arrItems.get(0).contains("IMG")){
                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                            val img =  arrItems.get(0).split("IMG")
                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                            val options: RequestOptions = RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.logo)
                                    .error(R.drawable.logo)

                            Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_option1)
                        }else{
                            val img =  arrItems.get(1).split("IMG")
                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                            val options: RequestOptions = RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.logo)
                                    .error(R.drawable.logo)

                            Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_option1)
                        }
                    }else{
                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                    }
                } else {
                    holder.option1.text = mcqAnswer.option1
                    holder.iv_option1.visibility = View.GONE
                }
                if (mcqAnswer.is_image_Option2 == 1) {
                    val arrItems = mcqAnswer.option2.split("[SPLIT]")
                    if(arrItems.isNotEmpty()){
                        if(arrItems.size == 3){
                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Parse Formula as well..")
                            if (arrItems.get(0).contains("IMG")){
                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                                val img = arrItems.get(0).split("IMG")
                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                                val options: RequestOptions = RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.logo)
                                        .error(R.drawable.logo)

                                Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_question)
                                val formulas = arrItems.get(1).split("FORMULA")
                                if(formulas.isNotEmpty()){
                                    holder.tv_question.text = img.get(0) + formulas.get(0)+ formulas.get(1)
                                }else{
                                    holder.tv_question.text = img.get(0)
                                }
                            }else {
                                val img = arrItems.get(1).split("IMG")
                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                val options: RequestOptions = RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.logo)
                                        .error(R.drawable.logo)

                                Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_question)
                                val formulas = arrItems.get(0).split("FORMULA")
                                if(formulas.isNotEmpty()){
                                    holder.tv_question.text = img.get(0) + formulas.get(0)+ formulas.get(1)
                                }else{
                                    holder.tv_question.text = img.get(0)
                                }
                            }
                        }else {
                            if (arrItems.get(0).contains("IMG")) {
                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                                val img = arrItems.get(0).split("IMG")
                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                                val options: RequestOptions = RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.logo)
                                        .error(R.drawable.logo)

                                Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_option2)
                            } else {
                                val img = arrItems.get(1).split("IMG")
                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                val options: RequestOptions = RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.logo)
                                        .error(R.drawable.logo)

                                Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_option2)
                            }
                        }
                    }else{
                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                    }
                } else {
                    holder.option2.text = mcqAnswer.option2
                    holder.iv_option2.visibility = View.GONE
                }
                if (mcqAnswer.is_image_Option3 == 1) {
                    val arrItems = mcqAnswer.option3.split("[SPLIT]")
                    if(arrItems.isNotEmpty()){
                        if(arrItems.get(0).contains("IMG")){
                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                            val img =  arrItems.get(0).split("IMG")
                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                            val options: RequestOptions = RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.logo)
                                    .error(R.drawable.logo)

                            Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_option3)
                        }else{
                            val img =  arrItems.get(1).split("IMG")
                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                            val options: RequestOptions = RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.logo)
                                    .error(R.drawable.logo)

                            Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_option3)
                        }
                    }else{
                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                    }
                } else {
                    holder.option3.text = mcqAnswer.option3
                    holder.iv_option3.visibility = View.GONE
                }
                if (mcqAnswer.is_image_Option4 == 1) {
                    val arrItems = mcqAnswer.option4.split("[SPLIT]")
                    if(arrItems.isNotEmpty()){
                        if(arrItems.get(0).contains("IMG")){
                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                            val img =  arrItems.get(0).split("IMG")
                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                            val options: RequestOptions = RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.logo)
                                    .error(R.drawable.logo)

                            Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_option4)
                        }else{
                            val img =  arrItems.get(1).split("IMG")
                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                            val options: RequestOptions = RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.logo)
                                    .error(R.drawable.logo)

                            Glide.with(this@MCQDetail).load(img.get(1)).apply(options).into(holder.iv_option4)
                        }
                    }else{
                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                    }
                } else {
                    holder.option4.text = mcqAnswer.option4
                    holder.iv_option4.visibility = View.GONE
                }

            } catch (e: Exception) {
                Log.d(PaymentAdapter::class.simpleName, "onBindViewHolder: ${e.message}")
            }


        }

        //the class is holding the list view
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val option1 = itemView.findViewById(R.id.tv_option1) as MathView
            val iv_option1 = itemView.findViewById(R.id.iv_option1) as ImageView
            val option2 = itemView.findViewById(R.id.tv_option2) as MathView
            val iv_option2 = itemView.findViewById(R.id.iv_option2) as ImageView
            val option3 = itemView.findViewById(R.id.tv_option3) as MathView
            val iv_option3 = itemView.findViewById(R.id.iv_option3) as ImageView
            val option4 = itemView.findViewById(R.id.tv_option4) as MathView
            val iv_option4 = itemView.findViewById(R.id.iv_option4) as ImageView
            val iv_question = itemView.findViewById(R.id.iv_question) as ImageView
            val tv_question = itemView.findViewById(R.id.tv_question) as MathView

            val iv_tick1 = itemView.findViewById(R.id.iv_tick1) as ImageView
            val iv_tick2 = itemView.findViewById(R.id.iv_tick2) as ImageView
            val iv_tick3 = itemView.findViewById(R.id.iv_tick3) as ImageView
            val iv_tick4 = itemView.findViewById(R.id.iv_tick4) as ImageView

            val iv_cross1 = itemView.findViewById(R.id.iv_cross1) as ImageView
            val iv_cross2 = itemView.findViewById(R.id.iv_cross2) as ImageView
            val iv_cross3 = itemView.findViewById(R.id.iv_cross3) as ImageView
            val iv_cross4 = itemView.findViewById(R.id.iv_cross4) as ImageView

        }


    }

}