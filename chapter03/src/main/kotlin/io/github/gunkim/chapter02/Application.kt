package io.github.gunkim.chapter02

fun main(args: Array<String>) {
    WebServer(getPort(args)).start()
}

private fun getPort(args: Array<String>): Int {
    if (args.isNotEmpty()) {
        return args[0].toInt()
    }
    return 8080
}