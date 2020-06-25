package br.com.meiadois.decole.presentation.user.partnership

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Like
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnerBottomSheetViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnerBottomSheetViewModelFactory
import br.com.meiadois.decole.util.extension.shortToast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_partner_bottom_sheet.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat

class PartnerBottomSheetDialog : BottomSheetDialogFragment(), KodeinAware, PartnerActionListener {

    override val kodein by kodein()
    private val factory: PartnerBottomSheetViewModelFactory by instance<PartnerBottomSheetViewModelFactory>()
    private lateinit var viewModel: PartnerBottomSheetViewModel

    lateinit var onDismissListener: OnActionCompletedListener

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

        viewModel.listener = this

        arguments?.getParcelable<Like>(INVITE_DETAILS_KEY)?.let {
            viewModel.inviteInfo = it

            sheet_text_partner_name.text = it.partnerCompany.name
            sheet_text_partner_segment.text = it.partnerCompany.segment?.name
            sheet_text_partner_description.text = it.partnerCompany.description
            sheet_text_phone.text = it.partnerCompany.cellphone
            sheet_text_email.text = it.partnerCompany.email
            Glide.with(container_company_info).load(it.partnerCompany.thumbnail)
                .apply(RequestOptions.circleCropTransform()).into(image_partner)

            if (it.status == "accepted")
                renderAcceptedSheet(formatDate(it.acceptedAt!!))
            else if (it.isSender)
                renderSenderFooter()
        }
        configureActions()
    }

    private fun configureActions() {

        btn_primary_invite_received.setOnClickListener {
            val button = it as Button
            if (button.text != getString(R.string.long_press_confirmation)){
                button.text = getString(R.string.long_press_confirmation)
                button.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mdi_warning), null, null, null)
            }
        }

        btn_primary_invite_received.setOnLongClickListener {
            val button = it as Button
            if (button.text == getString(R.string.long_press_confirmation)){
                viewModel.confirmPartnership()
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false
        }

        btn_secondary_invite_received.setOnClickListener {
            val button = it as Button
            if (button.text != getString(R.string.long_press_confirmation)){
                button.text = getString(R.string.long_press_confirmation)
                button.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mdi_warning), null, null, null)
            }
        }

        btn_secondary_invite_received.setOnLongClickListener {
            val button = it as Button
            if (button.text == getString(R.string.long_press_confirmation)){
                viewModel.cancelPartnership()
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false
        }

        btn_secondary_invite_sent.setOnClickListener {
            val button = it as Button
            if (button.text != getString(R.string.long_press_confirmation)){
                button.text = getString(R.string.long_press_confirmation)
                button.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mdi_warning), null, null, null)
            }
        }

        btn_secondary_invite_sent.setOnLongClickListener {
            val button = it as Button
            if (button.text == getString(R.string.long_press_confirmation)){
                viewModel.deleteLike()
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false
        }

        btn_secondary_match.setOnClickListener {
            val button = it as Button
            if (button.text != getString(R.string.long_press_confirmation)){
                button.text = getString(R.string.long_press_confirmation)
                button.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.ic_mdi_warning), null, null, null)
            }
        }

        btn_secondary_match.setOnLongClickListener {
            val button = it as Button
            if (button.text == getString(R.string.long_press_confirmation)){
                viewModel.deletePartnership()
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false
        }

        sheet_text_phone.setOnClickListener {
            val number: String = sheet_text_phone.text.toString()
            val intent: Intent
            if (!isWhatsAppInstalled()) {
                intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$number")
            } else
                intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://api.whatsapp.com/send?phone=$number")
                )
            startActivity(intent)
        }

        sheet_text_email.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(sheet_text_email.text.toString()))
            startActivity(Intent.createChooser(intent, "Send Email"))
        }

    }

    private fun renderAcceptedSheet(matchDate: String) {
        container_contact.visibility = View.VISIBLE
        sheet_text_title.text = getString(R.string.partner_bottom_sheet_accepted_invite_title)
        sheet_text_partner_since.text = getString(R.string.label_partners_since, matchDate)
        container_invite_received_footer.visibility = View.GONE
        container_match_footer.visibility = View.VISIBLE
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(date: String): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return formatter.format(parser.parse(date)!!)
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
        dismiss()
        onDismissListener.handle()
    }

    override fun onFailure() {
        container_partner_bottom_sheet.shortToast(getString(R.string.error_when_executing_the_action))
        dismiss()
        onDismissListener.handle()
    }

    private fun toggleLoading(loading: Boolean) {
        if (loading) {
            container_actions.visibility = View.GONE
            container_loading_footer.visibility = View.VISIBLE
        } else {
            container_actions.visibility = View.VISIBLE
            container_loading_footer.visibility = View.GONE
        }
    }

    private fun isWhatsAppInstalled(): Boolean {
        return try {
            context?.applicationContext?.packageManager?.getPackageInfo(
                "com.whatsapp",
                PackageManager.GET_ACTIVITIES
            )
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    interface OnActionCompletedListener {
        fun handle()
    }

    companion object {
        const val INVITE_DETAILS_KEY = "INVITE_DETAILS"
    }
}