package br.com.meiadois.decole.presentation.user.education.viewmodel

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.repository.RouteRepository
import br.com.meiadois.decole.presentation.user.education.RouteDetailsActivity
import br.com.meiadois.decole.util.lazyDeferred

class RouteListViewModel(
    val repository: RouteRepository
) : ViewModel() {

    val routes by lazyDeferred {
        repository.getRoutes()
    }

    fun onItemClick(item: Route, view: View) {
        Intent(view.context, RouteDetailsActivity::class.java).also {
            it.putExtra("itemId", item.id)
            view.context.startActivity(it)
        }
    }

}