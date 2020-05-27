package br.com.meiadois.decole.data.localdb.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserCompany (
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    ) val company: Company
)