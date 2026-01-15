package com.company.kmslauncher

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.company.kmslauncher.databinding.ActivityMainBinding
import com.company.kmslauncher.modules.player.PlayerManager
import com.company.kmslauncher.core.LauncherEngine

/**
 * 主界面 Activity
 * 职责：生命周期管理、UI 初始化及按键拦截
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 隐藏状态栏和导航栏
        setupImmersiveMode()

        // 启动核心引擎
        LauncherEngine.start(this, binding)

        // 初始化播放器
        PlayerManager.initAndStart(this, binding)

        // 激活跑马灯
        binding.marqueeText.isSelected = true
    }

    private fun setupImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // 发送主板广播彻底隐藏
        sendBroadcast(Intent("com.youngfeel.hide_status_bar"))
        val navIntent = Intent("com.android.yf_set_navigation_bar")
        navIntent.putExtra("value", 0)
        sendBroadcast(navIntent)
    }

    override fun onStop() {
        super.onStop()
        // 释放资源，防止后台播放
        PlayerManager.stopAndRelease()
    }

    override fun onRestart() {
        super.onRestart()
        PlayerManager.initAndStart(this, binding)
    }
}