package com.amirhusseinsoori.sampleworkmanger.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.work.*
import com.amirhusseinsoori.sampleworkmanger.util.Constance
import com.amirhusseinsoori.sampleworkmanger.worker.Worker
import com.amirhusseinsoori.sampleworkmanger.R
import kotlinx.android.synthetic.main.worker_fragment.*
import java.util.concurrent.TimeUnit

class FragmentWorker : Fragment(R.layout.worker_fragment) {
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
    lateinit var  workManager: WorkManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workManager=WorkManager.getInstance(requireContext())


        // This transformation makes sure that whenever th current work Id changes  the workState
        // the Ui is Listening to change

        outputWorkInfoItems = workManager.getWorkInfosByTagLiveData(Constance.TAG_OUTPUT)


        constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        // send data to worker
        data = Data.Builder().putString(Constance.KEY_TASK_DES, "hey the work data sending").build()

        // create the WorkRequest  OnTime
        requestOneTime = OneTimeWorkRequest.Builder(Worker::class.java)
            .setInputData(data)
            .addTag(Constance.TAG_OUTPUT)
            .setConstraints(constraints)
            .build()

        btn_workerF_onTime.setOnClickListener {
            workManager.beginWith(requestOneTime).enqueue()

        }

        // If We wanted our job to run periodically , we can use PeriodicWorkRequest instead
        requestPeriodicWork = PeriodicWorkRequestBuilder<Worker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES


        ).setInputData(data)
            .addTag(Constance.TAG_OUTPUT)
            .setConstraints(constraints)
            .build()


        btn_workerF_PeriodicWork.setOnClickListener {
            workManager.enqueue(requestPeriodicWork)
        }


        //we used id for know state RequestPeriodicWork
        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(requestPeriodicWork.id).observe(
            viewLifecycleOwner, { t ->
                //get data From workManger
                if (t != null) {
                    if (t.state.isFinished) {
                        val dataOutPut: Data = t.outputData
                        val output = dataOutPut.getString(Constance.KEY_TASK_DES_OUT_PUT)
                        txt_workerF_WorkManager.append("$output \n")
                    }


                    val status = t.state.name
                    txt_workerF_WorkManager.append("$status\n")
                }
            }
        )
    }
}