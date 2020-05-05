package br.com.meiadois.decole.data.localdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.meiadois.decole.data.network.response.UserDTO

const val CURRENT_USER_ID = 0

@Entity
data class User(
    var jwt: String,
    var name: String,
    var email: String,
    var introduced: Boolean
){
    @PrimaryKey(autoGenerate = false)
    var uid: Int = CURRENT_USER_ID
}