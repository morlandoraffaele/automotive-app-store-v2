package com.automotive.appstore

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import okhttp3.OkHttpClient
import okio.Path.Companion.toOkioPath
import java.util.concurrent.TimeUnit

/**
 * Application entry point.
 *
 * Coil 3 does **not** register a network fetcher on its default singleton: unlike Coil 2, having
 * `coil-network-*` on the classpath is not sufficient, so the store's icon URLs failed with
 * "Unable to create a network fetcher" and every tile rendered blank.
 *
 * A loader with an explicit [OkHttpNetworkFetcherFactory] is built once here and installed as the
 * singleton, so plain `AsyncImage` calls resolve it. Disk caching matters here because the
 * catalogue is ~30 fixed icon URLs that would otherwise be re-fetched on every launch.
 */
class AppStoreApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Installed eagerly rather than through ServiceLoader: the fetcher has to be registered
        // before the first composition requests an image.
        imageLoader = buildImageLoader(this)
    }

    companion object {
        private const val DISK_CACHE_BYTES = 32L * 1024 * 1024

        /** The process-wide loader, built with a real network fetcher. */
        lateinit var imageLoader: ImageLoader
            private set

        private fun buildImageLoader(context: android.content.Context): ImageLoader {
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            return ImageLoader.Builder(context)
                .components { add(OkHttpNetworkFetcherFactory(callFactory = { client })) }
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(context, percent = 0.25)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(context.cacheDir.resolve("icon_cache").toOkioPath())
                        .maxSizeBytes(DISK_CACHE_BYTES)
                        .build()
                }
                .build()
        }
    }
}


