package org.livinggoods.exam.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import org.livinggoods.exam.R
import org.livinggoods.exam.network.APIClient
import retrofit2.Call
import java.io.IOException

open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showConnectionError(call: Call<ResponseBody>?, t: Throwable?) {

        t!!.printStackTrace()

        var error = ""

        if (t is IOException) {
            if (APIClient.isConnected(this@BaseActivity)) {
                error = getString(R.string.host_not_avaiable)
            } else {
                error = getString(R.string.connection_timed_out)
            }

        } else if (t is IllegalStateException) {
            error = getString(R.string.conversion_error)
        } else {
            error = getString(R.string.generic_error)
        }

        Toast.makeText(this@BaseActivity, error, Toast.LENGTH_LONG).show()
    }
}