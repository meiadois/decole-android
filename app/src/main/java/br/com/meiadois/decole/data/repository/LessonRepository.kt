package br.com.meiadois.decole.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.localdb.entity.Lesson
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.preferences.PreferenceProvider
import br.com.meiadois.decole.util.extension.parseToLessonEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

// TODO colocar um observable em um companion e observar ele no activity, esse observable vai ter o erro de timeout
class LessonRepository(
    private val client: DecoleClient,
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
) : RequestHandler() {

    suspend fun getLessons(routeId: Long): LiveData<List<Lesson>> {
        return withContext(Dispatchers.IO) {
            val lastFetch = prefs.getLastLessonFetch(routeId)
            if (lastFetch == 0L || isLessonFetchNeeded(Date(lastFetch))) {
                fetchLessons(routeId)
            }
            db.getLessonDao().findLessonsByRoute(routeId)
        }
    }

    suspend fun fetchLessons(routeId: Long) {
        val res = callClient { client.routeDetails(routeId) }
        prefs.saveLastLessonFetch(routeId, System.currentTimeMillis())
        db.getLessonDao().updateLessons(res.lessons.parseToLessonEntity(routeId))
    }

    @SuppressLint("SimpleDateFormat")
    private fun isLessonFetchNeeded(lastFetch: Date): Boolean {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val now = Date()
        val nowWithZeroTime = formatter.parse(formatter.format(now))
        val lastFetchWithZeroTime = formatter.parse(formatter.format(lastFetch))

        return lastFetchWithZeroTime!!.before(nowWithZeroTime)
    }
}