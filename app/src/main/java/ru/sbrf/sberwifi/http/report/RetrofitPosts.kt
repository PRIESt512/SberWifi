package ru.sbrf.sberwifi.http.report

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import ru.sbrf.sberwifi.wifi.model.WiFiData

interface RetrofitPosts {

    @Headers("Authorization: Basic VHJ1bm92OnBhc3N3b3Jk")
    @POST("monitoring/send")
    fun postReport(@Body body: WiFiData): Deferred<Response<String>>
}