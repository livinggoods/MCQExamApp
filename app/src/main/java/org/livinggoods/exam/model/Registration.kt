package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Registration {


    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("cu_name")
    @Expose
    var cuName: String? = null

    @SerializedName("district")
    @Expose
    var district: String? = null

    @SerializedName("division")
    @Expose
    var division: String? = null

    @SerializedName("gender")
    @Expose
    var gender: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("subcounty")
    @Expose
    var subcounty: String? = null

    @SerializedName("village")
    @Expose
    var village: String? = null

    @SerializedName("ward")
    @Expose
    var ward: String? = null

}