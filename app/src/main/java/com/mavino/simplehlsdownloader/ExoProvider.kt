package com.mavino.simplehlsdownloader

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.*
import java.io.File

object ExoProvider {

    private val cacheSize: Long = 300 * 1024 * 1024

    private lateinit var evictor: LeastRecentlyUsedCacheEvictor

    private lateinit var dataBaseProvider: StandaloneDatabaseProvider

    private lateinit var cacheSink: CacheDataSink.Factory

    private lateinit var downStreamFactory: FileDataSource.Factory

    private var cache: SimpleCache? = null

    lateinit var cacheDataSource: CacheDataSource

    lateinit var cacheDataSourceFactory: CacheDataSource.Factory

    lateinit var cacheDirectory: File

    private fun getDirectory(context: Context): File{
        cacheDirectory = File(
            context.getExternalFilesDir(null),
            "download"
        )
        return cacheDirectory
    }

    private fun getCache(context: Context): SimpleCache?{
        if (cache == null){
            cache = SimpleCache(
                getDirectory(context),
                NoOpCacheEvictor(),
                getDatabaseProvider(context)
            )
        }
        return cache
    }

    private fun getDatabaseProvider(context: Context): StandaloneDatabaseProvider{
        dataBaseProvider = StandaloneDatabaseProvider(context)
        return dataBaseProvider
    }

    private fun getUpStream(context: Context): DefaultDataSource.Factory {
        //setup upstream factory (network streaming)
        return DefaultDataSource.Factory(
            context,
            DefaultHttpDataSource.Factory()
        )
    }

    private fun getDownStream(): FileDataSource.Factory{
        //setup downstream factory (local streaming)
        downStreamFactory = FileDataSource.Factory()
        return downStreamFactory
    }

    fun getSink(context: Context):CacheDataSink.Factory{
        //setup cache sink
        cacheSink = CacheDataSink.Factory()
            .setCache(getCache(context)!!)
        return cacheSink
    }

    fun getCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
        cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(getCache(context)!!)
            .setCacheWriteDataSinkFactory(getSink(context))
            .setUpstreamDataSourceFactory(getUpStream(context))
            .setCacheReadDataSourceFactory(getDownStream())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            .setEventListener(cacheEventListener())

        return cacheDataSourceFactory
    }

    fun getCacheDataSource(context: Context): CacheDataSource{
        cacheDataSource = getCacheDataSourceFactory(context).createDataSource()
        return cacheDataSource
    }

    private fun cacheEventListener(): CacheDataSource.EventListener{
        return object : CacheDataSource.EventListener{
            override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
                Log.d(DownloadWorker.TAG, "onCachedBytesRead: from ExoApplication  $cacheSizeBytes  $cachedBytesRead")
            }

            override fun onCacheIgnored(reason: Int) {
                Log.d(DownloadWorker.TAG, "onCachedBytesRead: from ExoApplication Error $reason")
            }
        }
    }

}