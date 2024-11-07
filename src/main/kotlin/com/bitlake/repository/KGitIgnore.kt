/*
* Copyright (c) 2024, Patrick Wilmes <p.wilmes89@gmail.com>
* All rights reserved.
*
* SPDX-License-Identifier: BSD-2-Clause
*/
package com.bitlake.repository

import arrow.core.Either
import com.bitlake.Failure
import java.nio.file.Path
import java.nio.file.Paths

interface Ignorable {
    fun ignoredPaths(): Either<Failure, Set<Path>>
}

class KGitIgnore : Ignorable {
    override fun ignoredPaths(): Either<Failure, Set<Path>> {
        val ignoreFile = Paths.get(Context.wd).toAbsolutePath().resolve(".kgitignore")
        return Either.catch {
            ignoreFile.toFile().readText().split("\n")
                .map {
                    Paths.get(it).toAbsolutePath()
                }.toSet()
        }.mapLeft { Failure(it.message ?: it.localizedMessage) }
    }
}