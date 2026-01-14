package com.company.kmslauncher.modules
import android.app.Activity // 引入安卓 Activity 类
import android.content.Context // 引入上下文类
import android.net.Uri // 引入统一资源标识符类
import android.view.View // 引入视图基类
import android.widget.ImageView // 引入图片控件类
import androidx.media3.common.MediaItem // 引入 Media3 媒体条目类
import androidx.media3.common.Player // 引入播放器接口类
import androidx.media3.exoplayer.ExoPlayer // 引入 ExoPlayer 实现类
import androidx.media3.ui.PlayerView // 引入播放器视图类
import com.company.kmslauncher.R // 引入资源索引类，确保路径与 build.gradle.kts 中的 namespace 一致
import com.company.kmslauncher.core.IKmsModule // 引入模块接口
import java.io.File // 引入文件处理类
/**
 * AdPlayerModule 文件功能：基于 Google Media3 的专业级广告混播引擎
 * 核心原则：严格遵守每一行代码必有中文注释，确保代码可读性
 */
class AdPlayerModule : IKmsModule {
    // 定义播放器核心对象，设为私有防止外部干扰
    private var exoPlayer: ExoPlayer? = null
    // 定义显示视频的控件引用
    private var playerView: PlayerView? = null
    // 定义显示图片的控件引用
    private var imageView: ImageView? = null
    // 记录当前播放到列表中的第几个文件
    private var currentIndex = 0
    // 缓存上下文对象供后续使用
    private var context: Context? = null
    // 返回模块名称，用于调试和日志
    override fun getModuleName(): String = "AdPlayerModule"
    // 初始化方法：在应用启动时绑定 UI 控件并构建播放引擎
    override fun initialize(context: Context) {
        // 将通用的上下文转换为具体的 Activity 实例
        val activity = context as Activity
        // 保存上下文引用
        this.context = context
        // 使用 Builder 模式构建最强大的 ExoPlayer 实例
        exoPlayer = ExoPlayer.Builder(context).build()
        // 从布局 ID 中找到播放器视图容器
        playerView = activity.findViewById(R.id.exo_player_view)
        // 将构建好的播放器引擎绑定到 UI 视图上
        playerView?.player = exoPlayer
        // 从布局 ID 中找到图片显示控件
        imageView = activity.findViewById(R.id.main_image_player)
        // 为播放器添加事件监听器
        exoPlayer?.addListener(object : Player.Listener {
            // 当播放状态发生改变时执行的回调函数
            override fun onPlaybackStateChanged(state: Int) {
                // 如果检测到当前媒体已经播放完毕
                if (state == Player.STATE_ENDED) {
                    // 自动开始加载并播放下一个媒体文件
                    playNext()
                }
            }
        })
    }
    // 启动方法：模块准备就绪后开始执行业务逻辑
    override fun start() {
        // 开始播放流程的第一步
        playNext()
    }
    // 播放逻辑核心：负责判断文件类型并调度不同的显示方式
    private fun playNext() {
        // 从扫描模块获取当前搜寻到的媒体文件列表
        val list = MediaScannerModule.playList
        // 如果列表为空，表示没有任何文件可以播放，直接返回
        if (list.isEmpty()) return
        // 索引溢出检查：如果播完了最后一项，则重置回第一项开始循环
        if (currentIndex >= list.size) currentIndex = 0
        // 获取当前索引指向的文件对象
        val currentFile = list[currentIndex]
        // 索引自增，为下一次播放做准备
        currentIndex++
        // 提取文件后缀并转换为小写，用于类型识别
        val ext = currentFile.extension.lowercase()
        // 如果后缀属于常见的视频格式
        if (listOf("mp4", "mkv", "avi", "ts", "mov", "flv").contains(ext)) {
            // 调用显示视频的逻辑函数
            showVideo(currentFile.absolutePath)
        } else {
            // 否则视为图片，调用显示图片的逻辑函数
            showImage(currentFile.absolutePath)
        }
    }
    // 视频播放调度函数
    private fun showVideo(path: String) {
        // 将图片控件设为隐藏状态，腾出屏幕空间
        imageView?.visibility = View.GONE
        // 将播放器控件设为可见状态，准备展示视频
        playerView?.visibility = View.VISIBLE
        // 根据文件绝对路径创建一个媒体条目对象
        val mediaItem = MediaItem.fromUri(Uri.fromFile(File(path)))
        // 将条目设置给 ExoPlayer 引擎
        exoPlayer?.setMediaItem(mediaItem)
        // 让引擎开始准备解码资源
        exoPlayer?.prepare()
        // 指令引擎立即开始播放
        exoPlayer?.play()
    }
    // 图片展示调度函数
    private fun showImage(path: String) {
        // 图片展示时，应停止当前的视频解码，节省系统 CPU/GPU 资源
        exoPlayer?.stop()
        // 将播放器控件设为隐藏
        playerView?.visibility = View.GONE
        // 将图片控件设为可见
        imageView?.visibility = View.VISIBLE
        // 通过路径给 ImageView 设置图像内容
        imageView?.setImageURI(Uri.fromFile(File(path)))
        // 图片本身无法触发“播放完成”事件，因此手动设置 5000 毫秒（5秒）的停留时间
        imageView?.postDelayed({
            // 延时结束后，再次调用 playNext 进入下一轮
            playNext()
        }, 5000)
    }
    // 资源释放函数：当 Activity 销毁时调用，防止内存泄漏
    override fun stop() {
        // 释放 ExoPlayer 占用的底层硬件解码器资源
        exoPlayer?.release()
        // 清空对象引用
        exoPlayer = null
        // 清空上下文引用
        context = null
    }
}