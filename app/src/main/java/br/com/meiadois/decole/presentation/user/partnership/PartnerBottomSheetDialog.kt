package br.com.meiadois.decole.presentation.user.partnership

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Like
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnerBottomSheetViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnerBottomSheetViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_partner_bottom_sheet.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PartnerBottomSheetDialog : BottomSheetDialogFragment(), KodeinAware, PartnerActionListener {

    override val kodein by kodein()
    private val factory: PartnerBottomSheetViewModelFactory by instance<PartnerBottomSheetViewModelFactory>()
    private lateinit var viewModel: PartnerBottomSheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(PartnerBottomSheetViewModel::class.java)

        (dialog as? BottomSheetDialog)?.let {
            it.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return inflater.inflate(R.layout.layout_partner_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<Like>("inviteDetails")?.let {
            viewModel.inviteInfo = it

            sheet_text_partner_name.text = it.partnerCompany.name
            sheet_text_partner_segment.text = it.partnerCompany.segment?.name
            sheet_text_partner_description.text = it.partnerCompany.description
            sheet_text_phone.text = it.partnerCompany.cellphone
            sheet_text_email.text = it.partnerCompany.email

            if (it.status == "accepted") {
                renderAcceptedSheet()
            } else if (it.isSender) {
                renderSenderFooter()
            }
        }
        configureActions()
    }

    private fun configureActions() {
        btn_primary_invite_received.setOnClickListener {
            viewModel.confirmPartnership()
        }

        btn_secondary_invite_received.setOnClickListener {
            viewModel.cancelPartnership()
        }

        btn_secondary_invite_sent.setOnClickListener {
            viewModel.deletePartnership()
        }

        btn_secondary_match.setOnClickListener {
            viewModel.deletePartnership()
        }
    }

    private fun renderAcceptedSheet() {
        sheet_text_title.text = getString(R.string.partner_bottom_sheet_accepted_invite_title)
        container_invite_received_footer.visibility = View.GONE
        container_match_footer.visibility = View.VISIBLE
    }

    private fun renderSenderFooter() {
        container_invite_received_footer.visibility = View.GONE
        container_invite_sent_footer.visibility = View.VISIBLE
    }

    override fun onStarted() {
        toggleLoading(true)
    }

    override fun onSuccess() {
        toggleLoading(false)

        (dialog as? BottomSheetDialog)?.let {
            it.behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun toggleLoading(loading: Boolean) {
        if(loading){
            container_invite_received_footer.visibility = View.GONE
            container_invite_sent_footer.visibility = View.GONE
            container_match_footer.visibility = View.GONE
            container_loading_footer.visibility = View.VISIBLE
        }else {
            container_invite_received_footer.visibility = View.VISIBLE
            container_invite_sent_footer.visibility = View.VISIBLE
            container_match_footer.visibility = View.VISIBLE
            container_loading_footer.visibility = View.GONE
        }
    }
}