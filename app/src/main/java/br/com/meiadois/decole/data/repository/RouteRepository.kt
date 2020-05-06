package br.com.meiadois.decole.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.parseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RouteRepository(
    private val client: DecoleClient,
    private val db: AppDatabase
) : RequestHandler() {

    private val routes = MutableLiveData<List<Route>>()

    init {
        routes.observeForever {
            saveRoutes(it)
        }
    }

    suspend fun getRoutes(): LiveData<List<Route>> {
        return withContext(Dispatchers.IO) {
            if (isFetchNeeded()) {
                fetchRoutes()
            }
            db.getRouteDao().findAll()
        }
    }

    suspend fun fetchRoutes() {
        val res = callClient { client.routes() }
        routes.postValue(res.parseEntity())
    }

    private fun saveRoutes(routes: List<Route>) {
        Coroutines.io {
            db.getRouteDao().updateRoutes(routes)
        }
    }

    private fun isFetchNeeded(): Boolean {
        return true
    }
}