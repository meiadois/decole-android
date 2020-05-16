package br.com.meiadois.decole.data.localdb.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.model.RouteDetails

@Dao
interface RouteDAO {

    @Query("SELECT * FROM Route WHERE id = :id")
    fun findByPk(id: Long): LiveData<Route>

    @Query("SELECT * FROM Route")
    fun findAll(): LiveData<List<Route>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRoutes(routes: List<Route>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRoute(routes: Route)

    @Transaction
    @Query("SELECT * FROM Route WHERE id = :id")
    fun getRouteAndLessonsByPk(id: Long): LiveData<RouteDetails>

}