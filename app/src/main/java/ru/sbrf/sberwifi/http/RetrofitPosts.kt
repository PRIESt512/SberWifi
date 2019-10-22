package ru.sbrf.sberwifi.http

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitPosts {

    @POST("post")
    fun postReport(@Body body: String): Deferred<Response<String>>
}