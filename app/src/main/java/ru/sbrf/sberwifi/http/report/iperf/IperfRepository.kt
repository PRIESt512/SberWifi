package ru.sbrf.sberwifi.http.report.iperf

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import org.apache.commons.lang3.StringUtils
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.sbrf.sberwifi.http.report.BaseRepository

class IperfRepository : BaseRepository<String, Response<String>>() {

    override suspend fun doWork(params: String): Response<String> {
        val retrofitPosts = Retrofit.Builder()
                .baseUrl("http://172.30.14.161:80")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
                .create(RetrofitIperf::class.java)

        Log.i("IperfRepo", "Request")
        var result: Response<String>? = null
        try {
            result = retrofitPosts.postReport(params).await()
        } catch (ex: Exception) {
            Log.e("IperfRepo", ex.toString())
        }
        return Response.success(StringUtils.EMPTY);
    }
}