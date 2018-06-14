package org.livinggoods.exam.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.google.gson.Gson
import org.livinggoods.exam.R
import org.livinggoods.exam.activity.adapter.ExamListAdapter
import org.livinggoods.exam.model.Exam
import org.livinggoods.exam.util.UtilFunctions
import com.google.gson.reflect.TypeToken



class ExamListActivity : AppCompatActivity() {

    lateinit var lvExams: ListView
    lateinit var adapter: ExamListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_list)

        lvExams = findViewById<ListView>(R.id.lv_exams)
        val examList = getExamList()
        adapter = ExamListAdapter(this, if (examList != null) examList else mutableListOf<Exam>())
        lvExams.adapter = adapter

        lvExams.setOnItemClickListener { parent, view, position, id ->
            val exam = adapter.getItem(position)
            val intent = Intent(this@ExamListActivity, TakeExamActivity::class.java)
            startActivity(intent)
        }
    }

    fun getExamList(): MutableList<Exam>? {
        val json = UtilFunctions.getJsonFromAssets(this, "sample_exam.json")
        if (json == null) return null
        val gson = Gson()
        val listType = object : TypeToken<ArrayList<Exam>>() {}.type
        val model = gson.fromJson<MutableList<Exam>>(json, listType)
        return model
    }
}
