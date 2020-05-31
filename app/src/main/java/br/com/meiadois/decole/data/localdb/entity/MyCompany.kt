package br.com.meiadois.decole.data.localdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.meiadois.decole.data.model.Segment

@Entity
data class MyCompany(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val name: String,
    val thumbnail: String,
    val segment: String
)