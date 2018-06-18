package org.livinggoods.exam.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Registration {

    @SerializedName("added_by")
    @Expose
    var addedBy: Int? = null
    @SerializedName("brac")
    @Expose
    var brac: Int? = null
    @SerializedName("brac_chp")
    @Expose
    var bracChp: Int? = null
    @SerializedName("branch_transport")
    @Expose
    var branchTransport: Int? = null
    @SerializedName("chew_name")
    @Expose
    var chewName: String? = null
    @SerializedName("chew_number")
    @Expose
    var chewNumber: String? = null
    @SerializedName("comment")
    @Expose
    var comment: String? = null
    @SerializedName("community_membership")
    @Expose
    var communityMembership: Int? = null
    @SerializedName("country")
    @Expose
    var country: String? = null
    @SerializedName("cu_name")
    @Expose
    var cuName: String? = null
    @SerializedName("date_added")
    @Expose
    var dateAdded: Any? = null
    @SerializedName("date_moved")
    @Expose
    var dateMoved: Int? = null
    @SerializedName("district")
    @Expose
    var district: String? = null
    @SerializedName("division")
    @Expose
    var division: String? = null
    @SerializedName("education")
    @Expose
    var education: Int? = null
    @SerializedName("english")
    @Expose
    var english: Int? = null
    @SerializedName("feature")
    @Expose
    var feature: String? = null
    @SerializedName("financial_accounts")
    @Expose
    var financialAccounts: Int? = null
    @SerializedName("gender")
    @Expose
    var gender: String? = null
    @SerializedName("households")
    @Expose
    var households: Int? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("interview")
    @Expose
    var interview: List<Any>? = null
    @SerializedName("is_chv")
    @Expose
    var isChv: Int? = null
    @SerializedName("is_gok_trained")
    @Expose
    var isGokTrained: Int? = null
    @SerializedName("languages")
    @Expose
    var languages: String? = null
    @SerializedName("link_facility")
    @Expose
    var linkFacility: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("occupation")
    @Expose
    var occupation: String? = null
    @SerializedName("parish")
    @Expose
    var parish: String? = null
    @SerializedName("phone")
    @Expose
    var phone: String? = null
    @SerializedName("proceed")
    @Expose
    var proceed: Int? = null
    @SerializedName("recruitment")
    @Expose
    var recruitment: String? = null
    @SerializedName("recruitment_transport")
    @Expose
    var recruitmentTransport: Int? = null
    @SerializedName("referral")
    @Expose
    var referral: String? = null
    @SerializedName("referral_id")
    @Expose
    var referralId: String? = null
    @SerializedName("referral_number")
    @Expose
    var referralNumber: String? = null
    @SerializedName("referral_title")
    @Expose
    var referralTitle: String? = null
    @SerializedName("subcounty")
    @Expose
    var subcounty: String? = null
    @SerializedName("synced")
    @Expose
    var synced: Int? = null
    @SerializedName("trainings")
    @Expose
    var trainings: String? = null
    @SerializedName("vht")
    @Expose
    var vht: Int? = null
    @SerializedName("village")
    @Expose
    var village: String? = null
    @SerializedName("ward")
    @Expose
    var ward: String? = null

}