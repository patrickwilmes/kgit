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
import kotlin.io.path.exists

interface Ignorable {
    fun ignoredPaths(): Either<Failure, Set<Path>>
}

class KGitIgnore : Ignorable {
    override fun ignoredPaths(): Either<Failure, Set<Path>> {
        val ignoreFile = Paths.get(Context.wd).toAbsolutePath().resolve(".kgitignore")
        return Either.catch {
            if (ignoreFile.exists()) {
                ignoreFile.toFile().readText().split("\n")
                    .map {
                        Paths.get(it).toAbsolutePath()
                    }.toSet()
            } else {
                emptySet()
            }
        }.mapLeft { Failure(it.message ?: it.localizedMessage) }
    }
}