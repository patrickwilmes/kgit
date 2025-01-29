package com.bitlake

private fun prompt() {
    print("> ")
}

fun main() {
    do {
        prompt()
        val input = readlnOrNull()
    } while (input != null && input != "exit")
}