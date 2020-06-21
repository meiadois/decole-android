package br.com.meiadois.decole.presentation.pwrecovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.FragmentPwRecoveryCodeBinding
import br.com.meiadois.decole.presentation.pwrecovery.listener.CodeListener
import br.com.meiadois.decole.presentation.pwrecovery.viewmodel.PwRecoveryViewModel
import br.com.meiadois.decole.util.extension.longSnackbar
import kotlinx.android.synthetic.main.fragment_pw_recovery_code.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein

class PwRecoveryCodeFragment : Fragment(), KodeinAware, CodeListener {

    override val kodein by kodein()

    private val mViewModel: PwRecoveryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPwRecoveryCodeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_pw_recovery_code, container, false
        )
        binding.viewModel = mViewModel
        mViewModel.codeListener = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        input_register_email.setOnEditorActionListener { _ , actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                mViewModel.onCodeNextButtonClicked(this.context)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        btn_code_next.setOnClickListener {
            mViewModel.onCodeNextButtonClicked(this.context)
        }
    }

    override fun onStarted() {
        toggleLoading(true)
    }

    private fun toggleLoading(loading: Boolean) {
        if (loading) {
            progress_bar_recovery_code.visibility = View.VISIBLE
            btn_code_next.visibility = View.GONE
        } else {
            progress_bar_recovery_code.visibility = View.GONE
            btn_code_next.visibility = View.VISIBLE
        }
    }

    override fun onFailure(message: String?) {
        toggleLoading(false)
        layout_input_recovery_code.error = mViewModel.codeErrorMessage
        message?.let {
            activity?.root_layout?.longSnackbar(message)
        }
    }

    override fun onSuccess() {
        val nextFragment = PwRecoveryResetFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, nextFragment)
            .addToBackStack(null)
            .commit()
    }
}
