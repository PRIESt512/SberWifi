package ru.sbrf.sberwifi.http.report.iperf

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.sbrf.sberwifi.http.report.BaseRepository
import ru.sbrf.sberwifi.wifi.iperf.IperfReport

class IperfRepository : BaseRepository<IperfReport, Response<String>>() {

    override suspend fun doWork(params: IperfReport): Response<String> {
        val retrofitPosts = Retrofit.Builder()
                .baseUrl("http://172.30.14.168:8080")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(RetrofitIperf::class.java)

        return retrofitPosts.postReport(params).await()
    }
}