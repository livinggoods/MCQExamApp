package org.livinggoods.exam.activity.adapter

import android.content.Context
import android.graphics.Color
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.orm.SugarRecord
import mehdi.sakout.fancybuttons.FancyButton
import org.livinggoods.exam.R
import org.livinggoods.exam.model.Exam
import org.livinggoods.exam.util.Constants

class ExamListAdapter(internal var context: Context, internal var exams: MutableList<Exam>, internal var listener: OnExamListItemClicked): BaseAdapter() {

    var inflater: LayoutInflater

    init {
        this.context = context
        this.exams = exams
        this.inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getItem(position: Int): Any {
        return exams.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return exams.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view = convertView
        val exam = exams.get(position)

        if (convertView == null)
            view = inflater.inflate(R.layout.layout_exam_item, parent, false)

        val tvExamTitle = view?.findViewById<TextView>(R.id.tv_exam_title)!!
        tvExamTitle.text = exam.title

        val tvExamStatus = view.findViewById<TextView>(R.id.tv_exam_status)
        tvExamStatus.text = exam.localExamStatus!!.toUpperCase()
        val btnStartExam = view.findViewById<FancyButton>(R.id.btn_start_exam)
        btnStartExam.setOnClickListener{ listener.startExam(exam) }
        if (exam.localExamStatus == Constants.EXAM_STATUS_DONE) {
            tvExamStatus.setTextColor(Color.GREEN)
            btnStartExam.visibility = View.GONE

        } else {
            tvExamStatus.setTextColor(Color.RED)
            btnStartExam.visibility = View.VISIBLE
        }

        return view
    }

    fun updateList() {

        exams = SugarRecord.findAll(Exam::class.java).asSequence().toMutableList()
        notifyDataSetChanged()
    }

    interface OnExamListItemClicked {
        fun startExam(exam: Exam)
    }
}