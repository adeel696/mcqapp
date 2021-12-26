package com.digitalandroidweb.androidregisterandlogin.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
        @SerializedName("email")
        val email: String,
        @SerializedName("password")
        val password: String
)


data class RegisterRequest(
        @SerializedName("name")
        val name: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("username")
        val username: String,
        @SerializedName("password")
        val password: String,
        @SerializedName("password_confirmation")
        val password_confirmation: String,
        @SerializedName("phone")
        val phone: String
)

data class PaymentRequest(
        @SerializedName("mobile_number")
        val mobile_number: String,
        @SerializedName("amount")
        val amount: String,
        @SerializedName("otp")
        val otp: String,
        @SerializedName("mcqs_type_id")
        val mcqs_type_id: Int
)

