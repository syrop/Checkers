package pl.org.seva.myapplication.main

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import pl.org.seva.myapplication.R

const val NOTIFICATION_CHANNEL_ID = "notifications"
const val NOTIFICATION_CHANNEL_NAME = "notifications"

fun createNotificationChannels(ctx: Context) {
    val notificationManager =
            ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT)
    channel.setSound(null, null)

    notificationManager.createNotificationChannel(channel)
}

fun notify(ctx: Context, content: String) {
    val notificationManager =
            ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notificationBuilder = Notification.Builder(ctx, NOTIFICATION_CHANNEL_NAME)

    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Title")
            .setContentText(content)
            .setAutoCancel(true)

    notificationManager.notify(0, notificationBuilder.build())
}
