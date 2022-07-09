package io.github.gunkim.chapter02

import java.io.*
import java.net.Socket
import java.util.logging.Logger

class RequestHandler(
    private val connection: Socket,
    private val resourceFileManager: ResourceFileManager,
) : Thread() {
    override fun run() {
        connection.connected(log)

        val (inputStream, outputStream) = connection
        val br = BufferedReader(InputStreamReader(inputStream))

        val requestLine = br.readLine()
        log.info("request line : ${requestLine}")
        generateHeader(br).also(log::info)

        val filePath = requestLine.split(" ")[1]
        resourceFileManager.load("/web${filePath}").run {
            DataOutputStream(outputStream).use { dos ->
                responseOk(dos, this)
            }
        }
    }

    private fun responseOk(dos: DataOutputStream, file: File) = with(dos) {
        val byteArr = file.readBytes()
        response200Header(this, byteArr.size)
        responseBody(this, byteArr)
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