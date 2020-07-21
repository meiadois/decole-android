package br.com.meiadois.decole.data.localdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.meiadois.decole.data.localdb.entity.Lesson

@Dao
interface LessonDAO {
    @Query("SELECT * FROM Lesson WHERE routeId = :routeId")
    fun findLessonsByRoute(routeId: Long): LiveData<List<Lesson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLessons(lessons: List<Lesson>)
}