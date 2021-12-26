package com.digitalandroidweb.androidregisterandlogin.network

import com.digitalandroidweb.androidregisterandlogin.util.ApplicationConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    companion object{
        fun GetService(): Apinterface {
            val logging =  HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val  httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
            httpClient.connectTimeout(100,TimeUnit.SECONDS).readTimeout(100,TimeUnit.SECONDS)
            val retrofit = Retrofit.Builder()
                    .baseUrl(ApplicationConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()

            return retrofit.create(Apinterface::class.java)
        }
    }

}