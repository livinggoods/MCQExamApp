package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Exam {

    @SerializedName("archived")
    @Expose
    var archived: Boolean? = null
    @SerializedName("country")
    @Expose
    var country: String? = null
    @SerializedName("created_by")
    @Expose
    var createdBy: Any? = null
    @SerializedName("date_created")
    @Expose
    var dateCreated: String? = null
    @SerializedName("exam_status")
    @Expose
    var examStatus: Any? = null
    @SerializedName("exam_status_id")
    @Expose
    var examStatusId: Any? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("passmark")
    @Expose
    var passmark: Any? = null
    @SerializedName("questions")
    @Expose
    var questions: List<Question>? = null
    @SerializedName("title")
    @Expose
    var title: String? = null

}