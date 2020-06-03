package br.com.meiadois.decole.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.localdb.entity.MyCompany
import br.com.meiadois.decole.data.localdb.entity.Segment
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.response.SegmentResponse
import br.com.meiadois.decole.data.preferences.PreferenceProvider
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.isFetchNeeded
import br.com.meiadois.decole.util.extension.toSegmentModelList
import br.com.meiadois.decole.util.extension.toSegmentModelListDb
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
                saveSegment(it)
            }
        }


        suspend fun getAllSegments(): List<SegmentResponse> {


            return callClient {
                client.getAllSegments()
            }
        }
        suspend fun getAllSegmentsHasCompanies(): List<SegmentResponse>{
            return callClient {
                client.getAllSegmentsHasCompanies()
            }
        }


        suspend fun fetchSegment() {
            val res = callClient { client.getAllSegments() }
            segment.postValue(res.toSegmentModelListDb())
        }

        private fun saveSegment(segment:List<Segment>) {
            prefs.saveLastSegmentFetch(System.currentTimeMillis())

            Coroutines.io {
                db.getSegmentDao().upsert(segment);
            }
        }

        suspend fun getAllSegmentsComDb(): LiveData<List<Segment>> {
            return withContext(Dispatchers.IO) {
                val lastFetch = prefs.getLastSegmentFetch()
                if (lastFetch == 0L || Date(lastFetch).isFetchNeeded()){
                    fetchSegment()
                }
                db.getSegmentDao().getSegmentLocalAll()
            }
        }
        suspend fun getSegmentComDb(id:Int): LiveData<Segment> {
            return withContext(Dispatchers.IO) {
                val lastFetch = prefs.getLastSegmentFetch()
                if (lastFetch == 0L || Date(lastFetch).isFetchNeeded()){
                    fetchSegment()
                }
                MutableLiveData(db.getSegmentDao().getSegmentLocal(id))
            }
        }


}