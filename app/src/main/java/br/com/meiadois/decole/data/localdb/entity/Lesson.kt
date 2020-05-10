package br.com.meiadois.decole.data.localdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Lesson(
    @PrimaryKey(autoGenerate = false) val id: Long,
    val title: String,
    val completed: Boolean,
    val routeId: Long
)