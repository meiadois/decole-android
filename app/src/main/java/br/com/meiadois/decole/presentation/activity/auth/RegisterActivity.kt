package br.com.meiadois.decole.presentation.activity.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.User
import br.com.meiadois.decole.databinding.ActivityLoginBinding
import br.com.meiadois.decole.databinding.ActivityRegisterBinding
import br.com.meiadois.decole.presentation.activity.auth.viewmodel.LoginViewModel
import br.com.meiadois.decole.presentation.activity.auth.viewmodel.RegisterViewModel
import br.com.meiadois.decole.presentation.activity.user.HomeActivity
import br.com.meiadois.decole.presentation.activity.welcome.WelcomeInfoActivity
import br.com.meiadois.decole.util.extension.longSnackbar
import br.com.meiadois.decole.util.extension.longToast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_next
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), AuthListener {

    val registerViewModel = RegisterViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityRegisterBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_register)

        binding.apply {
            viewModel = registerViewModel
        }

        registerViewModel.authListener = this
    }

    override fun onStarted() {
        toggleLoading(true)
    }

    override fun onSuccess(user: User) {
        toggleLoading(false)
        val intent = Intent(this, WelcomeInfoActivity::class.java)
        startActivity(intent)
        finish()

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

    private fun toggleLoading(boolean: Boolean){
        if(boolean){
            progress_bar_register.visibility = View.VISIBLE
            btn_register_next.visibility = View.GONE
        }else{
            progress_bar_register.visibility = View.GONE
            btn_register_next.visibility = View.VISIBLE
        }
    }

}