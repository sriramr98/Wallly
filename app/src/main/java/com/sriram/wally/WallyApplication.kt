package com.sriram.wally

import android.app.Application
import android.os.Environment
import com.sriram.wally.di.modules
import com.sriram.wally.utils.Logger
import com.sriram.wally.utils.SharedPrefUtils
import org.koin.android.ext.android.startKoin
import timber.log.Timber
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import com.sriram.wally.utils.Constants


class WallyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        SharedPrefUtils.init(this)

        startKoin(this, listOf(modules))

        Timber.plant(Timber.DebugTree())
        createNotificationChannel()

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.download_images_channel_name)
            val description = getString(R.string.download_images_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.IMAGE_DOWNLOAD_CHANNEL, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

}