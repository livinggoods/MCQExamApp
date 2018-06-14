package org.livinggoods.exam.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import java.io.FileNotFoundException
import java.io.IOException

object UtilFunctions {

    fun getJsonFromAssets(context: Context, filename: String): String? {

        var json: String? = null
        try {
            val `is` = context.getAssets().open(filename)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    fun showDialog(context: Context, title: String, message: String,
                   positive: DialogInterface.OnClickListener?, positiveText: CharSequence,
                   negative: DialogInterface.OnClickListener?, negativeText: String?) {

        val dialog = AlertDialog.Builder(context)

        dialog.setMessage(message)
        dialog.setTitle(title)

        if (positive != null && positiveText != "") {

            dialog.setPositiveButton(positiveText, positive)

        }

        if (negative != null && negativeText != null) {

            dialog.setNegativeButton(negativeText, negative)

        }

        dialog.setCancelable(false)

        dialog.create().show()
    }
}