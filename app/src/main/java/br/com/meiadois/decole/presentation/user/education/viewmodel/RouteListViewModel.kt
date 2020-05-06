package br.com.meiadois.decole.presentation.user.education.viewmodel

import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.repository.RouteRepository
import br.com.meiadois.decole.presentation.user.education.RouteDetailsActivity
import br.com.meiadois.decole.util.lazyDeferred

class RouteListViewModel(
    val repository: RouteRepository
) : ViewModel() {

    val routes by lazyDeferred {
        //        repository.getRoutes()
        getRoutes()
    }

    fun onItemClick(item: Route, view: View) {
        Intent(view.context, RouteDetailsActivity::class.java).also {
            it.putExtra("itemId", item.id)
            view.context.startActivity(it)
        }
    }

    //TODO Remove
    fun getRoutes() = mockRoutes()

    private fun mockRoutes(): LiveData<List<Route>> {
        val data = MutableLiveData<List<Route>>()
        data.postValue(
            listOf(
                Route(
                    "1A",
                    "Primeiros passos no Instagram",
                    "Aprenda a como apresentar seu negócio no instagram", false, 25
                ),
                Route(
                    "2A",
                    "Primeiros passos no Facebook",
                    "Aprenda a como apresentar seu negócio no facebook", true, 0
                ),
                Route(
                    "3A",
                    "Conectando o Instagram ao Facebook",
                    "Aprenda a como vender seus produtos usando o Instagram e o Facebook", true, 0
                ),
                Route(
                    "4",
                    "Primeiros passos no MercadoLivre",
                    "Aprenda a como apresentar seu negócio no MercadoLivre", true, 0
                ),
                Route(
                    "5",
                    "Primeiros passos no MercadoLivre",
                    "Aprenda a como apresentar seu negócio no MercadoLivre", true, 0
                ),
                Route(
                    "6",
                    "Primeiros passos no MercadoLivre",
                    "Aprenda a como apresentar seu negócio no MercadoLivre", true, 0
                ),
                Route(
                    "7",
                    "Primeiros passos no MercadoLivre",
                    "Aprenda a como apresentar seu negócio no MercadoLivre", true, 0
                ),
                Route(
                    "8",
                    "Primeiros passos no MercadoLivre",
                    "Aprenda a como apresentar seu negócio no MercadoLivre", true, 0
                ),
                Route(
                    "9",
                    "Primeiros passos no MercadoLivre",
                    "Aprenda a como apresentar seu negócio no MercadoLivre", true, 0
                ),
                Route(
                    "10",
                    "Últimos passos no MercadoLivreMercadoLivre",
                    "Aprenda a como apresentar seu negócio no MercadoLivre,Aprenda a como apresentar seu negócio no MercadoLivre",
                    true,
                    0
                )
            )
        )

        return data
    }

}