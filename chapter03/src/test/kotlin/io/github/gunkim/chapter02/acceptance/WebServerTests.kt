package io.github.gunkim.chapter02.acceptance

import io.github.gunkim.chapter02.WebServer
import io.restassured.RestAssured
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File
import java.util.random.RandomGenerator

class WebServerTests {
    private var port: Int = 0

    @BeforeEach
    fun setup() {
        port = RandomGenerator.getDefault().nextInt(50000)
        WebRunner(port).start()
    }

    @Test
    @DisplayName("/index.html 요청할 경우 index.html을 응답한다")
    fun requirements1() {
        val response = RestAssured.given().log().all()
            .`when`()
            .port(port)
            .post("/index.html")
            .then().log().all()
            .extract().body().asByteArray()
        assertThat(response).isEqualTo(resourceFile("/web/index.html").readBytes())
    }

    private fun resourceFile(filePath: String): File = File(javaClass.getResource(filePath).file)
}

class WebRunner(
    private val port: Int,
) : Thread() {
    override fun run() {
        val server = WebServer(port)
        server.start()
    }
}