package ru.sbrf.sberwifi.http.report.iperf

import ru.sbrf.sberwifi.http.report.BaseUseCase

class PostOfIperfUseCase : BaseUseCase<String, PostOfIperfUseCase.Result>() {

    override suspend fun doWork(params: String): Result {
        // val body = RequestBody.create(MediaType.parse("application/json"), params)
        val result = IperfRepository().doWork(params)
        return Result(result.code(), result.body())
    }

    class Result(val status: Int, val body: String?)
}