package ru.sbrf.sberwifi.http

abstract class BaseRepository<Params, Result> {

    abstract suspend fun doWork(params: Params): Result
}