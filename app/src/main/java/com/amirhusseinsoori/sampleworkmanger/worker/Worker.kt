package com.amirhusseinsoori.sampleworkmanger.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.amirhusseinsoori.sampleworkmanger.util.Constance.KEY_TASK_DES
import com.amirhusseinsoori.sampleworkmanger.util.Constance.KEY_TASK_DES_OUT_PUT
import com.amirhusseinsoori.sampleworkmanger.R
//Use  CoroutineWorker instead of Worker
// A CoroutineWorker is given a maximum of ten minutes to finish its execution and return a
// After this time has expired, the worker will be signalled to stop.
class Worker(var _Context: Context, workerParams: WorkerParameters?) : CoroutineWorker(
    _Context, workerParams!!
) {

    lateinit var manager: NotificationManager
    lateinit var channel: NotificationChannel
    lateinit var getData: Data
    lateinit var sendDataFromWorker: Data
    //by default doWork Dispatcher default
    override suspend fun doWork(): Result {

        return try {
            //getDate From Activity  with Key
            val des: String? = inputData.getString(KEY_TASK_DES)

            // run task
            displayNotification("hey I am Your Work  ", "$des")
            Log.e("TAG", "doWork:   ", )


            // Send Data From Worker  with Key
            //with workDataOf you can map one or multiple key
            sendDataFromWorker = workDataOf(KEY_TASK_DES_OUT_PUT to "task finish Successfully")

            Result.success(sendDataFromWorker)
        } catch (ex: Throwable) {

            //If there were errors ,return FAILURE
            Result.failure()

            //  In Some case,we may want to return Result.retry() to indicate that want retry
            //  running this work at a  later time
        }


    }


    private fun displayNotification(task: String, des: String) {
        manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                "amirhussein",
                "amirhussein",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        //createNotification
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, "amirhussein")
                .setContentTitle(task)
                .setContentText(des)
                .setSmallIcon(R.mipmap.ic_launcher)
        manager.notify(1, builder.build())

    }
}