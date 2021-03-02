package com.amirhusseinsoori.sampleworkmanger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.work.*
import com.amirhusseinsoori.sampleworkmanger.Constance.KEY_TASK_DES
import com.amirhusseinsoori.sampleworkmanger.Constance.KEY_TASK_DES_OUT_PUT
import com.amirhusseinsoori.sampleworkmanger.Constance.TAG_OUTPUT
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    lateinit var requestPeriodicWork: PeriodicWorkRequest
    lateinit var data: Data
    lateinit var constraints: Constraints
    lateinit var requestOneTime: OneTimeWorkRequest


    //  WorkInfo let's us know state of workRequest:blocked,cancelled,enqueued,failed
    //  running , success


    //  You can get LiveDate<WorkInfo> in 3 different ways:
    //  1. Using the unique ID of a workRequest
    //  2.Using the workRequest's unique chain name
    //  3.the tag name of a WorkRequest that you can optionally add

    lateinit var outputWorkInfoItems: LiveData<List<WorkInfo>>
    private val workManager: WorkManager = WorkManager.getInstance(this)

    init {
        //This transformation makes sure that whenever th current work Id changes  the workState
        // the Ui is Listening to change

        outputWorkInfoItems = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        // send data to worker
        data = Data.Builder().putString(KEY_TASK_DES, "hey the work data sending").build()

        // create the WorkRequest  OnTime
        requestOneTime = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(data)
            .addTag(TAG_OUTPUT)
            .setConstraints(constraints)
            .build()

        btn_WorkManager_onTime.setOnClickListener {
            workManager.beginWith(requestOneTime).enqueue()


        }


        // If We wanted our job to run periodically , we can use PeriodicWorkRequest instead
        requestPeriodicWork = PeriodicWorkRequestBuilder<MyWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES


        ).setInputData(data)
            .addTag(TAG_OUTPUT)
            .setConstraints(constraints)
            .build()


        btn_WorkManager_PeriodicWork.setOnClickListener {
            workManager.enqueue(requestPeriodicWork)
        }


        //we used id for know state Request
        workManager.getWorkInfoByIdLiveData(requestPeriodicWork.id).observe(
            this, { t ->
                //get data From workManger
                if (t != null) {
                    if (t.state.isFinished) {
                        val dataOutPut: Data = t.outputData
                        val output = dataOutPut.getString(KEY_TASK_DES_OUT_PUT)
                        txt_WorkManager.append("$output \n")
                    }


                    val status = t.state.name
                    txt_WorkManager.append("$status\n")
                }
            }
        )


    }
}