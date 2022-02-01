package com.digitalandroidweb.androidregisterandlogin.network

import com.digitalandroidweb.androidregisterandlogin.model.*
import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface Apinterface {
    @POST(ApplicationConstants.LOGIN)
    suspend fun userLogin(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST(ApplicationConstants.REGISTER)
    suspend fun userRegister(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST(ApplicationConstants.EDIT_PROFILE)
    suspend fun editProfile(@HeaderMap header: Map<String?, String?>,@Body profileRequest: ProfileRequest): Response<RegisterResponse>

    @POST(ApplicationConstants.CONTACT)
    suspend fun contact(@HeaderMap header: Map<String?, String?>,@Body contactRequest: ContactRequest): Response<RegisterResponse>

    @POST(ApplicationConstants.PAYMENT)
    suspend fun addPayment(@HeaderMap header: Map<String?, String?>,@Body paymentRequest: PaymentRequest): Response<PaymentResponse>

    @GET(ApplicationConstants.MCQ_List)
    suspend fun getMCQsList(@HeaderMap header: Map<String?, String?>): Response<List<GetMcqResponse>>

    @GET(ApplicationConstants.MCQ_History_List)
    suspend fun getMCQHistoryList(@HeaderMap header: Map<String?, String?>): Response<List<GetMcqHistoryResponse>>

    @GET(ApplicationConstants.DAILY_TIME)
    suspend fun getTimeSpend(@HeaderMap header: Map<String?, String?>): Response<String>



    @GET(ApplicationConstants.PAYMENT_LIST)
    suspend fun getPaymentHistory(@HeaderMap header: Map<String?, String?>): Response<List<Payment>>

    @GET(ApplicationConstants.OFFER_LIST)
    suspend fun getOfferList(@HeaderMap header: Map<String?, String?>): Response<List<Offer>>

    @GET(ApplicationConstants.SUBSCRIPTION_LIST)
    suspend fun getSubscriptionList(@HeaderMap header: Map<String?, String?>): Response<List<Subscription>>


    @POST(ApplicationConstants.MCQ+"/"+ApplicationConstants.DETAIL+"/{sessionId}")
    suspend fun getQuestionDetail(@Path("sessionId") mcqId: Int,@HeaderMap header: Map<String?, String?>): Response<DetailMCQResponse>

    @POST(ApplicationConstants.MCQ+"/{mqcId}")
    suspend fun openMCQ(@HeaderMap header: Map<String?, String?>,@Path("mqcId") mcqId: Int): Response<OpenMCQResponse>

    @POST(ApplicationConstants.MCQ+"/{mqcId}/"+ApplicationConstants.NEXT_QUESTION)
    suspend fun getNextQuestion(@HeaderMap header: Map<String?, String?>,@Path("mqcId") mcqId: Int,@Query("session_id") sessionId: Int,
                                @Query("question_id") question_id: Int,@Query("answer") answer: Int,
                                @Query("time_in_sec") time_in_sec: Int): Response<NextQuestionResponse>
}