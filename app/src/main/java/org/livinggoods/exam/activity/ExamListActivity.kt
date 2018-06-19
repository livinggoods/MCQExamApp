package org.livinggoods.exam.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import org.livinggoods.exam.R
import org.livinggoods.exam.activity.adapter.ExamListAdapter
import org.livinggoods.exam.model.Exam
import org.livinggoods.exam.util.UtilFunctions
import com.google.gson.reflect.TypeToken
import com.orm.SugarRecord
import org.livinggoods.exam.persistence.SessionManager


class ExamListActivity : BaseActivity() {

    lateinit var lvExams: ListView
    lateinit var adapter: ExamListAdapter
    lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this@ExamListActivity)

        if (!session.isSetup) {
            val intent = Intent(this@ExamListActivity, InitialSetupActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_exam_list)

        lvExams = findViewById<ListView>(R.id.lv_exams)
        val examList = getExamList()
        adapter = ExamListAdapter(this, if (examList != null) examList else mutableListOf<Exam>())
        lvExams.adapter = adapter

        lvExams.setOnItemClickListener { parent, view, position, id ->
            val exam = adapter.getItem(position) as Exam
            Log.e("exams", "${exam.title}, ${exam.id}")
            val intent = Intent(this@ExamListActivity, TakeExamActivity::class.java)
            intent.putExtra(TakeExamActivity.KEY_FORM_ID, exam.id)
            startActivity(intent)
        }
    }

    fun getExamList(): MutableList<Exam>? {

        val exams = SugarRecord.findAll(Exam::class.java)

        return exams.asSequence().toMutableList()
    }
}
