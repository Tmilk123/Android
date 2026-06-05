package com.example.myapplication.data

import com.example.myapplication.network.PexelsFeedRepository
import java.io.File
import java.util.Properties

/**
 * 全局配置
 *
 * API Key 存储在 local.properties (已加入 .gitignore, 不会提交到 Git)
 */
object AppConfig {
    /** 数据源: "fake" | "verified" | "pexels" */
    var dataSource: String = "pexels"

    /** 从 local.properties 读取 Pexels API Key */
    val pexelsApiKey: String by lazy {
        loadApiKeyFromProperties() ?: ""
    }

    val isPexelsAvailable: Boolean
        get() = pexelsApiKey.isNotBlank()

    val pexelsRepository: PexelsFeedRepository?
        get() = if (isPexelsAvailable) PexelsFeedRepository(pexelsApiKey) else null

    private fun loadApiKeyFromProperties(): String? {
        return try {
            val props = Properties()
            // Try multiple locations
            val candidates = listOf(
                File("local.properties"),
                File("../local.properties"),
                File(System.getProperty("user.dir"), "local.properties"),
            )
            val file = candidates.firstOrNull { it.exists() }
            if (file != null) {
                props.load(file.inputStream())
                props.getProperty("pexels.api.key")?.trim()?.takeIf { it.isNotBlank() }
            } else null
        } catch (_: Exception) {
            null
        }
    }
}
