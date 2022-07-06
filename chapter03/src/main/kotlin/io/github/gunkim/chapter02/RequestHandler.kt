package io.github.gunkim.chapter02

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.Socket
import java.util.logging.Logger

class RequestHandler(
    private val connection: Socket,
) : Thread() {
    override fun run() {
        val (inetAddress, port) = connection
        log.info("New Client Connect! Connected IP : ${inetAddress}, Port : ${port}")

        connection.getInputStream()
            .let(::InputStreamReader)
            .let(::BufferedReader)
            .apply { print("request line : ${readLine()}") }
            .run(::generateHeader)
            .apply(::print)

        DataOutputStream(connection.getOutputStream()).use { dos ->
            val body = "Hello World".toByteArray()
            response200Header(dos, body.size)
            responseBody(dos, body)
        }
    }

    private fun print(msg: String) = log.info(msg)
    private fun generateHeader(br: BufferedReader): String {
        val line = br.readLine()
            .takeUnless(String::isBlank) ?: return ""
        return "header : ${line}\n${generateHeader(br)}"
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

private operator fun Socket.component2(): Int = this.port

private operator fun Socket.component1(): InetAddress = this.inetAddress