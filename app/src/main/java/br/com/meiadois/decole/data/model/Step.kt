package br.com.meiadois.decole.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Step(val text: String, val positionX: Int, val positionY: Int) : Parcelable