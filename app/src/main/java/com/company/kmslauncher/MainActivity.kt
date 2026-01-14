package com.company.kmslauncher
import android.os.Bundle // 引入 Bundle 类
import androidx.appcompat.app.AppCompatActivity // 引入 AppCompat 兼容基类
import com.company.kmslauncher.core.IKmsModule // 引入模块接口
import com.company.kmslauncher.modules.AdPlayerModule // 引入播放模块
import com.company.kmslauncher.modules.MediaScannerModule // 引入扫描模块
import com.company.kmslauncher.modules.YFHardwareModule // 引入硬件模块
/**
 * MainActivity 文件功能：入口及模块管理器
 * 遵循原则：每一行代码都要注释，维持高度结构化
 */
class MainActivity : AppCompatActivity() {
    // 定义活跃模块容器，用于统一管理生命周期
    private val activeModules = mutableListOf<IKmsModule>()
    // 活动创建时的入口函数
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 绑定并加载 activity_main.xml 布局
        setContentView(R.layout.activity_main)
        // 步骤 1: 注册硬件服务模块（处理状态栏和导航栏）
        activeModules.add(YFHardwareModule())
        // 步骤 2: 注册存储扫描模块（搜寻本地媒体文件）
        activeModules.add(MediaScannerModule())
        // 步骤 3: 注册最强播放器模块（执行混播逻辑）
        activeModules.add(AdPlayerModule())
        // 对所有已注册模块执行初始化操作
        activeModules.forEach {
            // 将当前 Activity 作为上下文传入
            it.initialize(this)
            // 启动该模块的业务逻辑
            it.start()
        }
    }
    // 活动销毁时的收尾函数
    override fun onDestroy() {
        super.onDestroy()
        // 循环停止所有活跃模块并释放资源
        activeModules.forEach { it.stop() }
    }
}