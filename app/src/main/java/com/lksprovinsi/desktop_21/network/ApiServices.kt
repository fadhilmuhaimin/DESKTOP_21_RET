package com.lksprovinsi.desktop_21.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {

    @POST("api/login")
    fun login(
        @Body body: LoginRequest
    ): Call<ResponseLogin>

}