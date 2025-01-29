package com.bitlake

import arrow.core.raise.either
import com.bitlake.command.CommandFactory

private fun prompt() {
    print("> ")
}

private fun String.sanitize() = split(" ").toTypedArray()

fun main() {
    do {
        prompt()
        val input = readlnOrNull()?.sanitize()
        either {
            val command = CommandFactory.fromArgs(input ?: emptyArray<String>()).bind()
            val renderer = command.execute().bind()
            renderer.render()
        }.onLeft {
            println(it.message)
        }
    } while (input != null && input[0] != "exit")
}
