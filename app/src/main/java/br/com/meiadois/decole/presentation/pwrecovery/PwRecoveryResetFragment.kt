package br.com.meiadois.decole.presentation.pwrecovery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.FragmentPwRecoveryResetBinding
import br.com.meiadois.decole.presentation.pwrecovery.listener.ResetListener
import br.com.meiadois.decole.presentation.pwrecovery.viewmodel.PwRecoveryViewModel
import br.com.meiadois.decole.util.extension.longSnackbar
import kotlinx.android.synthetic.main.fragment_pw_recovery_reset.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein

class PwRecoveryResetFragment : Fragment(), KodeinAware, ResetListener {

    override val kodein by kodein()
    private val mViewModel: PwRecoveryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPwRecoveryResetBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_pw_recovery_reset, container, false
        )
        binding.viewModel = mViewModel

        mViewModel.resetListener = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        input_recovery_password_confirmation.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                mViewModel.onResetFinishButtonClicked(this.context)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        btn_reset_finish.setOnClickListener {
            mViewModel.onResetFinishButtonClicked(this.context)
        }
    }

    override fun onStarted() {
        toggleLoading(true)
    }

    override fun onFailure(message: String?) {
        toggleLoading(false)
        layout_input_recovery_password_input.error = mViewModel.passwordErrorMessage
        layout_input_recovery_password_confirmation_input.error =
            mViewModel.passwordConfirmationErrorMessage
        message?.let {
            activity?.root_layout?.longSnackbar(message)
        }
    }

    override fun onSuccess() {
        toggleLoading(false)
        activity?.finish()
    }

    private fun toggleLoading(loading: Boolean) {
        if (loading) {
            progress_bar_recovery_reset.visibility = View.VISIBLE
            btn_reset_finish.visibility = View.GONE
        } else {
            progress_bar_recovery_reset.visibility = View.GONE
            btn_reset_finish.visibility = View.VISIBLE
        }
    }
}