package com.bitlake.command

import arrow.core.Either
import com.bitlake.Failure
import com.bitlake.repository.Repository
import java.nio.file.Path
import java.nio.file.Paths

sealed interface Command {
    fun execute(): Either<Failure, Response>

    data class Init(private val path: Path = Paths.get(".").toAbsolutePath()) : Command {
        override fun execute(): Either<Failure, Response> =
            Repository.getInstance(path).initialize()
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
