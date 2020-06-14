package br.com.meiadois.decole.data.localdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.meiadois.decole.data.localdb.entity.Segment

@Dao
interface SegmentDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(segments: List<Segment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(segment: Segment)

    @Query("SELECT * FROM Segment WHERE id = :id")
    suspend fun getSegmentById(id: Int): Segment

    @Query("SELECT * FROM Segment")
    fun getAllSegments(): LiveData<List<Segment>>

}