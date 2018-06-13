package me.tevinjeffrey.kubernetes.entities.requests

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginRequest(
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null
) : Parcelable
