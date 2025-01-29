package com.bitlake.command

import arrow.core.Either
import arrow.core.raise.either
import com.bitlake.Failure
import com.bitlake.repository.Context
import com.bitlake.repository.Context.KGIT_DIR_NAME
import com.bitlake.repository.Repository
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

sealed class Command(
    protected val repository: Repository,
) {
    abstract fun execute(): Either<Failure, Renderer>

    class Init(
        private val path: Path = Paths.get(Context.wd).toAbsolutePath(),
        repository: Repository,
    ) : Command(repository) {
        override fun execute(): Either<Failure, Renderer> =
            repository.initialize(path)
                .map {
                    InitCommandRenderer("Repository successfully initialized")
                }

        data class InitCommandRenderer(private val message: String) : Renderer {
            override fun render() {
                println(message)
            }
        }
    }

    class Status(
        repository: Repository
    ) : Command(repository) {
        override fun execute(): Either<Failure, Renderer> = either {
            val workingDirectoryFiles = listAllFilesExcludingDirectory(
                Paths.get(Context.wd),
                listOf(
                    Paths.get(KGIT_DIR_NAME).toAbsolutePath(),
                    Paths.get(".git").toAbsolutePath(),
                ) + repository.ignoredPaths().bind(),
            ).toSet()
            Renderer.StatusRenderer(
                untrackedFiles = repository.filterForUntrackedFiles(workingDirectoryFiles).bind(),
                trackedFiles = repository.filterForTrackedFiles(workingDirectoryFiles).bind(),
            )
        }

        private fun listAllFilesExcludingDirectory(
            rootDir: Path,
            excludeDirs: List<Path>
        ): List<Path> {
            val fileList = mutableListOf<Path>()

            Files.walkFileTree(rootDir, object : FileVisitor<Path> {
                override fun preVisitDirectory(
                    dir: Path,
                    attrs: BasicFileAttributes
                ): FileVisitResult {
                    return if (excludeDirs.contains(dir)) {
                        FileVisitResult.SKIP_SUBTREE
                    } else {
                        FileVisitResult.CONTINUE
                    }
                }

                override fun visitFile(file: Path?, attrs: BasicFileAttributes): FileVisitResult {
                    if (file != null) {
                        fileList.add(file)
                    }
                    return FileVisitResult.CONTINUE
                }

                override fun visitFileFailed(file: Path?, exc: IOException): FileVisitResult {
                    return FileVisitResult.CONTINUE
                }

                override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
                    return FileVisitResult.CONTINUE
                }
            })

            return fileList
        }
    }
}
