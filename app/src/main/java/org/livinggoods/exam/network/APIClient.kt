package org.livinggoods.exam.network

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.livinggoods.exam.persistence.SessionManager
import org.livinggoods.exam.util.UtilFunctions
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


object APIClient {

    private val TAG = "APIClient"

    val HEADER_CACHE_CONTROL = "Cache-Control"
    val HEADER_PRAGMA = "Pragma"
    val HEADER_APPLICATION_VERSION = "Application"

    private var retrofit: Retrofit? = null

    fun getClient(context: Context): Retrofit {

        val session = SessionManager(context)

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor(provideOfflineCacheInterceptor(context))
                .addNetworkInterceptor(provideCacheInterceptor(context))
                .cache(provideCache(context))
                .build()

        val gson = UtilFunctions.getGsonSerializer()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(session.cloudEndpoint)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build()
        }
        return retrofit!!
    }

    private fun provideCache(mContext: Context): Cache? {
        var cache: Cache? = null

        try {
            cache = Cache(File(mContext.cacheDir, "http-cache"),
                    (10 * 1024 * 1024).toLong()) // 10 MB
        } catch (e: Exception) {
            Log.e(TAG, "Could not create Cache!")
        }

        return cache
    }

    private fun provideCacheInterceptor(mContext: Context): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())

            val cacheControl: CacheControl

            if (isConnected(mContext)) {
                cacheControl = CacheControl.Builder()
                        .maxAge(0, TimeUnit.HOURS)
                        .build()
            } else {
                cacheControl = CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build()
            }

            response.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build()

        }
    }

    private fun provideOfflineCacheInterceptor(mContext: Context): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()

            if (!isConnected(mContext)) {
                val cacheControl = CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build()

                request = request.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .cacheControl(cacheControl)
                        .build()
            }

            chain.proceed(request)
        }
    }

    fun isConnected(mContext: Context): Boolean {
        try {
            val e = mContext.getSystemService(
                    Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
            val activeNetwork = e.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        } catch (e: Exception) {
            Log.w(TAG, e.toString())
        }

        return false
    }
}