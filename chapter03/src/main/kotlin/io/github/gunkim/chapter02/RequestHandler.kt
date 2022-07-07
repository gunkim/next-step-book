package io.github.gunkim.chapter02

import java.io.*
import java.net.Socket
import java.util.logging.Logger

class RequestHandler(
    private val connection: Socket,
) : Thread() {
    override fun run() {
        val (inputStream, outputStream) = connection
        connection.connected(log)

        val br = BufferedReader(InputStreamReader(inputStream))

        log.info("request line : ${readLine()}")
        generateHeader(br).also(log::info)

        DataOutputStream(outputStream).use(::responseOk)
    }

    private fun responseOk(dos: DataOutputStream) = with(dos) {
        val body = "Hello World".toByteArray()
        response200Header(this, body.size)
        responseBody(this, body)
    }

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
        private val log: Logger = Logger.getLogger(RequestHandler::class.java.name)
    }
}

private operator fun Socket.component1(): InputStream = this.getInputStream()
private operator fun Socket.component2(): OutputStream = this.getOutputStream()

private fun Socket.connected(log: Logger) = log.info("New Client Connect! Connected IP : ${inetAddress}, Port : ${port}")