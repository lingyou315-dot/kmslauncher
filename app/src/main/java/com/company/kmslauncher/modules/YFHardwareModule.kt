package com.company.kmslauncher.modules
import android.content.ComponentName // 引入组件名称类
import android.content.Context // 引入上下文类
import android.content.Intent // 引入意图类
import android.content.ServiceConnection // 引入服务连接类
import android.os.IBinder // 引入 Binder 通信类
import com.company.kmslauncher.core.IKmsModule // 引入模块接口
import com.yf.apiserver.IYFAPIserver // 引入厂家的 AIDL 接口
/**
 * YFHardwareModule 文件功能：硬件抽象层实现
 * 职责：连接 A133 厂家服务，实现全屏显示（隐藏状态栏与导航栏）
 */
class YFHardwareModule : IKmsModule {
    // 定义厂家服务的 API 引用
    private var yfService: IYFAPIserver? = null
    // 获取模块名
    override fun getModuleName(): String = "YFHardwareModule"
    // 初始化方法：尝试绑定厂家的远程服务
    override fun initialize(context: Context) {
        // 创建用于绑定厂家服务的意图
        val intent = Intent("com.yf.apiserver.YFAPIserver")
        // 指定厂家服务的包名
        intent.setPackage("com.yf.apiserver")

        // 执行异步服务绑定逻辑
        context.bindService(intent, object : ServiceConnection {
            // 当服务连接成功时的回调
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                // 将 Binder 转换为 AIDL 接口实例
                yfService = IYFAPIserver.Stub.asInterface(service)
                // 通过厂家接口隐藏底部虚拟按键导航栏
                yfService?.yfsetNavigationBarVisibility(false)
                // 通过厂家接口隐藏顶部状态栏
                yfService?.yfsetStatusBarVisibility(false)
            }
            // 当服务意外断开时的回调
            override fun onServiceDisconnected(name: ComponentName?) {
                // 清空 API 引用
                yfService = null
            }
        }, Context.BIND_AUTO_CREATE) // 如果服务未运行则自动创建
    }
    // 启动方法：此模块暂无额外启动逻辑
    override fun start() {}
    // 停止方法：在模块释放时恢复系统界面显示
    override fun stop() {
        // 尝试恢复顶部状态栏（可选，视具体业务需求而定）
        yfService?.yfsetStatusBarVisibility(true)
    }
}