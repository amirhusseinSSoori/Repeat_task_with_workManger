package com.amirhusseinsoori.sampleworkmanger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.amirhusseinsoori.sampleworkmanger.Constance.KEY_TASK_DES
import com.amirhusseinsoori.sampleworkmanger.Constance.KEY_TASK_DES_OUT_PUT
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    lateinit var request: PeriodicWorkRequest
    lateinit var data: Data
    lateinit var constraints: Constraints


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        constraints = Constraints.Builder().setRequiresCharging(true).build()

        //send data to worker
        data = Data.Builder().putString(KEY_TASK_DES, "hey the work data sending").build()
/*
        var request: OneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .build()*/

        request = PeriodicWorkRequest.Builder(
            MyWorker::class.java,
            5, TimeUnit.MINUTES
        ).setInputData(data)
            .setConstraints(constraints)
            .build()




        btn_WorkManager.setOnClickListener {
            WorkManager.getInstance(applicationContext).enqueue(request)

        }


        WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(request.id).observe(
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