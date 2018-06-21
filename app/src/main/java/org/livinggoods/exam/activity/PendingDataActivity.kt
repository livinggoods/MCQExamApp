package org.livinggoods.exam.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import com.orm.SugarRecord
import org.livinggoods.exam.R
import org.livinggoods.exam.model.Answer
import android.widget.ArrayAdapter



class PendingDataActivity : AppCompatActivity() {

    lateinit var tvPending: TextView
    lateinit var lvPending: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_data)

        tvPending = findViewById<TextView>(R.id.tv_pending_records)
        lvPending = findViewById<ListView>(R.id.lv_pending_records)


        val pendingData = SugarRecord.findAll(Answer::class.java).asSequence().toMutableList()
        tvPending.text = String.format(getString(R.string.pending_count), pendingData.size)

        val itemsAdapter = ArrayAdapter<Answer>(this, android.R.layout.simple_list_item_1, pendingData)
        lvPending.adapter = itemsAdapter

        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {

            android.R.id.home -> {
                finish()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}
