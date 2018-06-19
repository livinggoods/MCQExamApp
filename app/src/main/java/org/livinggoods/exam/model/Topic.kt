package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord

class Topic(): SugarRecord<Topic>() {

    @SerializedName("added_by")
    @Expose
    var addedBy: Int? = null
    @SerializedName("archived")
    @Expose
    var archived: Int? = null
    @SerializedName("country")
    @Expose
    var country: String? = null
    @SerializedName("date_added")
    @Expose
    var dateAdded: String? = null
    @SerializedName("id")
    @Expose
    var topicId: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null

    var localQuestionId: Long? = null

}