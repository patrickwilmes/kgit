/*
* Copyright (c) 2024, Patrick Wilmes <p.wilmes89@gmail.com>
* All rights reserved.
*
* SPDX-License-Identifier: BSD-2-Clause
*/
package com.bitlake.command

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.bitlake.Failure
import java.nio.file.Paths

object CommandFactory {
    fun fromArgs(args: Array<String>): Either<Failure, Command> {
        return when (args.first()) {
            "init" -> {
                val path = args.getOrElse(1) { "." }
                Command.Init(path = Paths.get(path).toAbsolutePath()).right()
            }
            else -> Failure("Unknown command!").left()
        }
    }
}
