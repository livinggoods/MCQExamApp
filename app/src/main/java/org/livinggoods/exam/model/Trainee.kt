package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord

class Trainee(): SugarRecord<Trainee>() {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("training_id")
    @Expose
    var trainingId: Int? = null
    @SerializedName("country")
    @Expose
    var country: String? = null
    @SerializedName("branch")
    @Expose
    var branch: String? = null
    @SerializedName("cohort")
    @Expose
    var cohort: String? = null
    @SerializedName("chp_code")
    @Expose
    var chpCode: String? = null
    @SerializedName("comment")
    @Expose
    var comment: String? = null

}