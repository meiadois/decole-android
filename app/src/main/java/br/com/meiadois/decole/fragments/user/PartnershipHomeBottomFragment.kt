package br.com.meiadois.decole.fragments.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.meiadois.decole.R
import br.com.meiadois.decole.model.Partner
import kotlinx.android.synthetic.main.card_partner.view.*
import kotlinx.android.synthetic.main.fragment_partnership_home_bottom.*

class PartnershipHomeBottomFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_partnership_home_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = partner_recycler_view
        recyclerView.adapter = PartnerRecyclerAdapter(partners(), view.context)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
    }


    private fun partners(): List<Partner> {
        return listOf(
            Partner("Lorem ipsum",
                "Lorem ipsumLorem ipsumLorem ipsumLorem ipsum"),
            Partner("Lorem ipsum",
                "Lorem ipsumLorem ipsumLorem ipsumLorem ipsum"),
            Partner("Lorem ipsum",
                "Lorem ipsumLorem ipsumLorem ipsumLorem ipsum"),
            Partner("Lorem ipsum",
                "Lorem ipsumLorem ipsumLorem ipsumLorem ipsum"))
    }

    class PartnerRecyclerAdapter(private val dataset: List<Partner>, private val context: Context) :
        RecyclerView.Adapter<PartnerRecyclerAdapter.PartnerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerViewHolder {
            val view = LayoutInflater
                .from(context)
                .inflate(R.layout.card_partner, parent, false)

            return PartnerViewHolder(view)
        }

        override fun getItemCount(): Int = dataset.size

        override fun onBindViewHolder(holder: PartnerViewHolder, position: Int) {
            val partner = dataset[position]
            holder.bindView(partner)
        }


        class PartnerViewHolder(parent: View) : RecyclerView.ViewHolder(parent){
            val name = parent.partner_name
            val segment = parent.partner_segment
            val image = parent.partner_image

            fun bindView(partner: Partner){
                name.text = partner.name
                segment.text = partner.segment
            }
        }
    }
}
