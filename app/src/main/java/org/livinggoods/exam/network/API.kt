package org.livinggoods.exam.network

import okhttp3.ResponseBody
import org.livinggoods.exam.model.Answer
import retrofit2.Call
import retrofit2.http.*

interface API {

    @Headers("Cache-Control: no-cache")
    @GET("sync/trainings")
    fun getTrainings(): Call<ResponseBody>

    @Headers("Cache-Control: no-cache")
    @GET("sync/{training_id}/trainees")
    fun getTrainees(@Path(value = "training_id", encoded = true) trainingId: String): Call<ResponseBody>

    @GET("training/{training_id}/exams")
    fun getExams(@Path(value = "training_id", encoded = true) trainingId: String): Call<ResponseBody>

    @POST("training/exam/result/save")
    fun saveExamsAnswers(@Body answers: MutableList<Answer>): Call<ResponseBody>
}