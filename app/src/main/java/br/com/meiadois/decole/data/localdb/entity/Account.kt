package br.com.meiadois.decole.data.localdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Account(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val userName: String,
    val channelName: String,
    val channelCategory: String
)