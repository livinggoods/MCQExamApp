package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord

class Answer(): SugarRecord<Answer>() {

    @SerializedName("training_exam_id")
    @Expose
    var trainingExamId: String? = null

    @SerializedName("trainee_id")
    @Expose
    var traineeId: String? = null

    @SerializedName("question_id")
    @Expose
    var questionId: Int? = null

    @SerializedName("question_score")
    @Expose
    var questionScore: Int? = null


    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("answer")
    @Expose
    var answer: String? = null

    @SerializedName("is_correct")
    @Expose
    var isCorrect: Boolean? = null

    override fun toString(): String {

        return "${trainingExamId}-${traineeId}-${questionId}-${questionScore}-${answer}"
    }
}