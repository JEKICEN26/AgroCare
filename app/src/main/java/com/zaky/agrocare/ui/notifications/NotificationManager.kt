package com.zaky.agrocare.ui.notifications

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zaky.agrocare.MainActivity
import com.zaky.agrocare.R
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object NotificationManager {
    private val notifications = mutableListOf<NotificationItem>()
    private const val CHANNEL_ID = "agrocare_channel"
    private var isChannelCreated = false

    init {
        // Tambahkan promo default
        notifications.add(
            NotificationItem(
                title = "Promo Diskon 10%!",
                description = "Dapatkan potongan 10% untuk pembelian bibit cabai rawit khusus hari ini. Jangan sampai kehabisan!",
                timestamp = "2 jam yang lalu"
            )
        )
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isChannelCreated) {
            val name = "AgroCare Notifications"
            val descriptionText = "Notifikasi untuk pesanan dan info AgroCare"
            val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: android.app.NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
            isChannelCreated = true
        }
    }

    fun getNotifications(): List<NotificationItem> {
        return notifications.toList()
    }

    fun addNotification(context: Context?, item: NotificationItem) {
        // Masukkan di urutan paling atas untuk UI In-App
        notifications.add(0, item)
        
        // Push Notification ke Android System
        if (context == null) return
        createChannel(context)
        
        // Cek izin (khusus Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return // Belum diizinkan, jadi tidak bisa mengirim system push notification
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_agro)
            .setContentTitle(item.title)
            .setContentText(item.description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
