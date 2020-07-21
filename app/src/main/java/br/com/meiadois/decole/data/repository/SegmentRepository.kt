package br.com.meiadois.decole.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.localdb.entity.Segment
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.response.SegmentResponse
import br.com.meiadois.decole.data.preferences.PreferenceProvider
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.isFetchNeeded
import br.com.meiadois.decole.util.extension.toSegmentEntityList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class SegmentRepository(
    private val client: DecoleClient,
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
) : RequestHandler() {
    private val segment = MutableLiveData<List<Segment>>()

    init {
        segment.observeForever {
            saveSegments(it)
        }
    }

    suspend fun getAllSegmentsHasCompanies(): List<SegmentResponse> {
        return callClient {
            client.getAllSegmentsHasCompanies()
        }
    }

    private suspend fun fetchSegments(): List<SegmentResponse> {
        val response = callClient { client.getAllSegments() }
        segment.postValue(response.toSegmentEntityList())
        return response
    }

    private fun saveSegments(segments: List<Segment>) {
        prefs.saveLastSegmentFetch(System.currentTimeMillis())
        Coroutines.io {
            db.getSegmentDao().upsert(segments)
        }
    }

    suspend fun getAllSegments(): LiveData<List<Segment>> {
        return withContext(Dispatchers.IO) {
            val lastFetch = prefs.getLastSegmentFetch()
            if (lastFetch == 0L || Date(lastFetch).isFetchNeeded())
                return@withContext MutableLiveData(fetchSegments().toSegmentEntityList())
            db.getSegmentDao().getAllSegments()
        }
    }
}