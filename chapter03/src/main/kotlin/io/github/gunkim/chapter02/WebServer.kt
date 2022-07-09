package io.github.gunkim.chapter02

import java.net.ServerSocket
import java.util.logging.Logger

class WebServer(
    private val port: Int,
) {
    fun start() {
        ServerSocket(port).use {
            while (true) {
                logger.info("Web Application Server Started $port port.")

                val requestHandler = RequestHandler(it.accept(), ResourceFileManager())
                requestHandler.start()
            }
        }
    }

    companion object {
        private val logger = Logger.getLogger(WebServer::class.java.name)
    }
}