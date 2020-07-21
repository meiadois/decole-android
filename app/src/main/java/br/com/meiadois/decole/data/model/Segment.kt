package br.com.meiadois.decole.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Segment(
    val id: Int?,
    val name: String
) : Parcelable