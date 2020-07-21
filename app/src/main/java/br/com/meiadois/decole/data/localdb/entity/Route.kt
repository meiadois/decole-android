package br.com.meiadois.decole.data.localdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Route(
    @PrimaryKey(autoGenerate = false) val id: Long,
    val title: String,
    val description: String,
    val locked: Boolean,
    val lessonsCompleted: Int,
    val lessonsAvailable: Int,
    val progress: Int
)