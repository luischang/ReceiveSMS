package dev.lchang.receivesms.service

import dev.lchang.receivesms.model.RequestModel
import dev.lchang.receivesms.model.ResultModel
import retrofit2.Call
import retrofit2.http.*

interface ResultService {
    @Headers("Content-Type: application/json")
    @POST("smishing")
    fun postResultSmishing(@Body requestModel: RequestModel): Call<ResultModel>




}