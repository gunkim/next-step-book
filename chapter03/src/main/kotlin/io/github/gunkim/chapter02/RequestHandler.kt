package io.github.gunkim.chapter02

import java.io.DataOutputStream
import java.net.Socket
import java.util.logging.Logger

class RequestHandler(
    private val connection: Socket
) : Thread() {
    override fun run() {
        log.info("New Client Connect! Connected IP : ${connection.inetAddress}, Port : ${connection.port}")

        connection.use {
            DataOutputStream(it.getOutputStream()).use { dos ->
                val body = "Hello World".toByteArray()
                response200Header(dos, body.size)
                responseBody(dos, body)
            }
        }
    }

    private fun response200Header(dos: DataOutputStream, lengthOfBodyContent: Int) {
        dos.writeBytes("HTTP/1.1 200 OK \r\n")
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n")
        dos.writeBytes("Content-Length: ${lengthOfBodyContent}\r\n")
        dos.writeBytes("\r\n")
    }

    private fun responseBody(dos: DataOutputStream, body: ByteArray) {
        dos.write(body, 0, body.size)
        dos.writeBytes("\r\n")
        dos.flush()
    }

    companion object {
        private val log = Logger.getLogger(RequestHandler::class.java.name)
    }
}