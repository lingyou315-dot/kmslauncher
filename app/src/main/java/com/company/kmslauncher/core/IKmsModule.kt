package com.company.kmslauncher.core
import android.content.Context
/**
 * IKmsModule 文件功能：所有功能模块的顶级接口
 * 架构原则：严禁将业务代码直接耦合在 Activity 中。
 * 每一个独立的逻辑（如：播放、网络上报、磁盘清理）都必须实现此接口。
 */
interface IKmsModule {

    // 获取模块的名称，用于调试或日志记录
    fun getModuleName(): String

    // 模块初始化逻辑，在此处加载配置、申请资源
    fun initialize(context: Context)

    // 模块开始执行业务逻辑
    fun start()

    // 模块停止运行并释放资源，防止内存泄漏
    fun stop()
}