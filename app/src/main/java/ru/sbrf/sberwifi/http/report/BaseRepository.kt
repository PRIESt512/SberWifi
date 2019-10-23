package ru.sbrf.sberwifi.http.report

abstract class BaseRepository<Params, Result> {

    abstract suspend fun doWork(params: Params): Result
}