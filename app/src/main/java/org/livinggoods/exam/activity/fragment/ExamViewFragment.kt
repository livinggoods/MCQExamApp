package org.livinggoods.exam.activity.fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.gson.Gson
import org.livinggoods.exam.R
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_exam_view.*
import org.livinggoods.exam.activity.TakeExamActivity
import org.livinggoods.exam.model.Answer
import org.livinggoods.exam.model.Exam
import org.livinggoods.exam.persistence.SessionManager
import org.livinggoods.exam.util.Constants
import org.livinggoods.exam.util.UtilFunctions

class ExamViewFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null

    public lateinit var webView: WebView
    lateinit var gson: Gson
    lateinit var sessionManager: SessionManager

    companion object {

        fun newInstance(): ExamViewFragment {
            val fragment = ExamViewFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gson = UtilFunctions.getGsonSerializer()
        sessionManager = SessionManager(activity!!)

        if (arguments != null) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_exam_view, container, false)

        webView = rootView.findViewById<WebView>(R.id.webview)
        webView.addJavascriptInterface(WebInterface(context!!), "Android")
        webView.isHorizontalScrollBarEnabled = false
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        webView.loadUrl("file:///android_asset/www/index.html")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                webView.loadUrl("javascript:initialize()")
            }
        }

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onPause() {
        Log.e("Chromium", "Triggered pause")
        webview.loadUrl("javascript:onPause()")
        super.onPause()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {

        fun onNextOrSubmit()
        fun onShowError(message: String)
        fun getExamJSON(): Exam
    }

    private inner class WebInterface(internal var mContext: Context) {

        @JavascriptInterface
        fun onPause(examJSON: String) {
            val exam = mListener?.getExamJSON()!!
            sessionManager.cacheOngoingExam(exam.examId.toString(), examJSON)
        }

        @JavascriptInterface
        fun getExam(): String {
            val exam = mListener?.getExamJSON()!!
            val cachedExam = sessionManager.getCachedExam(exam.examId!!.toString())
            if (cachedExam.isBlank()) {
                return  gson.toJson(mListener?.getExamJSON(), Exam::class.java)
            } else {
                return cachedExam
            }
        }

        @JavascriptInterface
        fun submitAnswers(status: Boolean, message: String, data: String, totalMarks: Int, isPassed: Boolean) {

            val parent = activity as TakeExamActivity
            val isOther = parent.isOtherApp

            if (!status) {
                mListener!!.onShowError(message)
                return
            }

            UtilFunctions.showDialog(context!!,
                    getString(R.string.confirm_submission),
                    getString(R.string.confirm_inst),
                    DialogInterface.OnClickListener { dialog, which ->

                        dialog.dismiss()

                        val listType = object : TypeToken<ArrayList<Answer>>() {}.type
                        val answers = gson.fromJson<MutableList<Answer>>(data, listType)
                        answers.forEach { answer -> answer.save() }

                        if (isOther) {
                            val intent = Intent()
                            intent.action = "org.livinggoods.exam.ACTION_TAKE_EXAM"
                            intent.putExtra("results", data)
                            activity!!.setResult(Activity.RESULT_OK, intent)
                            sessionManager.removeKey(mListener?.getExamJSON()!!.examId.toString())
                            activity!!.finish()
                        } else {

                            // TODO: Update this at some point
                            val exam = mListener?.getExamJSON()!!
                            exam.localExamStatus = Constants.EXAM_STATUS_DONE
                            exam.save()
                            val intent = Intent(TakeExamActivity.ACTION_EXAM_DONE)
                            context!!.sendBroadcast(intent)
                            sessionManager.removeKey(exam.examId.toString())
                            activity!!.finish()
                        }
                    },
                    getString(R.string.confirm),
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() },
                    getString(R.string.cancel)
            )
        }
    }

}// Required empty public constructor