package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord

class Trainee {

    @SerializedName("branch")
    @Expose
    var branch: String? = null

    @SerializedName("class_id")
    @Expose
    var classId: Int? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("registration")
    @Expose
    var registration: Registration? = null

    @SerializedName("registration_id")
    @Expose
    var registrationId: String? = null

    @SerializedName("training_id")
    @Expose
    var trainingId: String? = null

    override fun toString(): String {

        return if (registration != null) registration!!.name!! else trainingId!!
    }
}