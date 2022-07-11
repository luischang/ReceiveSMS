package dev.lchang.receivesms.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.converter.gson.GsonConverterFactory


object ResultClient {
    private var url = "https://smishinguesan.luischang.repl.co/api/v1/"
   /* private var httpClient = OkHttpClient().Builder()
        .connectTimeout(30, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.MINUTES)
        .writeTimeout(30, TimeUnit.MINUTES)
        .build()*/
   var gson: Gson? = GsonBuilder()
       .setLenient()
       .create()
    private fun buildRetrofit() = retrofit2.Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitService: ResultService by lazy {
        buildRetrofit().create(ResultService::class.java)
    }

}