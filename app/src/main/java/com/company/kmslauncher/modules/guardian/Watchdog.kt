package com.company.kmslauncher.modules.guardian

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.company.kmslauncher.MainActivity

/**
 * 软件层看门狗模块
 * 职责：负责应用退出后的定时拉起（逻辑自启）
 */
object Watchdog {

    private const val RESTART_DELAY = 60 * 1000L // 退出后一分钟启动

    /**
     * 设置 AlarmManager 定时任务，在应用退出后拉起
     */
    fun scheduleRestart(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 使用最高精度的定时唤醒
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + RESTART_DELAY,
            pendingIntent
        )
    }

    /**
     * 取消定时任务
     */
    fun cancelRestart(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}