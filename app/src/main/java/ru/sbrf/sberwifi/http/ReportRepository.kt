package ru.sbrf.sberwifi.http

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ReportRepository : BaseRepository<String, ReportRepository.Result>() {

    override suspend fun doWork(params: String): Result {
        val retrofitPosts = Retrofit.Builder()
                .baseUrl("http://192.168.43.72:8080")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(RetrofitPosts::class.java)

        val result = retrofitPosts.postReport(params).await()
        return Result(result.body())
    }

    data class Result(val posts: String?)
}