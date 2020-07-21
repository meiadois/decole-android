package br.com.meiadois.decole.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Like(
    val id: Int,
    val status: String,
    val partnerCompany: Company,
    val userCompany: Company,
    val isSender: Boolean = false,
    val acceptedAt: String? = null
) : Parcelable