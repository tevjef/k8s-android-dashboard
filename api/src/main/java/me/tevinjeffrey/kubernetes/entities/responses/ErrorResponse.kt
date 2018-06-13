package me.tevinjeffrey.kubernetes.entities.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ErrorResponse(
    @SerializedName("error") val error: String? = null,
    @SerializedName("error_description") val description: String? = null
) : Parcelable
