package com.company.kmslauncher.modules.player

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.company.kmslauncher.common.Constant
import com.company.kmslauncher.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.File

/**
 * 内部媒体包装类
 */
private data class LocalMedia(
    val file: File,
    val isVideo: Boolean
)

/**
 * 播放管理模块
 * 职责：负责本地素材的加载、视频图片混播调度及解码器资源释放
 */
object PlayerManager {
    private const val TAG = "KmsPlayer"

    private var exoPlayer: ExoPlayer? = null
    private var mediaList = mutableListOf<LocalMedia>()
    private var currentIndex = 0
    private var playerScope = CoroutineScope(Dispatchers.Main + Job())

    /**
     * 初始化播放器并开始循环播放
     * @param context 上下文
     * @param binding 视图绑定
     */
    fun initAndStart(context: Context, binding: ActivityMainBinding) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
            binding.playerView.player = exoPlayer

            // 监听播放状态，视频结束自动切下一个
            exoPlayer?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        playNext(context, binding)
                    }
                }
            })
        }
        refreshAndPlay(context, binding)
    }

    /**
     * 重新扫描文件夹并从头开始播放（用于同步完成后的刷新）
     * @param context 上下文
     * @param binding 视图绑定
     */
    fun refreshAndPlay(context: Context, binding: ActivityMainBinding) {
        playerScope.coroutineContext.cancelChildren()
        exoPlayer?.stop()

        loadPlaylist(context)

        if (mediaList.isNotEmpty()) {
            currentIndex = 0
            playCurrent(context, binding)
        } else {
            Log.w(TAG, "播放列表为空")
        }
    }

    /**
     * 加载本地主播放目录下的媒体文件
     */
    private fun loadPlaylist(context: Context) {
        val dir = File(context.filesDir, Constant.LOCAL_MAIN_FOLDER)
        mediaList.clear()

        if (dir.exists() && dir.isDirectory) {
            val files = dir.listFiles()?.filter { file ->
                val name = file.name.lowercase()
                name.endsWith(".mp4") || name.endsWith(".mkv") ||
                        name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")
            }?.sortedBy { it.name }

            files?.forEach {
                val isVideo = it.name.lowercase().endsWith(".mp4") || it.name.lowercase().endsWith(".mkv")
                mediaList.add(LocalMedia(it, isVideo))
            }
        }
    }

    /**
     * 执行当前索引素材的显示或播放
     */
    private fun playCurrent(context: Context, binding: ActivityMainBinding) {
        if (mediaList.isEmpty()) return
        val current = mediaList[currentIndex]
        playerScope.coroutineContext.cancelChildren()

        if (current.isVideo) {
            binding.imageView.visibility = View.GONE
            binding.playerView.visibility = View.VISIBLE

            val mediaItem = MediaItem.fromUri(Uri.fromFile(current.file))
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.play()
        } else {
            exoPlayer?.pause()
            binding.playerView.visibility = View.GONE
            binding.imageView.visibility = View.VISIBLE
            binding.imageView.setImageURI(Uri.fromFile(current.file))

            playerScope.launch {
                delay(Constant.IMAGE_DURATION_MS)
                playNext(context, binding)
            }
        }
    }

    private fun playNext(context: Context, binding: ActivityMainBinding) {
        if (mediaList.isEmpty()) return
        currentIndex = (currentIndex + 1) % mediaList.size
        playCurrent(context, binding)
    }

    /**
     * 释放播放器资源，停止后台运行
     */
    fun stopAndRelease() {
        playerScope.coroutineContext.cancelChildren()
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
    }
}