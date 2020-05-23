package br.com.meiadois.decole.presentation.user.partnership

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        bottom_bar.setOnMenuItemClickListener { menuItem ->
            menuItem.isChecked = true
            when(menuItem.itemId){
                R.id.menu_connected -> {
                    // todo obter e mostrar lista nova
                    true
                }
                R.id.menu_waiting_response -> {
                    // todo obter e mostrar lista nova
                    true
                }
                R.id.menu_to_respond -> {
                    // todo obter e mostrar lista nova
                    true
                }
                else -> false
            }
        }
        init(view)
    }

    private fun init(view: View){
        Coroutines.main {
            try{
                val company : Company = viewModel.getUserCompany()
                showPartnershipList(view, company.id)
            }catch (ex: ClientException){
                if (ex.code == 404) showInviteToRegister()
                else showGenericErrorMessage()
            }catch (ex: Exception){
                showGenericErrorMessage()
            }finally {
                progress_bar?.visibility = View.INVISIBLE
            }
        }
    }

    private fun showPartnershipList(view: View, companyId: Int){
        partnership_scroolable_view.visibility = View.VISIBLE
        viewModel.partnershipLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it.isEmpty()){
                    partner_recycler_view.visibility = View.GONE
                    layout_empty.visibility = View.VISIBLE
                }else{
                    partner_recycler_view.visibility = View.VISIBLE
                    layout_empty.visibility = View.GONE
                    with(partner_recycler_view) {
                        layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
                        setHasFixedSize(true)
                        adapter = PartnerRecyclerAdapter(it, view.context){
                            onPartnerItemClick(view.context, it)
                        }
                    }
                }

            }
        })
        viewModel.getPartnerships(companyId)
    }

    private fun showInviteToRegister(){
        fragment_bottom_root_layout.removeAllViews()
        layoutInflater.inflate(R.layout.fragment_partnership_no_account_bottom, fragment_bottom_root_layout)
    }

    private fun showGenericErrorMessage(){
        fragment_bottom_root_layout.longSnackbar(getString(R.string.getting_data_failed_error_message)){ snackbar ->
            snackbar.setAction(getString(R.string.reload)) {
                init(it)
                snackbar.dismiss()
            }
        }
    }

    private fun onPartnerItemClick(context: Context, like: Like){
        val intent : Intent = PartnershipPopUpActivity.getStartIntent(
            context, like.id, like.partnerCompany.id, like.userCompany.id, like.isSender
        )
        startActivityForResult(intent, UNDO_PARTNERSHIP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            UNDO_PARTNERSHIP_REQUEST_CODE -> when (resultCode) {
                UNDO_PARTNERSHIP_DELETED_RESULT_CODE -> {
                    data?.let {
                        viewModel.removeLike(it.getIntExtra(UNDO_PARTNERSHIP_DELETED_TAG, 0))
                    }
                }
            }
        }
    }

    class PartnerRecyclerAdapter(private val dataset: List<Like>, private val context: Context,
        private val onItemClickListener: ((like: Like) -> Unit)) :
        RecyclerView.Adapter<PartnerRecyclerAdapter.PartnerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerViewHolder {
            val view = LayoutInflater
                .from(context)
                .inflate(R.layout.card_partner, parent, false)

            return PartnerViewHolder(view, onItemClickListener)
        }

        override fun getItemCount(): Int = dataset.size

        override fun onBindViewHolder(holder: PartnerViewHolder, position: Int) {
            val partner = dataset[position]
            holder.bindView(partner)
        }

        class PartnerViewHolder(private val parent: View, private val onItemClickListener: ((like: Like) -> Unit)) : RecyclerView.ViewHolder(parent) {
            private val segment = parent.text_partner_segment
            private val name = parent.text_partner_name
            private val image = parent.image_partner

            fun bindView(like: Like) {
                Glide.with(parent).load(like.partnerCompany.thumbnail).apply(RequestOptions.circleCropTransform()).into(image)
                segment.text = like.partnerCompany.segment?.name
                name.text = like.partnerCompany.name

                parent.setOnClickListener { onItemClickListener.invoke(like) }
            }
        }
    }

    companion object{
        private const val UNDO_PARTNERSHIP_REQUEST_CODE = 11352
        const val UNDO_PARTNERSHIP_DELETED_RESULT_CODE = 11353
        const val UNDO_PARTNERSHIP_DELETED_TAG = "DELETED_LIKE_ID"
    }
}
