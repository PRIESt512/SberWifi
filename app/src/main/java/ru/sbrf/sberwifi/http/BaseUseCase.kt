package ru.sbrf.sberwifi.http

abstract class BaseUseCase<Params, Result> {

    abstract suspend fun doWork(params: Params): Result
}