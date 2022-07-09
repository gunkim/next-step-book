package io.github.gunkim.chapter02

import java.io.File
import java.io.FileNotFoundException
import java.net.URL

class ResourceFileManager {
    fun load(filePath: String): File {
        return File(getPullPath(filePath).file)
    }

    private fun getPullPath(filePath: String): URL = javaClass.getResource(filePath)
        ?: throw FileNotFoundException()
}