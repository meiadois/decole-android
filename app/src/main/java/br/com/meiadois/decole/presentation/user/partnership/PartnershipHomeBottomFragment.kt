package br.com.meiadois.decole.presentation.user.partnership

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Partner
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipHomeBottomViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipHomeBottomViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import kotlinx.android.synthetic.main.card_partner.view.*
import kotlinx.android.synthetic.main.fragment_partnership_home_bottom.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.net.SocketException

class PartnershipHomeBottomFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: PartnershipHomeBottomViewModelFactory by instance<PartnershipHomeBottomViewModelFactory>()
    private lateinit var viewModel: PartnershipHomeBottomViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_partnership_home_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, factory).get(PartnershipHomeBottomViewModel::class.java)

        Coroutines.main {
            try{
                val companies = viewModel.getUserCompanies()
                if (companies.isNotEmpty())
                    showPartnershipList(view)
                else
                    showInviteToRegister()
            }catch (ex: SocketException){
                Log.i("Coroutines.io", ex.message!!)
            }catch (ex: ClientException){
                Log.i("Coroutines.io", ex.message!!)
            }catch (ex: Exception){
                Log.i("Coroutines.io", ex.message!!)
            }finally {
                progress_bar_parent_layout.visibility = View.INVISIBLE
            }
        }
    }

    private fun showPartnershipList(view: View){
        partnership_scroolable_view.visibility = View.VISIBLE
        viewModel.partnershipLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                with(partner_recycler_view) {
                    layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
                    setHasFixedSize(true)
                    adapter = PartnerRecyclerAdapter(it, view.context)
                }
            }
        })
        viewModel.getPartnerships()
    }

    private fun showInviteToRegister(){ }

    class PartnerRecyclerAdapter(private val dataset: List<Partner>, private val context: Context) :
        RecyclerView.Adapter<PartnerRecyclerAdapter.PartnerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerViewHolder {
            val view = LayoutInflater
                .from(context)
                .inflate(R.layout.card_partner, parent, false)

            return PartnerViewHolder(
                view
            )
        }

        override fun getItemCount(): Int = dataset.size

        override fun onBindViewHolder(holder: PartnerViewHolder, position: Int) {
            val partner = dataset[position]
            holder.bindView(partner)
        }


        class PartnerViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
            val name = parent.text_partner_name
            val segment = parent.text_partner_segment
            val image = parent.image_partner

            fun bindView(partner: Partner) {
                name.text = partner.name
                segment.text = partner.segment
            }
        }
    }
}
