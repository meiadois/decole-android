package br.com.meiadois.decole.presentation.user.partnership

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipPopUpViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipPopUpViewModelFactory
import kotlinx.android.synthetic.main.popupwindow_partner.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PartnershipPopUpActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: PartnershipPopUpViewModelFactory by instance<PartnershipPopUpViewModelFactory>()
    private lateinit var viewModel: PartnershipPopUpViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.popupwindow_partner)

        val bundle = intent.extras
        val partnerId : Int = bundle?.getInt("partnerId", -1) ?: -1

        viewModel = ViewModelProvider(this, factory).get(PartnershipPopUpViewModel::class.java)

        popup_window_close_button.setOnClickListener { onBackPressed() }
        setBackgroundStartFadeAnimation()
        setStartFadeAnimation()

        viewModel.companyLiveData.observe(this, Observer {
            it?.let {
                popup_window_partner_name.text = it.name
                popup_window_partner_segment.text = it.segment!!.name
                popup_window_description.text = "Lorem Ipsum is simply dummy text of the printing and typesetting " +
                        "industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
                        "when an unknown printer took a galley of type and scrambled it to make a type specimen book. " +
                        "It has survived not only five centuries."
                popup_window_phone.text = "(71) 00000-0000"
                popup_window_mail.text = "contato@empresa.com"
            }
        })
        viewModel.getCompanyById(partnerId)
    }

    private fun setBackgroundStartFadeAnimation(){
        val alpha = 100
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()
    }

    private fun setStartFadeAnimation(){
        popup_window_view_with_border.alpha = 0f
        popup_window_view_with_border.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()
    }

    override fun onBackPressed() {
        val alpha = 100
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 500
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(
                animator.animatedValue as Int
            )
        }
        popup_window_view_with_border.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }

    companion object {
        private const val EXTRA_PARTNER_ID = "PARTNER_ID"

        fun getStartIntent(context: Context, partnerId: Int) : Intent {
            return Intent(context, PartnershipPopUpActivity::class.java).apply {
                putExtra(EXTRA_PARTNER_ID, partnerId)
            }
        }
    }
}