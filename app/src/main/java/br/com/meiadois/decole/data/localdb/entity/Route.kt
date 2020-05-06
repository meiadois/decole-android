package br.com.meiadois.decole.data.localdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Route(
    @PrimaryKey(autoGenerate = false) val id: String,
    val title: String,
    val description: String,
    val locked: Boolean,
    val progress: Int
)