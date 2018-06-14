package org.livinggoods.exam.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.livinggoods.exam.R
import org.livinggoods.exam.model.Exam

class ExamListAdapter(context: Context, exams: MutableList<Exam>): BaseAdapter() {

    var context: Context
    var exams: MutableList<Exam>
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

        return view
    }
}