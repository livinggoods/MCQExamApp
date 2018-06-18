package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord

class Training(): SugarRecord<Training>() {

    @SerializedName("archived")
    @Expose
    var archived: Int? = null
    @SerializedName("client_time")
    @Expose
    var clientTime: Double? = null
    @SerializedName("comment")
    @Expose
    var comment: Any? = null
    @SerializedName("country")
    @Expose
    var country: String? = null
    @SerializedName("county_id")
    @Expose
    var countyId: Any? = null
    @SerializedName("created_by")
    @Expose
    var createdBy: Int? = null
    @SerializedName("date_commenced")
    @Expose
    var dateCommenced: Any? = null
    @SerializedName("date_completed")
    @Expose
    var dateCompleted: Any? = null
    @SerializedName("date_created")
    @Expose
    var dateCreated: String? = null
    @SerializedName("district")
    @Expose
    var district: String? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("lat")
    @Expose
    var lat: Int? = null
    @SerializedName("location_id")
    @Expose
    var locationId: Any? = null
    @SerializedName("lon")
    @Expose
    var lon: Int? = null
    @SerializedName("parish_id")
    @Expose
    var parishId: Any? = null
    @SerializedName("recruitment_id")
    @Expose
    var recruitmentId: String? = null
    @SerializedName("subcounty_id")
    @Expose
    var subcountyId: Any? = null
    @SerializedName("training_name")
    @Expose
    var trainingName: String? = null
    @SerializedName("training_status_id")
    @Expose
    var trainingStatusId: Int? = null
    @SerializedName("training_venue_details")
    @Expose
    var trainingVenueDetails: Any? = null
    @SerializedName("training_venue_id")
    @Expose
    var trainingVenueId: Any? = null
    @SerializedName("ward_id")
    @Expose
    var wardId: Any? = null

    override fun toString(): String {
        return if (trainingName != null) trainingName!! else ""
    }
}