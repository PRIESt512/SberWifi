package ru.sbrf.sberwifi.http.report.iperf

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.sbrf.sberwifi.wifi.iperf.IperfReport

interface RetrofitIperf {

    @POST("monitoring/iperf")
    fun postReport(@Body body: IperfReport): Deferred<Response<String>>
}