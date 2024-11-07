/*
* Copyright (c) 2024, Patrick Wilmes <p.wilmes89@gmail.com>
* All rights reserved.
*
* SPDX-License-Identifier: BSD-2-Clause
*/
package com.bitlake.command

import java.nio.file.Path

sealed interface Renderer {
    fun render()

    data class StatusRenderer(
        private val untrackedFiles: Set<Path>,
        private val trackedFiles: Set<Path>,
    ) : Renderer {
        override fun render() {
            println("Tracked files:")
            trackedFiles.forEach {
                println(it)
            }
            println("Untracked files:")
            untrackedFiles.forEach {
                println(it)
            }
        }
    }
}
