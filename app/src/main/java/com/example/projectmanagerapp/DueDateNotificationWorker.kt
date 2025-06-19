package com.example.projectmanagerapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DueDateNotificationWorker(private val context: Context, private val workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    companion object {
        const val NOTIFICATION_ID_KEY = "notification_id"
        const val CARD_TITLE_KEY = "card_title"
        const val CARD_ID_KEY = "card_id"
        const val CHANNEL_ID = "due_date_channel"
    }

    override suspend fun doWork(): Result {
        val cardTitle = inputData.getString(CARD_TITLE_KEY) ?: "Unknown"
        val cardId = inputData.getString(CARD_ID_KEY) ?: "Unknown"
        val notificationId = inputData.getInt(NOTIFICATION_ID_KEY, cardId.hashCode())

        showNotification(notificationId, cardTitle, "Thẻ '$cardTitle' đến hạn", cardId)

        return Result.success()
    }

    private fun showNotification(id: Int, title: String, content: String, cardId: String?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Thông báo Ngày hết hạn",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Hiển thị thông báo khi một thẻ đến hạn"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

//            putExtra("deep_link_card_id", cardId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id, notification)

    }

}