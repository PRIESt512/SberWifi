package ru.sbrf.sberwifi.http.report

abstract class BaseUseCase<Params, Result> {

    abstract suspend fun doWork(params: Params): Result
}