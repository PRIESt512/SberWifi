package ru.sbrf.sberwifi.http.report.iperf

import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitIperf {

    @POST("monitoring/iperf")
    fun postReport(@Body body: RequestBody): Deferred<Response<String>>
}