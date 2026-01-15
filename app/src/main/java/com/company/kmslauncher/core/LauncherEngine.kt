package com.company.kmslauncher.core

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.company.kmslauncher.modules.player.PlayerManager
import com.company.kmslauncher.modules.sync.FileSyncManager
import com.company.kmslauncher.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * 核心调度引擎
 * 职责：负责初始化扫描、同步状态反馈及模块间的逻辑调度
 */
object LauncherEngine {
    private const val TAG = "KmsEngine"
    private var mainBinding: ActivityMainBinding? = null

    /**
     * 引擎初始化及启动扫描
     * @param context 上下文
     * @param binding 视图绑定
     */
    fun start(context: Context, binding: ActivityMainBinding) {
        this.mainBinding = binding
        Log.d(TAG, "引擎启动，开始执行初始化挂载点扫描")

        val storageBase = File("/mnt/media_rw")
        if (storageBase.exists() && storageBase.isDirectory) {
            storageBase.listFiles()?.forEach { drive ->
                if (drive.isDirectory) {
                    CoroutineScope(Dispatchers.IO).launch {
                        FileSyncManager.syncFromUsb(context, drive.absolutePath)
                    }
                }
            }
        }
    }

    /**
     * 同步开始回调
     */
    fun onSyncStarted(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "正在同步U盘素材...", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 同步完成回调
     */
    fun onSyncFinished(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "同步成功，正在刷新播放列表", Toast.LENGTH_SHORT).show()
            mainBinding?.let {
                PlayerManager.refreshAndPlay(context, it)
            }
        }
    }
}