package br.com.meiadois.decole.data.repository

import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.localdb.entity.Segment
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.response.SegmentResponse
import br.com.meiadois.decole.util.Coroutines

class SegmentRepository(
    private val client: DecoleClient
   // private val db: AppDatabase
) : RequestHandler() {
   /* private val segment = MutableLiveData<Segment>()

    init {
        segment.observeForever {
            saveSegment(it)
        }
    }*/


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


    /*suspend fun fetchSegment() {
        val res = callClient { client.getAllSegments() }
        //segment.postValue(res.toSegmentModelList())
    }

    private fun saveSegment(segment:List<Segment>) {
        /
        Coroutines.io {
            db.getSegmentDao().upsert(segment);
        }
    }*/
}