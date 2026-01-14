package com.company.kmslauncher.modules
import android.content.Context
import android.os.Environment
import com.company.kmslauncher.core.IKmsModule
import java.io.File
/**
 * MediaScannerModule 文件功能：负责扫描存储设备中的媒体文件
 * 注释原则：每一行都包含中文说明
 */
class MediaScannerModule : IKmsModule {
    // 定义支持的媒体格式列表
    private val videoExtensions = listOf("mp4", "mkv", "avi", "mov")
    private val imageExtensions = listOf("jpg", "png", "jpeg", "webp")
    // 公开静态变量：存储扫描到的文件路径列表
    companion object {
        val playList = mutableListOf<File>()
    }
    override fun getModuleName(): String = "MediaScannerModule"
    override fun initialize(context: Context) {}
    override fun start() {
        // 1. 获取外部存储根目录（通常是广告机的内置存储或挂载的 U 盘）
        val root = Environment.getExternalStorageDirectory()

        // 2. 执行递归扫描逻辑
        scanDirectory(root)
    }
    /**
     * scanDirectory 文件功能：递归扫描文件夹
     */
    private fun scanDirectory(dir: File) {
        // 3. 检查目录是否存在且可读
        if (dir.exists() && dir.isDirectory) {
            // 4. 遍历目录下所有子文件
            dir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    // 5. 如果是文件夹，继续深入扫描
                    scanDirectory(file)
                } else {
                    // 6. 获取后缀名并转为小写
                    val ext = file.extension.lowercase()
                    // 7. 如果属于视频或图片格式，则加入播放列表
                    if (videoExtensions.contains(ext) || imageExtensions.contains(ext)) {
                        playList.add(file)
                    }
                }
            }
        }
    }
    override fun stop() {
        // 8. 释放内存
        playList.clear()
    }
}