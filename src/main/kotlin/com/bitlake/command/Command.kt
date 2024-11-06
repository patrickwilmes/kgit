package com.bitlake.command

import arrow.core.Either
import com.bitlake.Failure
import com.bitlake.repository.Repository
import java.nio.file.Path
import java.nio.file.Paths

sealed class Command(
    protected val repository: Repository,
) {
    abstract fun execute(): Either<Failure, Response>

    class Init(
        private val path: Path = Paths.get(".").toAbsolutePath(),
        repository: Repository,
    ) : Command(repository) {
        override fun execute(): Either<Failure, Response> =
            repository.initialize(path)
                .map {
                    InitCommandResponse("Repository successfully initialized")
                }

        data class InitCommandResponse(private val message: String) : Response {
            override fun render() {
                println(message)
            }
        }
    }
}
