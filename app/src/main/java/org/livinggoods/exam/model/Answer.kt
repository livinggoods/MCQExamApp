package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord

class Answer(): SugarRecord<Answer>() {
    @SerializedName("question_id")
    @Expose
    var questionId: Int? = null

    @SerializedName("choice_id")
    @Expose
    var choiceId: Int? = null

    @SerializedName("is_correct")
    @Expose
    var isCorrect: Boolean? = null

    @SerializedName("allocated_marks")
    @Expose
    var allocatedMarks: Int? = null
}