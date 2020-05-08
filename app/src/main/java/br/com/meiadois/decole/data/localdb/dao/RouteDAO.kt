package br.com.meiadois.decole.data.localdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.meiadois.decole.data.localdb.entity.Route

@Dao
interface RouteDAO {

    @Query("SELECT * FROM Route WHERE id = :id")
    fun findByPk(id: Long): LiveData<Route>

    @Query("SELECT * FROM Route")
    fun findAll(): LiveData<List<Route>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRoutes(routes: List<Route>)

}