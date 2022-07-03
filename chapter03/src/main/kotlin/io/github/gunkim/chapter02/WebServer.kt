package io.github.gunkim.chapter02

import java.net.ServerSocket
import java.util.logging.Logger

private const val DEFAULT_PORT = 8080

private val logger = Logger.getLogger("GLOBAL_MAIN_FUNCTION")

fun main(args: Array<String>) {
    val port: Int = if (args.isEmpty()) {
        DEFAULT_PORT
    } else {
        args[0].toInt()
    }

    while (true) {
        ServerSocket(port).use {
            logger.info("Web Application Server Started $port port.")

            val requestHandler = RequestHandler(it.accept())
            requestHandler.start()
        }
    }
}