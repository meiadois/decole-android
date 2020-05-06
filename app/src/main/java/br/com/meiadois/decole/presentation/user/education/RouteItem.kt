package br.com.meiadois.decole.presentation.user.education

import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.databinding.CardRouteBinding
import com.xwray.groupie.databinding.BindableItem

class RouteItem(val route: Route) : BindableItem<CardRouteBinding>() {

    override fun getLayout(): Int = R.layout.card_route

    override fun bind(viewBinding: CardRouteBinding, position: Int) {
        viewBinding.route = route
    }


}