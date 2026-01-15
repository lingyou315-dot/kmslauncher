package com.company.kmslauncher

import android.app.Application
import android.content.Intent
import com.company.kmslauncher.modules.guardian.Watchdog

/**
 * 应用程序全局类
 * 职责：
 * 1. 初始化硬件看门狗（针对 YF-XXI 主板）。
 * 2. 注册生命周期回调，管理应用自启计划。
 * 3. 设置系统默认 Launcher 权限。
 */
class KmsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. 初始化看门狗：开启硬件看门狗（根据主板文档接口 6）
        // 开启后，如果系统或程序卡死，硬件将强制重启，保障 24 小时运行
        sendBroadcast(Intent("com.android.yf_watchdog_on"))

        // 2. 将本程序设为默认 Launcher（根据主板文档接口 8）
        val launcherIntent = Intent("com.android.yf_set_defaultLauncher")
        launcherIntent.putExtra("pkgname", packageName)
        launcherIntent.putExtra("classname", "${packageName}.MainActivity")
        sendBroadcast(launcherIntent)

        // 3. 注册应用生命周期监听，处理退出后 1 分钟重启逻辑
        registerLifecycle()
    }

    /**
     * 注册生命周期回调
     * 当所有 Activity 停止时（应用退出），启动定时重启计划
     */
    private fun registerLifecycle() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: android.app.Activity, savedInstanceState: android.os.Bundle?) {}

            override fun onActivityStarted(activity: android.app.Activity) {
                // 只要应用处于活跃状态，就取消重启计划（喂狗/重置）
                Watchdog.cancelRestart(this@KmsApplication)
                // 同时发送硬件喂狗广播，防止硬件重启系统
                sendBroadcast(Intent("com.android.yf_watchdog_feed"))
            }

            override fun onActivityResumed(activity: android.app.Activity) {}
            override fun onActivityPaused(activity: android.app.Activity) {}

            override fun onActivityStopped(activity: android.app.Activity) {
                // 当应用被关闭或退到后台，启动 60 秒倒计时重启
                Watchdog.scheduleRestart(this@KmsApplication)
            }

            override fun onActivitySaveInstanceState(activity: android.app.Activity, outState: android.os.Bundle) {}
            override fun onActivityDestroyed(activity: android.app.Activity) {}
        })
    }
}