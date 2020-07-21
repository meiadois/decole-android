package br.com.meiadois.decole.data.localdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.meiadois.decole.data.localdb.entity.CURRENT_USER_ID
import br.com.meiadois.decole.data.localdb.entity.User

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: User): Long

    @Query("SELECT * FROM User WHERE uid = $CURRENT_USER_ID")
    fun find(): LiveData<User>

    @Query("SELECT jwt FROM User WHERE uid = $CURRENT_USER_ID")
    fun findJWT(): String?

    @Query("UPDATE User SET introduced = :boolean WHERE jwt = :jwt")
    fun updateIntroducedStatus(jwt: String, boolean: Boolean)
}