package org.livinggoods.exam.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class ExamSyncService : Service() {

    override fun onCreate() {
        Log.e("ExamSyncService", "onCreate fn()")
        synchronized(sSyncAdapterLock) {
            if (syncServiceAdapter == null) {
                syncServiceAdapter = ExamSyncServiceAdapter(applicationContext, true)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e("ExamSyncServiceSync", "onBind")
        return syncServiceAdapter!!.getSyncAdapterBinder()
    }

    companion object {
        private val sSyncAdapterLock = Any()
        private var syncServiceAdapter: ExamSyncServiceAdapter? = null
    }
}