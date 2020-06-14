package br.com.meiadois.decole.data.localdb.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MyCompany(
    @Embedded val company: Company,
    @Relation(
        parentColumn = "segmentId",
        entityColumn = "id"
    )
    val segment: Segment?
)