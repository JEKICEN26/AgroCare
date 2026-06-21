package com.zaky.agrocare.ui.notifications

object NotificationManager {
    private val notifications = mutableListOf<NotificationItem>()

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

    fun getNotifications(): List<NotificationItem> {
        return notifications.toList()
    }

    fun addNotification(item: NotificationItem) {
        // Masukkan di urutan paling atas
        notifications.add(0, item)
    }
}
