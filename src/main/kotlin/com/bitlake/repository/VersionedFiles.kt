/*
* Copyright (c) 2024, Patrick Wilmes <p.wilmes89@gmail.com>
* All rights reserved.
*
* SPDX-License-Identifier: BSD-2-Clause
*/
package com.bitlake.repository

import arrow.core.Either
import arrow.core.raise.either
import com.bitlake.Failure
import com.bitlake.repository.Context.KGIT_DIR_NAME
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths

interface FileTrackingAware {
    fun filterForUntrackedFiles(totalFiles: Set<Path>): Either<Failure, Set<Path>>
    fun filterForTrackedFiles(totalFiles: Set<Path>): Either<Failure, Set<Path>>
    fun trackFiles(totalFiles: Set<Path>, append: Boolean = true): Either<Failure, Unit>
    fun untrackFiles(totalFiles: Set<Path>): Either<Failure, Unit>
}

@OptIn(ExperimentalSerializationApi::class)
class VersionedFiles(basePath: Path) : FileTrackingAware {
    private val objectsFilePath = basePath.resolve(KGIT_DIR_NAME).resolve(OBJECT_FILE)

    override fun filterForUntrackedFiles(totalFiles: Set<Path>): Either<Failure, Set<Path>> =
        either {
            val trackedFiles = listTrackedFiles().bind()
            totalFiles - trackedFiles
        }

    override fun filterForTrackedFiles(totalFiles: Set<Path>): Either<Failure, Set<Path>> =
        either {
            totalFiles.intersect(listTrackedFiles().bind())
        }

    override fun trackFiles(totalFiles: Set<Path>, append: Boolean): Either<Failure, Unit> =
        Either.catch {
            FileOutputStream(objectsFilePath.toFile(), append).use {
                it.write(Cbor.encodeToByteArray(SetSerializer(PathSerializer), totalFiles))
            }
        }.mapLeft { Failure(it.message ?: it.localizedMessage) }

    override fun untrackFiles(totalFiles: Set<Path>): Either<Failure, Unit> = either {
        val filesStillTracked = listTrackedFiles().bind()
            .filter { !totalFiles.contains(it) }
            .toSet()
        trackFiles(totalFiles = filesStillTracked, append = false).bind()
    }

    private fun listTrackedFiles(): Either<Failure, Set<Path>> {
        val bytes = File(objectsFilePath.toAbsolutePath().toString()).readBytes()
        return Either.catch {
            Cbor.decodeFromByteArray(SetSerializer(PathSerializer), bytes)
        }.mapLeft { Failure(it.message ?: it.localizedMessage) }
    }

    companion object {
        private const val OBJECT_FILE = "objects"
    }

    private object PathSerializer : KSerializer<Path> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("path", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Path {
            return Paths.get(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: Path) {
            encoder.encodeString(value.toAbsolutePath().toString())
        }
    }
}
