/*
* Copyright (c) 2024, Patrick Wilmes <p.wilmes89@gmail.com>
* All rights reserved.
*
* SPDX-License-Identifier: BSD-2-Clause
*/
package com.bitlake.repository

import arrow.core.Either
import arrow.core.left
import com.bitlake.Failure
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

class Repository private constructor(
    private var path: Path,
) {
    fun initialize(pathOverride: Path? = null): Either<Failure, Unit> {
        if (pathOverride != null) {
            path = pathOverride
        }
        if (isInitialized()) {
            return Failure("${path.toAbsolutePath()} is already a kgit repository!").left()
        }
        return Either.catch {
            kgitDir().createDirectory()
            Unit
        }.mapLeft { Failure(it.message ?: it.localizedMessage) }
    }

    private fun kgitDir() = path.resolve(KGIT_DIR_NAME)

    private fun isInitialized(): Boolean {
        return kgitDir().exists() && kgitDir().isDirectory()
    }

    companion object {
        private const val KGIT_DIR_NAME = ".kgit"

        @Volatile
        private var instance: Repository? = null

        fun getInstance(path: Path): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(path)
                    .also {
                        instance = it
                    }
            }
    }
}
