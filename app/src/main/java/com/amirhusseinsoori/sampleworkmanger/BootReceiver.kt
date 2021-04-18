package com.amirhusseinsoori.sampleworkmanger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.amirhusseinsoori.sampleworkmanger.util.Constance
import com.amirhusseinsoori.sampleworkmanger.worker.Worker
import java.util.concurrent.TimeUnit

class BootReceiver:BroadcastReceiver() {
    lateinit var constraints: Constraints
    lateinit var requestOneTime: OneTimeWorkRequest
    lateinit var workManager: WorkManager


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == Intent.ACTION_BOOT_COMPLETED) {
            workManager = WorkManager.getInstance(context!!)
            constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            requestOneTime =
                OneTimeWorkRequest.Builder(Worker::class.java)
                    .setConstraints(constraints)
                    .build()

            workManager.beginWith(requestOneTime).enqueue()

        }

    }
}