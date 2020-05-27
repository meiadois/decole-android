package br.com.meiadois.decole.data.localdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Segment(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val name: String
)