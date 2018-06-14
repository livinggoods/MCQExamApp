package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Topic {

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
    var id: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null

}