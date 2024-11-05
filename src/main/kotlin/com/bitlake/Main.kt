/*
* Copyright (c) 2024, Patrick Wilmes <p.wilmes89@gmail.com>
* All rights reserved.
*
* SPDX-License-Identifier: BSD-2-Clause
*/
package com.bitlake

import arrow.core.raise.either
import com.bitlake.command.CommandFactory

/*
kgit <COMMAND> <ARGS>
kgit init | [OPTIONAL_ARGS] (path)
 */

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: kgit <COMMAND> <ARGS>")
        return
    }
    either {
        val command = CommandFactory.fromArgs(args).bind()
        command.execute().bind()
    }.fold({
        println(it.message)
    }) {
        it.render()
    }
}
