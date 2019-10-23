package ru.sbrf.sberwifi.http.report

import ru.sbrf.sberwifi.wifi.model.WiFiData

class PostOfReportUseCase : BaseUseCase<WiFiData, PostOfReportUseCase.Result>() {

    override suspend fun doWork(params: WiFiData): Result {
        val result = ReportRepository().doWork(params)
        return Result(result.code(), result.body())
    }

    class Result(val status: Int, val body: String?)
}