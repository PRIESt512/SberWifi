package ru.sbrf.sberwifi.http.report.iperf

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitIperf {

    //@Headers("Authorization: Basic UGxlc2hrYW5ldjEyMw==")
    @POST("iperf/send")
    fun postReport(@Body body: String): Deferred<Response<String>>
}