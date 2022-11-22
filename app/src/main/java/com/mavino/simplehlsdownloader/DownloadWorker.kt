package com.mavino.simplehlsdownloader

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.offline.HlsDownloader
import java.io.IOException

class DownloadWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    override fun doWork(): Result {

        return try {
            downloadVideo("https://d3usnkytdvge4t.cloudfront.net/6675638e-ee49-42b2-8f3f-795c9f17e128_1.m3u8")
            Result.success()
        }catch (e: Exception){
            Log.d(TAG, "doWork: $e")
            Result.retry()
        }
    }


   private fun downloadVideo(url: String) {
            try {
                val mediaItem = MediaItem.fromUri(url)
                val downloadAction = HlsDownloader(
                    mediaItem,
                    ExoProvider.getCacheDataSourceFactory(applicationContext)
                )
                downloadAction.download { contentLength, bytesDownloaded, percentDownloaded ->
                    Log.d(
                        TAG, "doWork: content length: $contentLength  " +
                                "bytes downloaded: $bytesDownloaded  " +
                                "download percent: $percentDownloaded"
                    )
                }
            }catch (e: InterruptedException){
                Log.d(TAG, "downloadVideo: InException $e")
            }catch (e: IOException){
                Log.d(TAG, "downloadVideo: IOException $e")
            }catch (e: Exception){
                Log.d(TAG, "downloadVideo: Exception  $e")
            }
    }

    companion object{
        const val TAG = "AppDebug"
    }
}