package com.company.kmslauncher.modules.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.company.kmslauncher.common.Constant
import com.company.kmslauncher.core.LauncherEngine // 确保导入引擎
import kotlinx.coroutines.*
import java.io.File

object FileSyncManager {
    private const val TAG = "KmsFileSync"

    class UsbReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_MEDIA_MOUNTED) {
                val usbPath = intent.data?.path ?: return
                Log.i(TAG, "找到挂载路径: $usbPath")
                CoroutineScope(Dispatchers.IO).launch {
                    syncFromUsb(context, usbPath)
                }
            }
        }
    }

    suspend fun syncFromUsb(context: Context, usbRootPath: String) {
        withContext(Dispatchers.IO) {
            val sourceDir = File(usbRootPath, Constant.USB_SYNC_FOLDER_NAME)
            val destDir = File(context.filesDir, Constant.LOCAL_MAIN_FOLDER)

            if (!sourceDir.exists() || !sourceDir.isDirectory) return@withContext
            if (!destDir.exists()) destDir.mkdirs()

            val usbFiles = sourceDir.listFiles() ?: return@withContext
            var syncCount = 0

            // 如果有新文件，先给个提示
            if (usbFiles.isNotEmpty()) {
                // 检查是否真的需要拷贝
                val needsSync = usbFiles.any { f ->
                    val target = File(destDir, f.name)
                    !target.exists() || target.length() != f.length()
                }
                if (needsSync) {
                    LauncherEngine.onSyncStarted(context)
                }
            }

            usbFiles.forEach { sourceFile ->
                if (sourceFile.isFile) {
                    val destFile = File(destDir, sourceFile.name)
                    if (!destFile.exists() || destFile.length() != sourceFile.length()) {
                        Log.d(TAG, "📥 正在拷贝: ${sourceFile.name}")
                        sourceFile.inputStream().use { input ->
                            destFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        syncCount++
                    }
                }
            }

            if (syncCount > 0) {
                Log.i(TAG, "✅ 同步结束，新增 $syncCount 个文件")
                // 通知引擎刷新 UI
                LauncherEngine.onSyncFinished(context)
            }
        }
    }
}