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
import br.com.meiadois.decole.data.model.Like
import br.com.meiadois.decole.presentation.user.account.AccountActivity
import br.com.meiadois.decole.presentation.user.partnership.PartnershipSearchActivity.Companion.PARTNERSHIP_SEARCH_COMPANY_ID
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

    private var currentMenuItemActive: Int = -1

    // region OnFragment Events
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

        setContentVisibility(CONTENT_NONE)
        setProgressBarVisibility(true)
        setActiveMenuItem(ICON_MATCH_ID)
        setDataSetObservers()

        bottom_bar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == currentMenuItemActive)
                return@setOnMenuItemClickListener true
            if (menuItem.itemId !in setOf(ICON_MATCH_ID, ICON_RECEIVED_ID, ICON_SENT_ID))
                return@setOnMenuItemClickListener false
            setActiveMenuItem(menuItem.itemId)
            updateContent(viewModel.company!!.id)
            true
        }

        btn_search.setOnClickListener {
            Intent(view.context, PartnershipSearchActivity::class.java).also {
                it.putExtra(PARTNERSHIP_SEARCH_COMPANY_ID, viewModel.company!!.id)
                view.context.startActivity(it)
            }
        }

        btn_register_business.setOnClickListener {
            Intent(view.context, AccountActivity::class.java).also {
                view.context.startActivity(it)
            }
        }
    }

    private fun setActiveMenuItem(itemId: Int){
        currentMenuItemActive = itemId
        ICON_MATCH_ID.let {
            bottom_bar.menu.findItem(it)
                .setIcon(if (itemId == it) R.drawable.ic_mdi_connected_companies_selected else R.drawable.ic_mdi_connected_companies)
        }
        ICON_RECEIVED_ID.let {
            bottom_bar.menu.findItem(it)
                .setIcon(if (itemId == it) R.drawable.ic_mdi_likes_recived_selected else R.drawable.ic_mdi_likes_recived)
        }
        ICON_SENT_ID.let {
            bottom_bar.menu.findItem(it)
                .setIcon(if (itemId == it) R.drawable.ic_mdi_likes_sent_selected else R.drawable.ic_mdi_likes_sent)
        }
    }

//    private fun setActiveMenuItem(itemId: Int){
//        currentMenuItemActive = itemId
//        ICON_MATCH_ID.let {
//            bottom_bar.menu.findItem(it)
//                .setIcon(if (itemId == it) R.drawable.ic_mdi_people_green else R.drawable.ic_mdi_people_white)
//        }
//        ICON_RECEIVED_ID.let {
//            bottom_bar.menu.findItem(it)
//                .setIcon(if (itemId == it) R.drawable.ic_mdi_move_to_inbox_green else R.drawable.ic_mdi_move_to_inbox_white)
//        }
//        ICON_SENT_ID.let {
//            bottom_bar.menu.findItem(it)
//                .setIcon(if (itemId == it) R.drawable.ic_mdi_grade_green else R.drawable.ic_mdi_grade_white)
//        }
//    }

    override fun onResume() {
        super.onResume()
        init()
    }
    // endregion

    private fun init() {
        Coroutines.main {
            try {
                updateContent(viewModel.getUserCompany().id)
            } catch (ex: ClientException) {
                if (ex.code == 404) setContentVisibility(CONTENT_NO_ACCOUNT)
                else showGenericErrorMessage()
            } catch (ex: Exception) {
                showGenericErrorMessage()
            } finally {
                setProgressBarVisibility(false)
            }
        }
    }

    // region Content Management
    private fun setProgressBarVisibility(visible: Boolean) {
        progress_bar.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setContentVisibility(contentMode: Int) {
        partner_recycler_view.visibility =
            if (contentMode == CONTENT_LIST_PARTNERS) View.VISIBLE else View.GONE
        layout_empty.visibility =
            if (contentMode == CONTENT_NO_REGISTERS_FOUND){
                text_empty.text = when(currentMenuItemActive){
                    ICON_MATCH_ID -> getString(R.string.no_match_found)
                    ICON_RECEIVED_ID -> getString(R.string.no_received_likes_found)
                    else -> getString(R.string.no_sent_likes_found)
                }
                View.VISIBLE
            } else View.GONE
        fragment_container_noAccount.visibility =
            if (contentMode == CONTENT_NO_ACCOUNT) View.VISIBLE else View.GONE
        btn_search.visibility =
            if (contentMode in setOf(CONTENT_LIST_PARTNERS, CONTENT_NO_REGISTERS_FOUND)) View.VISIBLE else View.GONE
        bottom_bar.visibility = btn_search.visibility
    }

    private fun showGenericErrorMessage() {
        fragment_bottom_root_layout.longSnackbar(getString(R.string.getting_data_failed_error_message)) { snackbar ->
            snackbar.setAction(getString(R.string.reload)) {
                init()
                snackbar.dismiss()
            }
        }
    }
    // endregion

    // region DataSet Management
    private fun updateContent(companyId: Int) {
        when(currentMenuItemActive){
            ICON_MATCH_ID -> {
                viewModel.recyclerDataSet.value = viewModel.matchesList.value
                viewModel.getUserMatches(companyId)
            }
            ICON_SENT_ID -> {
                viewModel.recyclerDataSet.value = viewModel.sentLikesList.value
                viewModel.getSentLikes(companyId)
            }
            ICON_RECEIVED_ID -> {
                viewModel.recyclerDataSet.value = viewModel.receivedLikesList.value
                viewModel.getReceivedLikes(companyId)
            }
            else -> {}
        }
    }

    private fun setRecyclerViewDataSet(list: List<Like>){
        setProgressBarVisibility(false)
        if (list.isNotEmpty()) {
            setContentVisibility(CONTENT_LIST_PARTNERS)
            with(partner_recycler_view) {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                setHasFixedSize(true)
                adapter = PartnerRecyclerAdapter(list, context) {
                    onPartnerItemClick(context, it)
                }
            }
        } else
            setContentVisibility(CONTENT_NO_REGISTERS_FOUND)
    }

    private fun setDataSetObservers() {
        viewModel.matchesList.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (currentMenuItemActive == ICON_MATCH_ID)
                    setRecyclerViewDataSet(it)
            }
        })
        viewModel.sentLikesList.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (currentMenuItemActive == ICON_SENT_ID)
                    setRecyclerViewDataSet(it)
            }
        })
        viewModel.receivedLikesList.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (currentMenuItemActive == ICON_RECEIVED_ID)
                    setRecyclerViewDataSet(it)
            }
        })
        viewModel.recyclerDataSet.observe(viewLifecycleOwner, Observer {
            it?.let {
                setRecyclerViewDataSet(it)
            }
        })
    }
    // endregion

    private fun onPartnerItemClick(context: Context, like: Like) {
        val intent: Intent = PartnershipPopUpActivity.getStartIntent(
            context, like.id, like.partnerCompany.id, like.userCompany.id, like.isSender, currentMenuItemActive)
        startActivityForResult(intent, PARTNERSHIP_POPUP_ACTIONS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PARTNERSHIP_POPUP_ACTIONS_REQUEST_CODE -> when (resultCode) {
                UNDO_PARTNERSHIP_DELETED_RESULT_CODE -> {
                    data?.let {
                        viewModel.removeMatch(it.getIntExtra(UNDO_PARTNERSHIP_DELETED_TAG, 0))
                    }
                }
                PARTNERSHIP_CANCELED_RESULT_CODE -> {
                    data?.let {
                        viewModel.removeSentLike(it.getIntExtra(PARTNERSHIP_CANCELED_TAG, 0))
                    }
                }
                PARTNERSHIP_DENIED_RESULT_CODE -> {
                    data?.let {
                        viewModel.removeReceivedLike(it.getIntExtra(PARTNERSHIP_DENIED_TAG, 0))
                    }
                }
                PARTNERSHIP_ACCEPTED_RESULT_CODE -> {
                    data?.let {
                        viewModel.removeReceivedLike(it.getIntExtra(PARTNERSHIP_ACCEPTED_TAG, 0))
                    }
                }
            }
        }
    }

    class PartnerRecyclerAdapter(
        private val dataset: List<Like>, private val context: Context,
        private val onItemClickListener: ((like: Like) -> Unit)
    ) :
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

        class PartnerViewHolder(
            private val parent: View,
            private val onItemClickListener: ((like: Like) -> Unit)
        ) : RecyclerView.ViewHolder(parent) {
            private val segment = parent.text_partner_segment
            private val name = parent.text_partner_name
            private val image = parent.image_partner

            fun bindView(like: Like) {
                Glide.with(parent).load(like.partnerCompany.thumbnail)
                    .apply(RequestOptions.circleCropTransform()).into(image)
                segment.text = like.partnerCompany.segment?.name
                name.text = like.partnerCompany.name

                parent.setOnClickListener { onItemClickListener.invoke(like) }
            }
        }
    }

    companion object {
        private const val PARTNERSHIP_POPUP_ACTIONS_REQUEST_CODE = 11352

        const val UNDO_PARTNERSHIP_DELETED_RESULT_CODE = 11353
        const val UNDO_PARTNERSHIP_DELETED_TAG = "DELETED_LIKE_ID"

        const val PARTNERSHIP_ACCEPTED_RESULT_CODE = 11354
        const val PARTNERSHIP_ACCEPTED_TAG = "ACCEPTED_LIKE_ID"

        const val PARTNERSHIP_DENIED_RESULT_CODE = 11355
        const val PARTNERSHIP_DENIED_TAG = "DENIED_LIKE_ID"

        const val PARTNERSHIP_CANCELED_RESULT_CODE = 11356
        const val PARTNERSHIP_CANCELED_TAG = "CANCELED_LIKE_ID"

        private const val CONTENT_NONE = 0
        private const val CONTENT_NO_ACCOUNT = 1
        private const val CONTENT_LIST_PARTNERS = 2
        private const val CONTENT_NO_REGISTERS_FOUND = 3

        const val ICON_SENT_ID = R.id.menu_waiting_response
        const val ICON_RECEIVED_ID = R.id.menu_to_respond
        const val ICON_MATCH_ID = R.id.menu_connected
    }
}
