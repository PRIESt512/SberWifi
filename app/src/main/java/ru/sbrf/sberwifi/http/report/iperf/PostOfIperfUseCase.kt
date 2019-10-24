package ru.sbrf.sberwifi.http.report.iperf

import ru.sbrf.sberwifi.http.report.BaseUseCase
import ru.sbrf.sberwifi.wifi.iperf.IperfReport

class PostOfIperfUseCase : BaseUseCase<IperfReport, PostOfIperfUseCase.Result>() {

    override suspend fun doWork(params: IperfReport): Result {
        // val body = RequestBody.create(MediaType.parse("application/json"), params)
        val result = IperfRepository().doWork(params)
        return Result(result.code(), result.body())
    }

    class Result(val status: Int, val body: String?)
}