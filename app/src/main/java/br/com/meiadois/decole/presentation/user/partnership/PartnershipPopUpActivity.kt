package br.com.meiadois.decole.presentation.user.partnership

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.user.partnership.PartnershipHomeBottomFragment.Companion.ICON_MATCH_ID
import br.com.meiadois.decole.presentation.user.partnership.PartnershipHomeBottomFragment.Companion.ICON_RECEIVED_ID
import br.com.meiadois.decole.presentation.user.partnership.PartnershipHomeBottomFragment.Companion.ICON_SENT_ID
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipPopUpViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipPopUpViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.longSnackbar
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.popupwindow_partner.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PartnershipPopUpActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: PartnershipPopUpViewModelFactory by instance<PartnershipPopUpViewModelFactory>()
    private lateinit var viewModel: PartnershipPopUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.popupwindow_partner)

        val bundle = intent.extras
        val likeId: Int = bundle?.getInt(EXTRA_LIKE_ID, -1) ?: -1
        val partnerId: Int = bundle?.getInt(EXTRA_PARTNER_ID, -1) ?: -1
        val userCompanyId: Int = bundle?.getInt(EXTRA_USER_COMPANY_ID, -1) ?: -1
        val contentMode: Int = bundle?.getInt(EXTRA_CONTENT_MODE, -1) ?: -1
        val isUserSender: Boolean = bundle?.getBoolean(EXTRA_IS_USER_SENDER, false) ?: false

        viewModel = ViewModelProvider(this, factory).get(PartnershipPopUpViewModel::class.java)

        getCompany(partnerId)

        popup_window_close_button.setOnClickListener { onBackPressed() }
        setBackgroundStartFadeAnimation()
        setStartFadeAnimation()

        setContextButtons(likeId, partnerId, userCompanyId, isUserSender, contentMode)
        setOpenDialAppListener()
        setOpenMailAppListener()
    }

    private fun getCompany(partnerId: Int) {
        viewModel.companyLiveData.observe(this, Observer {
            it?.let {
                popup_window_partner_name.text = it.name
                popup_window_partner_segment.text = it.segment?.name
                popup_window_description.text = it.description
                popup_window_phone.text = it.cellphone
                popup_window_mail.text = it.email

                popup_window_phone.visibility = View.VISIBLE
                popup_window_mail.visibility = View.VISIBLE
            }
        })
        viewModel.getCompanyById(partnerId)
    }

    // region Actions
    private fun setOpenMailAppListener() {
        popup_window_mail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(popup_window_mail.text.toString()))
            startActivity(Intent.createChooser(intent, "Send Email"))
        }
    }

    private fun setOpenDialAppListener() {
        popup_window_phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + popup_window_phone.text.toString())
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val alphaColor = ColorUtils.setAlphaComponent(getColor(R.color.popup_background), ALPHA_VALUE)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = TRANSITION_DURATION
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(animator.animatedValue as Int)
        }
        popup_window_view_with_border.animate().alpha(0f).setDuration(TRANSITION_DURATION)
            .setInterpolator(DecelerateInterpolator()).start()
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }
    // endregion

    // region Content Management
    private fun setContextButtons(likeId: Int, partnerId: Int, userCompanyId: Int, isUserSender: Boolean, contentMode: Int){
        when(contentMode){
            ICON_MATCH_ID -> {
                popup_window_match_button_layout.visibility = View.VISIBLE
                setMatchButtonListener(likeId, partnerId, userCompanyId, isUserSender)
            }
            ICON_RECEIVED_ID -> {
                popup_window_received_button_layout.visibility = View.VISIBLE
                setReceivedButtonsListener(likeId, partnerId, userCompanyId, isUserSender)
            }
            ICON_SENT_ID -> {
                popup_window_sent_button_layout.visibility = View.VISIBLE
                setSentButtonListener(likeId)
            }
        }
    }

    private fun setMatchButtonListener(likeId: Int, partnerId: Int, userCompanyId: Int, isUserSender: Boolean){
        match_button.setOnClickListener {
            Coroutines.main {
                try {
                    viewModel.deletePartnership(
                        likeId,
                        if (isUserSender) userCompanyId else partnerId,
                        if (isUserSender) partnerId else userCompanyId
                    )
                    showSuccessMessage(
                        likeId,
                        getString(R.string.undo_partnership_success_message),
                        PartnershipHomeBottomFragment.UNDO_PARTNERSHIP_DELETED_TAG,
                        PartnershipHomeBottomFragment.UNDO_PARTNERSHIP_DELETED_RESULT_CODE
                    )
                } catch (ex: Exception) {
                    popup_window_background.longSnackbar(getString(R.string.undo_partnership_error_message))
                }
            }
        }
    }

    private fun setReceivedButtonsListener(likeId: Int, partnerId: Int, userCompanyId: Int, isUserSender: Boolean){
        received_button_primary.setOnClickListener {
            Coroutines.main {
                try {
                    viewModel.confirmPartnership(
                        likeId,
                        if (isUserSender) userCompanyId else partnerId,
                        if (isUserSender) partnerId else userCompanyId
                    )
                    showSuccessMessage(
                        likeId,
                        getString(R.string.confirm_partnership_success_message),
                        PartnershipHomeBottomFragment.PARTNERSHIP_ACCEPTED_TAG,
                        PartnershipHomeBottomFragment.PARTNERSHIP_ACCEPTED_RESULT_CODE
                    )
                } catch (ex: Exception) {
                    popup_window_background.longSnackbar(getString(R.string.confirm_partnership_error_message))
                }
            }
        }
        received_button_secondary.setOnClickListener {
            Coroutines.main {
                try {
                    viewModel.cancelPartnership(
                        likeId,
                        if (isUserSender) userCompanyId else partnerId,
                        if (isUserSender) partnerId else userCompanyId
                    )
                    showSuccessMessage(
                        likeId,
                        getString(R.string.cancel_partnership_success_message),
                        PartnershipHomeBottomFragment.PARTNERSHIP_DENIED_TAG,
                        PartnershipHomeBottomFragment.PARTNERSHIP_DENIED_RESULT_CODE
                    )
                } catch (ex: Exception) {
                    popup_window_background.longSnackbar(getString(R.string.cancel_partnership_error_message))
                }
            }
        }
    }

    private fun setSentButtonListener(likeId: Int){
        sent_button.setOnClickListener {
            Coroutines.main {
                try {
                    viewModel.deleteLike(likeId)
                    showSuccessMessage(
                        likeId,
                        getString(R.string.cancel_like_success_message),
                        PartnershipHomeBottomFragment.PARTNERSHIP_CANCELED_TAG,
                        PartnershipHomeBottomFragment.PARTNERSHIP_CANCELED_RESULT_CODE
                    )
                } catch (ex: Exception) {
                    popup_window_background.longSnackbar(getString(R.string.cancel_like_error_message))
                }
            }
        }
    }

    private fun showSuccessMessage(likeId: Int, message: String, resultTag: String, resultCode: Int) {
        popup_window_background.longSnackbar(message) {
            it.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    val nIntent = Intent()
                    nIntent.putExtra(resultTag, likeId)
                    setResult(resultCode, nIntent)
                    onBackPressed()
                }
            })
        }
    }
    // region

    // region PopUp Animation
    private fun setBackgroundStartFadeAnimation() {
        val alphaColor =
            ColorUtils.setAlphaComponent(getColor(R.color.popup_background), ALPHA_VALUE)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = TRANSITION_DURATION
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()
    }

    private fun setStartFadeAnimation() {
        popup_window_view_with_border.alpha = 0f
        popup_window_view_with_border.animate().alpha(1f).setDuration(TRANSITION_DURATION)
            .setInterpolator(
                DecelerateInterpolator()
            ).start()
    }
    // endregion

    companion object {
        private const val EXTRA_USER_COMPANY_ID = "USER_COMPANY_ID"
        private const val EXTRA_IS_USER_SENDER = "IS_USER_SENDER"
        private const val EXTRA_CONTENT_MODE = "CONTENT_MODE"
        private const val EXTRA_PARTNER_ID = "PARTNER_ID"
        private const val EXTRA_LIKE_ID = "LIKE_ID"
        private const val ALPHA_VALUE = 100
        private const val TRANSITION_DURATION = 100L

        fun getStartIntent(context: Context, likeId: Int, partnerId: Int, userCompanyId: Int, userIsSender: Boolean, contentMode: Int): Intent {
            return Intent(context, PartnershipPopUpActivity::class.java).apply {
                putExtra(EXTRA_USER_COMPANY_ID, userCompanyId)
                putExtra(EXTRA_IS_USER_SENDER, userIsSender)
                putExtra(EXTRA_CONTENT_MODE, contentMode)
                putExtra(EXTRA_PARTNER_ID, partnerId)
                putExtra(EXTRA_LIKE_ID, likeId)
            }
        }
    }
}