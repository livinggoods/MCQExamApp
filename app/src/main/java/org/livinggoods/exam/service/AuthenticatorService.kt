package org.livinggoods.exam.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AuthenticatorService : Service() {

    // Instance field that stores the authenticator object
    private var expansionAuthenticator: ExamAuthenticator? = null

    override fun onCreate() {
        Log.e("MyAuthenticatorService", "onCreate")
        // Create a new authenticator object
        expansionAuthenticator = ExamAuthenticator(this)
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    override fun onBind(intent: Intent): IBinder? {
        Log.e("MyAuthenticatorService", "onBind")
        return expansionAuthenticator!!.getIBinder()
    }

}
