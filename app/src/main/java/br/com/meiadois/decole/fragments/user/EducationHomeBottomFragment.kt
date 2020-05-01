package br.com.meiadois.decole.fragments.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.meiadois.decole.R
import br.com.meiadois.decole.activity.user.RouteDetailsActivity
import br.com.meiadois.decole.model.Route
import kotlinx.android.synthetic.main.card_route.view.*
import kotlinx.android.synthetic.main.fragment_education_home_bottom.*


class EducationHomeBottomFragment : Fragment() {

    interface OnClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_education_home_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = route_recycler_view
        recyclerView.adapter = RouteRecyclerAdapter(routes(), view.context,
            object : OnClickListener {
                override fun onItemClick(position: Int) {
                    Log.i("assertClick", "$position")
                    val intent = Intent(activity, RouteDetailsActivity::class.java)
                    startActivity(intent)
                }
            })

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
    }

    private fun routes(): List<Route> {
        return listOf(
            Route(
                "Primeiros passos no Instagram",
                "Aprenda a como apresentar seu negócio no instagram", false, 25
            ),
            Route(
                "Primeiros passos no Facebook",
                "Aprenda a como apresentar seu negócio no facebook", true, 0
            ),
            Route(
                "Conectando o Instagram ao Facebook",
                "Aprenda a como vender seus produtos usando o Instagram e o Facebook", true, 0
            ),
            Route(
                "Primeiros passos no MercadoLivre",
                "Aprenda a como apresentar seu negócio no MercadoLivre", true, 0
            )
        )
    }

    inner class RouteRecyclerAdapter(
        private val dataset: List<Route>,
        private val context: Context,
        val cardClickListener: OnClickListener
    ) :
        RecyclerView.Adapter<RouteRecyclerAdapter.RouteViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
            val view = LayoutInflater
                .from(context)
                .inflate(R.layout.card_route, parent, false)

            return RouteViewHolder(view)
        }

        override fun getItemCount(): Int = dataset.size

        override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
            val route = dataset[position]
            holder.bindView(route)
        }


        inner class RouteViewHolder(val parent: View) :
            RecyclerView.ViewHolder(parent) {
            val title = parent.route_title
            val description = parent.route_description
            val lockImage = parent.lock_image
            val progressBar = parent.progress_bar

            fun bindView(route: Route) {

                parent.setOnClickListener { v ->
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        cardClickListener.onItemClick(position)
                    }
                }

                title.text = route.title
                description.text = route.description
                val resource = if (route.locked) R.drawable.ic_lock else R.drawable.ic_lock_open
                lockImage.setImageResource(resource)
                progressBar.progress = route.progress
            }
        }
    }
}
