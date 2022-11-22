package com.mavino.simplehlsdownloader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class MainActivity : AppCompatActivity() {

    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workManager = WorkManager.getInstance(this)

        val request = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .build()

        workManager.enqueue(request)
    }
}