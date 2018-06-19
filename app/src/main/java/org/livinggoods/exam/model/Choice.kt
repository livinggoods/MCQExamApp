package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord

class Choice(): SugarRecord<Choice>() {

    @SerializedName("allocated_marks")
    @Expose
    var allocatedMarks: Any? = null
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
    @SerializedName("id")
    @Expose
    var choiceId: Int? = null
    @SerializedName("is_answer")
    @Expose
    var isAnswer: Boolean? = null
    @SerializedName("question_choice")
    @Expose
    var questionChoice: String? = null
    @SerializedName("question_id")
    @Expose
    var questionId: Int? = null

    var localQuestionId: Long? = null

}