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
import br.com.meiadois.decole.databinding.FragmentPwRecoveryHomeBinding
import br.com.meiadois.decole.presentation.pwrecovery.listener.HomeListener
import br.com.meiadois.decole.presentation.pwrecovery.viewmodel.PwRecoveryViewModel
import br.com.meiadois.decole.util.extension.longSnackbar
import kotlinx.android.synthetic.main.fragment_pw_recovery_home.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein

class PwRecoveryHomeFragment : Fragment(), KodeinAware, HomeListener {

    override val kodein by kodein()
    private val mViewModel: PwRecoveryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPwRecoveryHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_pw_recovery_home, container, false
        )
        binding.viewModel = mViewModel
        mViewModel.homeListener = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        input_recovery_email.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                mViewModel.onHomeNextButtonClicked(this.context)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        btn_home_next.setOnClickListener {
            mViewModel.onHomeNextButtonClicked(this.context)
        }
    }

    override fun onStarted() {
        //ignore
    }

    override fun onFailure(message: String?) {
        layout_input_recovery_email.error = mViewModel.emailErrorMessage
        message?.let {
            activity?.root_layout?.longSnackbar(message)
        }
    }

    override fun onSuccess() {
        val nextFragment = PwRecoveryCodeFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, nextFragment)
            .addToBackStack(null)
            .commit()
    }
}
