package com.amirhusseinsoori.sampleworkmanger

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.amirhusseinsoori.sampleworkmanger.Constance.KEY_TASK_DES
import com.amirhusseinsoori.sampleworkmanger.Constance.KEY_TASK_DES_OUT_PUT

class MyWorker(var _Context: Context, workerParams: WorkerParameters?) : Worker(
    _Context, workerParams!!
) {

    lateinit  var manager: NotificationManager
    lateinit  var channel:NotificationChannel

    override fun doWork(): Result {
         val data=inputData
        val des=data.getString(KEY_TASK_DES)
        displayNotification("hey I am Your Work",des!!)
        val sendDataFromWorker = Data.Builder()
            .putString(KEY_TASK_DES_OUT_PUT, "task finish Successfully").build()

        Log.e("work", "doWork: ", )

        return Result.success(sendDataFromWorker)
    }


    private fun displayNotification(task: String, des: String) {
        manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            channel= NotificationChannel("amirhussein","amirhussein",NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
         //createNotification
        val builder:NotificationCompat.Builder=NotificationCompat.Builder(applicationContext,"amirhussein")
            .setContentTitle(task)
            .setContentText(des)
            .setSmallIcon(R.mipmap.ic_launcher)


        manager.notify(1,builder.build())

    }
}