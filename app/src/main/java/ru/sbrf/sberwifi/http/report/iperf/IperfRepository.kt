package ru.sbrf.sberwifi.http.report.iperf

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.sbrf.sberwifi.http.report.BaseRepository

class IperfRepository : BaseRepository<RequestBody, Response<String>>() {

    override suspend fun doWork(params: RequestBody): Response<String> {
        val retrofitPosts = Retrofit.Builder()
                .baseUrl("http://10.8.7.49:8080")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(RetrofitIperf::class.java)

        return retrofitPosts.postReport(params).await()
    }
}