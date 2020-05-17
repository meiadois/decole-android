package br.com.meiadois.decole.presentation.user.partnership

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.model.Like
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipHomeBottomViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipHomeBottomViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.exception.ClientException
import br.com.meiadois.decole.util.extension.longSnackbar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.card_partner.view.*
import kotlinx.android.synthetic.main.fragment_partnership_home_bottom.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

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
        init()
    }

    private fun init(){
        Coroutines.main {
            try{
             val company : Company = viewModel.getUserCompany()
                showPartnershipList(company.id)
            }catch (ex: ClientException){
                if (ex.code == 404) showInviteToRegister()
                else showGenericErrorMessage()
            }catch (ex: Exception){
                showGenericErrorMessage()
            }finally {
                progress_bar_parent_layout?.visibility = View.INVISIBLE
            }
        }
    }


     fun showPartnershipList(companyId: Int){
        progress_bar_parent_layout?.visibility = View.INVISIBLE
        partnership_scroolable_view.visibility = View.VISIBLE
        viewModel.partnershipLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                with(partner_recycler_view) {
                    layoutManager = LinearLayoutManager(fragment_bottom_root_layout.context, RecyclerView.VERTICAL, false)
                    setHasFixedSize(true)
                    adapter = PartnerRecyclerAdapter(it, fragment_bottom_root_layout.context)
                }
            }
        })
        viewModel.getPartnerships(companyId)
    }

     fun showInviteToRegister(){
        fragment_bottom_root_layout.removeAllViews()
        layoutInflater.inflate(R.layout.fragment_partnership_no_account_bottom, fragment_bottom_root_layout)

    }

     fun showGenericErrorMessage(){
        progress_bar_parent_layout?.visibility = View.INVISIBLE
        fragment_bottom_root_layout.longSnackbar(getString(R.string.getting_data_failed_error_message)){ snackbar ->
            snackbar.setAction(getString(R.string.reload)) {
                init()
                snackbar.dismiss()
            }
        }
    }

    class PartnerRecyclerAdapter(private val dataset: List<Like>, private val context: Context) :
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
            private val segment = parent.text_partner_segment
            private val name = parent.text_partner_name
            private val image = parent.image_partner
            private val card = parent

            fun bindView(like: Like) {
                segment.text = like.partnerCompany.segment?.name
                Glide.with(card).load(like.partnerCompany.thumbnail).apply(RequestOptions.circleCropTransform()).into(image)
                name.text = like.partnerCompany.name

                card.setOnClickListener {
                    val intent : Intent = PartnershipPopUpActivity.getStartIntent(card.context, like.partnerCompany.id)
                    card.context.startActivity(intent)
                }
            }
        }
    }
}
