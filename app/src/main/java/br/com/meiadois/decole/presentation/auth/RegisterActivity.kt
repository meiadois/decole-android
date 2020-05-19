package br.com.meiadois.decole.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.localdb.entity.User
import br.com.meiadois.decole.databinding.ActivityRegisterBinding
import br.com.meiadois.decole.presentation.auth.viewmodel.RegisterViewModel
import br.com.meiadois.decole.presentation.auth.viewmodel.RegisterViewModelFactory
import br.com.meiadois.decole.presentation.welcome.WelcomeInfoActivity
import br.com.meiadois.decole.util.extension.longSnackbar
import kotlinx.android.synthetic.main.activity_register.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class RegisterActivity : AppCompatActivity(), AuthListener, KodeinAware {

    override val kodein by kodein()
    private val factory: RegisterViewModelFactory by instance()

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityRegisterBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_register)

        registerViewModel = ViewModelProvider(this, factory).get(RegisterViewModel::class.java)

        binding.apply {
            viewModel = registerViewModel
        }

        registerViewModel.authListener = this

        input_register_confirm_password.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerViewModel.onRegisterButtonClick(v)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStarted() {
        toggleLoading(true)
    }

    override fun onSuccess(user: User, message: String?) {
        toggleLoading(false)
        Intent(this, WelcomeInfoActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            it.putExtra("message", message)
            startActivity(it)
        }
    }

    override fun onFailure(message: String?) {
        toggleLoading(false)
        layout_register_email_input.error = registerViewModel.emailErrorMessage
        layout_register_username_input.error = registerViewModel.nameErrorMessage
        layout_register_password_input.error = registerViewModel.passwordErrorMessage
        layout_register_confirm_password_input.error = registerViewModel.confirmPasswordErrorMessage

        message?.let {
            root_layout.longSnackbar(it)
        }
    }

    private fun toggleLoading(boolean: Boolean) {
        if (boolean) {
            progress_bar_register.visibility = View.VISIBLE
            btn_register_next.visibility = View.GONE
        } else {
            progress_bar_register.visibility = View.GONE
            btn_register_next.visibility = View.VISIBLE
        }
    }

}