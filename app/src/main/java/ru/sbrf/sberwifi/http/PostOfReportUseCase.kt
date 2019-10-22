package ru.sbrf.sberwifi.http

class PostOfReportUseCase : BaseUseCase<String, PostOfReportUseCase.Result>() {

    override suspend fun doWork(params: String): Result {
        return Result(
                ReportRepository()
                        .doWork(params)
                        .posts
        )
    }

    class Result(val posts: String?)
}