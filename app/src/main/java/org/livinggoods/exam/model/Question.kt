package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord

class Question(): SugarRecord<Question>() {

    @SerializedName("allocated_marks")
    @Expose
    var allocatedMarks: Int? = null
    @SerializedName("archived")
    @Expose
    var archived: Boolean? = null
    @SerializedName("batch_id")
    @Expose
    var batchId: String? = null
    @SerializedName("choices")
    @Expose
    var choices: List<Choice>? = null
    @SerializedName("country")
    @Expose
    var country: String? = null
    @SerializedName("created_by")
    @Expose
    var createdBy: String? = null
    @SerializedName("date_created")
    @Expose
    var dateCreated: String? = null
    @SerializedName("id")
    @Expose
    var questionId: Int? = null
    @SerializedName("question")
    @Expose
    var question: String? = null
    @SerializedName("question_type_id")
    @Expose
    var questionTypeId: Int? = null
    @SerializedName("topics")
    @Expose
    var topics: List<Topic>? = null
//    @SerializedName("type")
//    @Expose
//    var type: Any? = null

    var localExamId: Long? = null

}