package io.github.gunkim.chapter02

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.Socket
import java.util.logging.Logger

class RequestHandler(
    private val connection: Socket,
) : Thread() {
    override fun run() {
        log.info("New Client Connect! Connected IP : ${connection.inetAddress}, Port : ${connection.port}")

        connection.getInputStream()
            .let(::InputStreamReader)
            .let(::BufferedReader)
            .apply {
                var msg = "request line : ${readLine()}\n"
                var line: String = readLine() ?: return
                while (line.isNotEmpty()) {
                    msg += "header : ${line}\n"
                    line = readLine() ?: return
                }
                log.info(msg)
            }

        DataOutputStream(connection.getOutputStream()).use { dos ->
            val body = "Hello World".toByteArray()
            response200Header(dos, body.size)
            responseBody(dos, body)
        }
    }

    private fun response200Header(dos: DataOutputStream, lengthOfBodyContent: Int) = dos.run {
        writeBytes("HTTP/1.1 200 OK \r\n")
        writeBytes("Content-Type: text/html;charset=utf-8\r\n")
        writeBytes("Content-Length: ${lengthOfBodyContent}\r\n")
        writeBytes("\r\n")
    }

    private fun responseBody(dos: DataOutputStream, body: ByteArray) = dos.run {
        write(body, 0, body.size)
        writeBytes("\r\n")
        flush()
    }

    companion object {
        private val log = Logger.getLogger(RequestHandler::class.java.name)
    }
}