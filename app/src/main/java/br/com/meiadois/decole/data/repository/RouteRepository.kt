package br.com.meiadois.decole.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.model.RouteDetails
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.preferences.PreferenceProvider
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.parseEntity
import br.com.meiadois.decole.util.extension.parseToRouteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class RouteRepository(
    private val client: DecoleClient,
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
) : RequestHandler() {

    private val routes = MutableLiveData<List<Route>>()

    init {
        routes.observeForever {
            saveRoutes(it)
        }
    }

    suspend fun getRoutes(): LiveData<List<Route>> {
        return withContext(Dispatchers.IO) {
            val lastFetch = prefs.getLastRouteFetch()
            if (lastFetch == 0L || isRouteFetchNeeded(Date(lastFetch))) {
                fetchRoutes()
            }
            db.getRouteDao().findAll()
        }
    }

    suspend fun getRoute(id: Long): LiveData<RouteDetails> {
        return withContext(Dispatchers.IO) {
            val lastFetch = prefs.getLastRouteFetch()
            if (lastFetch == 0L || isRouteFetchNeeded(Date(lastFetch))) {
                fetchRoutes()
            }
            db.getRouteDao().getRouteAndLessonsByPk(id)
        }
    }

    suspend fun fetchRoutes() {
        val res = callClient { client.routes() }
        routes.postValue(res.parseToRouteEntity())
    }

    suspend fun fetchRoute(routeId: Long) {
        val res = callClient { client.route(routeId) }
        db.getRouteDao().updateRoute(res.parseEntity())
    }

    private fun saveRoutes(routes: List<Route>) {
        prefs.saveLastRouteFetch(System.currentTimeMillis())
        Coroutines.io {
            db.getRouteDao().updateRoutes(routes)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun isRouteFetchNeeded(lastFetch: Date): Boolean {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val now = Date()
        val nowWithZeroTime = formatter.parse(formatter.format(now))
        val lastFetchWithZeroTime = formatter.parse(formatter.format(lastFetch))

        return lastFetchWithZeroTime!!.before(nowWithZeroTime)
    }

    suspend fun jumpRoute(routesId: Long) {
        callClient {
            client.jumpRoute(routesId)
        }
        fetchRoutes()
    }
}