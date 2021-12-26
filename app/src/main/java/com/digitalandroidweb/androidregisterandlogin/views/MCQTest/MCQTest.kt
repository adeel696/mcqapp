package com.digitalandroidweb.androidregisterandlogin.views.MCQTest

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.digitalandroidweb.androidregisterandlogin.R
import com.digitalandroidweb.androidregisterandlogin.model.NextQuestionResponse
import com.digitalandroidweb.androidregisterandlogin.model.OpenMCQResponse
import com.digitalandroidweb.androidregisterandlogin.network.RetrofitClient
import com.digitalandroidweb.androidregisterandlogin.util.General
import com.digitalandroidweb.androidregisterandlogin.views.Dashboard.ChildView
import com.digitalandroidweb.androidregisterandlogin.views.Detail.MCQDetail
import kotlinx.android.synthetic.main.activity_m_c_q_test.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MCQTest : AppCompatActivity() {

    // Create a Coroutine scope using a job to be able to cancel when needed
    var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)
    var mcqId = 0
    var sessionId = 0
    var questionId = 0
    var answerNo = 1
    var timeInSeconds = 5
    var startTime = 0L
    var endTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_m_c_q_test)
        if(intent!=null){
            mcqId = intent.getIntExtra("mcqId", 0)
            Log.d(MCQTest::class.simpleName, "onCreate: MCQId =  $mcqId")
            question_view.visibility = View.GONE
            rb_option1.isChecked = true
            tv_score.visibility = View.GONE
            btn_detail.visibility = View.GONE
        }



        tv_time.setOnChronometerTickListener {
            val time: Long = SystemClock.elapsedRealtime() - it.getBase()
            val h = (time / 3600000).toInt()
            val m = (time - h * 3600000).toInt() / 60000
            val s = (time - h * 3600000 - m * 60000).toInt() / 1000
            //it.setText(String.format("%02d:%02d:%02d", h, m, s))
            val t = (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
            it.setText(t)
        }

        btn_start.setOnClickListener {
            Log.d(MCQTest::class.simpleName, "onCreate: ")
            callOpenMCQApi(mcqId)
            it.visibility = View.GONE
            tv_time.start()
            startTime = System.currentTimeMillis()
        }

        btn_detail.setOnClickListener {
            Log.d(MCQTest::class.simpleName, "onCreate: Show Detail Screen...")
            finish()
            startActivity(Intent(this@MCQTest,MCQDetail::class.java).putExtra("sessionId",sessionId))
        }

        btn_next.setOnClickListener {
            endTime = System.currentTimeMillis()
            callNextQuestionApi()
        }

        rb_option1.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                answerNo = 1
                rb_option2.isChecked = false
                rb_option3.isChecked = false
                rb_option4.isChecked = false
            }
        }

        rb_option2.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                answerNo = 2
                rb_option1.isChecked = false
                rb_option3.isChecked = false
                rb_option4.isChecked = false
            }
        }

        rb_option3.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                answerNo = 3
                rb_option1.isChecked = false
                rb_option2.isChecked = false
                rb_option4.isChecked = false
            }
        }

        rb_option4.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                answerNo = 4
                rb_option1.isChecked = false
                rb_option2.isChecked = false
                rb_option3.isChecked = false
            }
        }


    }

    private fun callNextQuestionApi() {
        val timeInSeconds = endTime - startTime
        Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Seconds = ${(timeInSeconds / 1000.0).toInt()} ")
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val responseApi = retrofitService.getNextQuestion(General.addHeaders(this@MCQTest, true), mcqId, sessionId, questionId, answerNo, (timeInSeconds / 1000.0).toInt())
                if (responseApi.isSuccessful && responseApi.body() != null) {
                    val nextQuestionResponse = responseApi.body() as NextQuestionResponse
                    if (nextQuestionResponse.more == 1) {
                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Continue Fetching Questions ")
                        questionId = nextQuestionResponse.id
                        coroutineScope.launch(Dispatchers.Main) {
                            question_view.visibility = View.VISIBLE
                            startTime = System.currentTimeMillis()
                            if (nextQuestionResponse.is_image_question == 1) {
                                iv_question.visibility = View.VISIBLE
                                tv_question.visibility = View.VISIBLE
                                val arrItems = nextQuestionResponse.question.split("[SPLIT]")
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

                                            Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_question)
                                            val formulas = arrItems.get(1).split("FORMULA")
                                            if(formulas.isNotEmpty()){
                                            tv_question.text = img.get(0) + formulas.get(0)+ formulas.get(1)
                                            }else{
                                                tv_question.text = img.get(0)
                                            }
                                        }else {
                                            val img = arrItems.get(1).split("IMG")
                                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                            val options: RequestOptions = RequestOptions()
                                                    .centerCrop()
                                                    .placeholder(R.drawable.logo)
                                                    .error(R.drawable.logo)

                                            Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_question)
                                            val formulas = arrItems.get(0).split("FORMULA")
                                            if(formulas.isNotEmpty()){
                                                tv_question.text = img.get(0) + formulas.get(0)+ formulas.get(1)
                                            }else{
                                                tv_question.text = img.get(0)
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

                                            Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_question)
                                            tv_question.text = img.get(0)
                                        }else {
                                            val img = arrItems.get(1).split("IMG")
                                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                            val options: RequestOptions = RequestOptions()
                                                    .centerCrop()
                                                    .placeholder(R.drawable.logo)
                                                    .error(R.drawable.logo)

                                            Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_question)
                                        }
                                    }
                                }else{
                                    Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                                }
                            } else {
                                iv_question.visibility = View.GONE
                                tv_question.visibility = View.VISIBLE
                                tv_question.text = nextQuestionResponse.question
                            }

                            if (nextQuestionResponse.is_image_Option1 == 1) {
                                val arrItems = nextQuestionResponse.option1.split("[SPLIT]")
                                if(arrItems.isNotEmpty()){
                                    if(arrItems.get(0).contains("IMG")){
                                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                                        val img =  arrItems.get(0).split("IMG")
                                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                                        val options: RequestOptions = RequestOptions()
                                                .centerCrop()
                                                .placeholder(R.drawable.logo)
                                                .error(R.drawable.logo)

                                        Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option1)
                                    }else{
                                        val img =  arrItems.get(1).split("IMG")
                                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                        val options: RequestOptions = RequestOptions()
                                                .centerCrop()
                                                .placeholder(R.drawable.logo)
                                                .error(R.drawable.logo)

                                        Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option1)
                                    }
                                }else{
                                    Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                                }
                            } else {
                                tv_option1.text = nextQuestionResponse.option1
                                iv_option1.visibility = View.GONE
                            }
                            if (nextQuestionResponse.is_image_Option2 == 1) {
                                val arrItems = nextQuestionResponse.option2.split("[SPLIT]")
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

                                            Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_question)
                                            val formulas = arrItems.get(1).split("FORMULA")
                                            if(formulas.isNotEmpty()){
                                                tv_question.text = img.get(0) + formulas.get(0)+ formulas.get(1)
                                            }else{
                                                tv_question.text = img.get(0)
                                            }
                                        }else {
                                            val img = arrItems.get(1).split("IMG")
                                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                            val options: RequestOptions = RequestOptions()
                                                    .centerCrop()
                                                    .placeholder(R.drawable.logo)
                                                    .error(R.drawable.logo)

                                            Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_question)
                                            val formulas = arrItems.get(0).split("FORMULA")
                                            if(formulas.isNotEmpty()){
                                                tv_question.text = img.get(0) + formulas.get(0)+ formulas.get(1)
                                            }else{
                                                tv_question.text = img.get(0)
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

                                            Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option2)
                                        } else {
                                            val img = arrItems.get(1).split("IMG")
                                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                            val options: RequestOptions = RequestOptions()
                                                    .centerCrop()
                                                    .placeholder(R.drawable.logo)
                                                    .error(R.drawable.logo)

                                            Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option2)
                                        }
                                    }
                                }else{
                                    Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                                }
                            } else {
                                tv_option2.text = nextQuestionResponse.option2
                                iv_option2.visibility = View.GONE
                            }
                            if (nextQuestionResponse.is_image_Option3 == 1) {
                                val arrItems = nextQuestionResponse.option3.split("[SPLIT]")
                                if(arrItems.isNotEmpty()){
                                    if(arrItems.get(0).contains("IMG")){
                                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                                        val img =  arrItems.get(0).split("IMG")
                                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                                        val options: RequestOptions = RequestOptions()
                                                .centerCrop()
                                                .placeholder(R.drawable.logo)
                                                .error(R.drawable.logo)

                                        Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option3)
                                    }else{
                                        val img =  arrItems.get(1).split("IMG")
                                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                        val options: RequestOptions = RequestOptions()
                                                .centerCrop()
                                                .placeholder(R.drawable.logo)
                                                .error(R.drawable.logo)

                                        Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option3)
                                    }
                                }else{
                                    Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                                }
                            } else {
                                tv_option3.text = nextQuestionResponse.option3
                                iv_option3.visibility = View.GONE
                            }
                            if (nextQuestionResponse.is_image_Option4 == 1) {
                                val arrItems = nextQuestionResponse.option4.split("[SPLIT]")
                                if(arrItems.isNotEmpty()){
                                    if(arrItems.get(0).contains("IMG")){
                                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                                        val img =  arrItems.get(0).split("IMG")
                                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                                        val options: RequestOptions = RequestOptions()
                                                .centerCrop()
                                                .placeholder(R.drawable.logo)
                                                .error(R.drawable.logo)

                                        Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option4)
                                    }else{
                                        val img =  arrItems.get(1).split("IMG")
                                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                        val options: RequestOptions = RequestOptions()
                                                .centerCrop()
                                                .placeholder(R.drawable.logo)
                                                .error(R.drawable.logo)

                                        Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option4)
                                    }
                                }else{
                                    Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                                }
                            } else {
                                tv_option4.text = nextQuestionResponse.option4
                                iv_option4.visibility = View.GONE
                            }
                        }
                    } else {
                        Log.d(MCQTest::class.simpleName, "callNextQuestionApi: No Questions Left...Finish... ")
                        runOnUiThread {
                            question_view.visibility = View.GONE
                            btn_start.visibility = View.GONE
                            tv_score.visibility = View.VISIBLE
                            tv_score.text = resources.getString(R.string.your_score_is, " ${nextQuestionResponse.correct} / ${nextQuestionResponse.total}")
                            tv_time.stop()
                            btn_detail.visibility = View.VISIBLE
                        }
                    }
                } else {
                    Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Error ${responseApi.errorBody()} ")
                }
            }catch (e: Exception){
                Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${e.message}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun callOpenMCQApi(id: Int) {
        Log.d(ChildView::class.simpleName, "callOpenMCQApi: $id")
        coroutineScope.launch {
            try {
                val retrofitService = RetrofitClient.GetService()
                val response = retrofitService.openMCQ(General.addHeaders(this@MCQTest, true), id)
                if(response.isSuccessful && response.body()!=null){
                    Log.d(MCQTest::class.simpleName, " Success: ${response.body()}")
                    val openMcsResponse = response.body() as OpenMCQResponse
                    if(openMcsResponse.info_Mcq.status.contentEquals("1")){
                        Log.d(MCQTest::class.simpleName, "callOpenMCQApi: Its subscribed...")
                       val responseApi = retrofitService.getNextQuestion(General.addHeaders(this@MCQTest, true), mcqId, sessionId, questionId, answerNo, 0)
                        if(responseApi.isSuccessful && responseApi.body()!=null){
                            val nextQuestionResponse = responseApi.body() as NextQuestionResponse
                            if(nextQuestionResponse.more == 1){
                                Log.d(MCQTest::class.simpleName, "callOpenMCQApi:Continue Fetching Questions ")
                                questionId = nextQuestionResponse.id
                                sessionId = nextQuestionResponse.session_id
                                coroutineScope.launch(Dispatchers.Main) {
                                    question_view.visibility = View.VISIBLE

                                    if (nextQuestionResponse.is_image_question == 1) {
                                        iv_question.visibility = View.VISIBLE
                                        tv_question.visibility = View.VISIBLE
                                        val arrItems = nextQuestionResponse.question.split("[SPLIT]")
                                        if(arrItems.isNotEmpty()){
                                            if(arrItems.get(0).contains("IMG")){
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                                                val img =  arrItems.get(0).split("IMG")
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                                                val options: RequestOptions = RequestOptions()
                                                        .centerCrop()
                                                        .placeholder(R.drawable.logo)
                                                        .error(R.drawable.logo)

                                                Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_question)
                                                tv_question.text = img.get(0)
                                            }else{
                                                val img =  arrItems.get(1).split("IMG")
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                                val options: RequestOptions = RequestOptions()
                                                        .centerCrop()
                                                        .placeholder(R.drawable.logo)
                                                        .error(R.drawable.logo)

                                                Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_question)
                                            }
                                        }else{
                                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                                        }
                                    } else {
                                        iv_question.visibility = View.GONE
                                        tv_question.visibility = View.VISIBLE
                                        tv_question.text = nextQuestionResponse.question
                                    }

                                    if (nextQuestionResponse.is_image_Option1 == 1) {
                                        val arrItems = nextQuestionResponse.option1.split("[SPLIT]")
                                        if(arrItems.isNotEmpty()){
                                            if(arrItems.get(0).contains("IMG")){
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                                                val img =  arrItems.get(0).split("IMG")
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                                                val options: RequestOptions = RequestOptions()
                                                        .centerCrop()
                                                        .placeholder(R.drawable.logo)
                                                        .error(R.drawable.logo)

                                                Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option1)
                                            }else{
                                                val img =  arrItems.get(1).split("IMG")
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                                val options: RequestOptions = RequestOptions()
                                                        .centerCrop()
                                                        .placeholder(R.drawable.logo)
                                                        .error(R.drawable.logo)

                                                Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option1)
                                            }
                                        }else{
                                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                                        }

                                    } else {
                                        tv_option1.text = nextQuestionResponse.option1
                                        iv_option1.visibility = View.GONE
                                    }
                                    if (nextQuestionResponse.is_image_Option2 == 1) {
                                        val arrItems = nextQuestionResponse.option2.split("[SPLIT]")
                                        if(arrItems.isNotEmpty()){
                                            if(arrItems.get(0).contains("IMG")){
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                                                val img =  arrItems.get(0).split("IMG")
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                                                val options: RequestOptions = RequestOptions()
                                                        .centerCrop()
                                                        .placeholder(R.drawable.logo)
                                                        .error(R.drawable.logo)

                                                Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option2)
                                            }else{
                                                val img =  arrItems.get(1).split("IMG")
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                                val options: RequestOptions = RequestOptions()
                                                        .centerCrop()
                                                        .placeholder(R.drawable.logo)
                                                        .error(R.drawable.logo)

                                                Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option2)
                                            }
                                        }else{
                                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                                        }
                                    } else {
                                        tv_option2.text = nextQuestionResponse.option2
                                        iv_option2.visibility = View.GONE

                                    }
                                    if (nextQuestionResponse.is_image_Option3 == 1) {
                                        val arrItems = nextQuestionResponse.option3.split("[SPLIT]")
                                        if(arrItems.isNotEmpty()){
                                            if(arrItems.get(0).contains("IMG")){
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                                                val img =  arrItems.get(0).split("IMG")
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                                                val options: RequestOptions = RequestOptions()
                                                        .centerCrop()
                                                        .placeholder(R.drawable.logo)
                                                        .error(R.drawable.logo)

                                                Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option3)
                                            }else{
                                                val img =  arrItems.get(1).split("IMG")
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                                val options: RequestOptions = RequestOptions()
                                                        .centerCrop()
                                                        .placeholder(R.drawable.logo)
                                                        .error(R.drawable.logo)

                                                Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option3)
                                            }
                                        }else{
                                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                                        }
                                    } else {
                                        tv_option3.text = nextQuestionResponse.option3
                                        iv_option3.visibility = View.GONE
                                    }
                                    if (nextQuestionResponse.is_image_Option4 == 1) {
                                        val arrItems = nextQuestionResponse.option4.split("[SPLIT]")
                                        if(arrItems.isNotEmpty()){
                                            if(arrItems.get(0).contains("IMG")){
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi: ${arrItems.get(0)}")
                                                val img =  arrItems.get(0).split("IMG")
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image First${img.get(1)}")
                                                val options: RequestOptions = RequestOptions()
                                                        .centerCrop()
                                                        .placeholder(R.drawable.logo)
                                                        .error(R.drawable.logo)

                                                Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option4)
                                            }else{
                                                val img =  arrItems.get(1).split("IMG")
                                                Log.d(MCQTest::class.simpleName, "callNextQuestionApi:Load Image ${img.get(1)}")
                                                val options: RequestOptions = RequestOptions()
                                                        .centerCrop()
                                                        .placeholder(R.drawable.logo)
                                                        .error(R.drawable.logo)

                                                Glide.with(this@MCQTest).load(img.get(1)).apply(options).into(iv_option4)
                                            }
                                        }else{
                                            Log.d(MCQTest::class.simpleName, "callNextQuestionApi: Array is Empty...")
                                        }
                                    } else {
                                        tv_option4.text = nextQuestionResponse.option4
                                        iv_option4.visibility = View.GONE
                                    }
                                }
                            }else{
                                Log.d(MCQTest::class.simpleName, "callOpenMCQApi: No Questions Left... ")
                            }
                        }else{
                            Log.d(MCQTest::class.simpleName, "callOpenMCQApi: Error ${responseApi.errorBody()} ")
                        }
                    }
                }else{
                    Log.d(MCQTest::class.simpleName, "callLoginApi Fail: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.d(MCQTest::class.simpleName, "callLoginApi: Exception ${e.message} ")
            }
        }
    }
}