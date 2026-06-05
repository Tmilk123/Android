package com.example.myapplication.data

/**
 * 全局配置 — 控制数据源、功能开关
 * 生产环境中可替换为 DataStore 或远程配置
 */
object AppConfig {
    /** true = 真实素材 (Pexels/Unsplash), false = 测试假数据 */
    var useRealData: Boolean = true
}
