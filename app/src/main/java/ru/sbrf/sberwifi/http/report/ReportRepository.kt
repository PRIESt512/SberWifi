package ru.sbrf.sberwifi.http.report

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.sbrf.sberwifi.wifi.model.WiFiData

class ReportRepository : BaseRepository<WiFiData, Response<String>>() {

    override suspend fun doWork(params: WiFiData): Response<String> {
        val retrofitPosts = Retrofit.Builder()
                .baseUrl("http://10.8.7.49:8080")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(RetrofitPosts::class.java)

        return retrofitPosts.postReport(params).await()
    }
}