package br.com.meiadois.decole.data.localdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.meiadois.decole.data.localdb.entity.Route

@Dao
interface RouteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateRoutes(routes: List<Route>)

    @Query("SELECT * FROM Route")
    fun findAll(): LiveData<List<Route>>
}