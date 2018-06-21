package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import org.livinggoods.exam.util.Constants

class Exam(): SugarRecord<Exam>() {

    @SerializedName("archived")
    @Expose
    var archived: Boolean? = null
    @SerializedName("country")
    @Expose
    var country: String? = null
    @SerializedName("created_by")
    @Expose
    var createdBy: String? = null
    @SerializedName("date_created")
    @Expose
    var dateCreated: String? = null
//    @SerializedName("exam_status")
//    @Expose
//    var examStatus: Any? = null
    @SerializedName("exam_status_id")
    @Expose
    var examStatusId: Int? = null
    @SerializedName("id")
    @Expose
    var examId: String? = null
    @SerializedName("passmark")
    @Expose
    var passmark: Int? = null
    @SerializedName("questions")
    @Expose
    var questions: List<Question>? = null
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("local_exam_status")
    @Expose
    var localExamStatus: String? = Constants.EXAM_STATUS_PENDING

    @SerializedName("trainee_id")
    @Expose
    var traineeId: String? = null

}