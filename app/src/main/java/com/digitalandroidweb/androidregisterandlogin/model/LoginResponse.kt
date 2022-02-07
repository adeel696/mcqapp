package com.digitalandroidweb.androidregisterandlogin.model

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("access_token")
    val access_token: String,
    @SerializedName("token_type")
    val token_type: String,
    @SerializedName("expires_at")
    val expires_at: String,
    @SerializedName("user_image")
    val user_image: String,
    @SerializedName("name")
    val name: String
    )

data class RegisterResponse (
        @SerializedName("message")
        val message: String
)

data class GetMcqResponse (
        @SerializedName("id") val id : String,
@SerializedName("McqType") val mcqType : String,
@SerializedName("Description") val description : String,
@SerializedName("Subscribe") val subscribe : String,
@SerializedName("ExpiryDate") val expiryDate : String,
@SerializedName("Amount") val amount : String,
@SerializedName("McqDetail") val mcqDetail : List<McqDetail>
)

data class GetMcqHistoryResponse (
        @SerializedName("McqName") val mcqName : String,
        @SerializedName("History") val mcqHistory : List<McqHistory>
)

data class McqDetail (
        @SerializedName("id") val id : Int,
        @SerializedName("McqName") val mcqName : String,
        @SerializedName("McqCategoryType") val mcqCategoryType : String,
        @SerializedName("Question") val question : Int
)

data class McqHistory (
        @SerializedName("Date") val date : String,
        @SerializedName("Status") val status : String,
        @SerializedName("Score") val score : String
)

data class Payment (
        @SerializedName("Date") val date : String,
        @SerializedName("Msisdn") val msisdn : String,
        @SerializedName("Subscribe") val subscribe : String,
        @SerializedName("Amount") val amount : String,
        @SerializedName("Status") val status : String,
)

data class Offer (
        @SerializedName("id") val id : Int,
        @SerializedName("McqTypeName") val mcqTypeName : String,
        @SerializedName("Subscription") val subscription : Int,
        @SerializedName("Amount") val amount : Int
)

data class PointOfSale (
        @SerializedName("id") val id : Int,
        @SerializedName("name") val name : String,
        @SerializedName("city") val city : String,
        @SerializedName("address") val address : String,
        @SerializedName("phone_number_1") val phone_number_1 : String,
        @SerializedName("phone_number_2") val phone_number_2 : String,
        @SerializedName("phone_number_3") val phone_number_3 : String,
        @SerializedName("created_at") val created_at : String,
        @SerializedName("updated_at") val updated_at : String,
)

data class YearPublication (
        @SerializedName("year_of_publication") val year : String
)

data class DocYear (
        @SerializedName("year_of_publication") val year : String,
        @SerializedName("image") val image : String,
        @SerializedName("description") val description : String
)

data class Subscription (
        @SerializedName("id") val id : Int,
        @SerializedName("McqTypeName") val mcqTypeName : String,
        @SerializedName("Subscription") val subscription : Int,
        @SerializedName("Status") val status : String,
        @SerializedName("Expire") val expire : String
)

data class PaymentResponse (
        @SerializedName("message")
        val message: String,
        @SerializedName("success")
        val success: Boolean
)


data class OpenMCQResponse (
        @SerializedName("message")
        val message: String,
        @SerializedName("error")
        val error: Int,
        @SerializedName("info_Mcq")
        val info_Mcq: McqInfo
)

data class NextQuestionResponse (
        @SerializedName("more")
        val more: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("Question")
        val question: String,
        @SerializedName("is_image_Question")
        val is_image_question: Int,
        @SerializedName("Option1")
        val option1: String,
        @SerializedName("is_image_Option1")
        val is_image_Option1: Int,
        @SerializedName("Option2")
        val option2: String,
        @SerializedName("is_image_Option2")
        val is_image_Option2: Int,
        @SerializedName("Option3")
        val option3: String,
        @SerializedName("is_image_Option3")
        val is_image_Option3: Int,
        @SerializedName("Option4")
        val option4: String,
        @SerializedName("is_image_Option4")
        val is_image_Option4: Int,
        @SerializedName("session_id")
        val session_id: Int,
        @SerializedName("correct") val correct : Int,
        @SerializedName("total") val total : Int
)


data class DetailMCQResponse (
        @SerializedName("info_McqsScore")
        val info_McqsScore: MCQScore,
        @SerializedName("info_McqsAnswer")
        val info_McqsAnswer: List<MCQAnswers>
)

data class MCQScore (
        @SerializedName("correct") val correct : Int,
        @SerializedName("total") val total : Int
        )


data class MCQAnswers (
        @SerializedName("McqName")
        val mcqName: String,
        @SerializedName("Question")
        val question: String,
        @SerializedName("is_image_Question")
        val is_image_question: Int,
        @SerializedName("Option1")
        val option1: String,
        @SerializedName("is_image_Option1")
        val is_image_Option1: Int,
        @SerializedName("Option2")
        val option2: String,
        @SerializedName("is_image_Option2")
        val is_image_Option2: Int,
        @SerializedName("Option3")
        val option3: String,
        @SerializedName("is_image_Option3")
        val is_image_Option3: Int,
        @SerializedName("Option4")
        val option4: String,
        @SerializedName("is_image_Option4")
        val is_image_Option4: Int,
        @SerializedName("UserAnser") val userAnser : String,
        @SerializedName("CorrectAnswer") val correctAnswer : String,
        @SerializedName("IsCorrect") val isCorrect : String
)

data class Option (
        @SerializedName("option_value") val option_value : String,
        @SerializedName("is_image") val is_image : String,
        @SerializedName("image_name") val image_name : String
)

data class Score (
        @SerializedName("more") val more : Int,
        @SerializedName("correct") val correct : Int,
        @SerializedName("total") val total : Int
)
data class Question (
        @SerializedName("id") val id : Int,
        @SerializedName("question") val question : String,
        @SerializedName("is_image") val is_image : String,
        @SerializedName("image_name") val image_name : String
)

data class McqInfo (
        @SerializedName("id") val id : Int,
        @SerializedName("name") val name : String,
        @SerializedName("mcqs_type_id") val mcqs_type_id : String,
        @SerializedName("mcqs_type_subcategory_id") val question : String,
        @SerializedName("status") val status : String
)