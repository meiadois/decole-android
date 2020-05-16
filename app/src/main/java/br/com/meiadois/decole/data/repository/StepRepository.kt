package br.com.meiadois.decole.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.model.Step
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.util.extension.parseToStepEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StepRepository(
    private val client: DecoleClient
) : RequestHandler() {

    suspend fun getSteps(lessonId: Long): LiveData<List<Step>> {
        return withContext(Dispatchers.IO) {
            val res = callClient { client.steps(lessonId) }
            MutableLiveData(res.parseToStepEntity())
        }
    }
}