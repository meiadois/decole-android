package br.com.meiadois.decole.presentation.activity.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityLoginBinding
import br.com.meiadois.decole.presentation.activity.auth.viewmodel.LoginViewModel
import br.com.meiadois.decole.presentation.activity.user.HomeActivity
import br.com.meiadois.decole.util.extension.longSnackbar
import br.com.meiadois.decole.util.extension.longToast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), AuthListener {

    val loginViewModel = LoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.apply {
            viewModel = loginViewModel
        }

        loginViewModel.authListener = this
    }

    override fun onStarted() {
        toggleLoading(true)
    }

    override fun onSuccess(jwt: String) {
        toggleLoading(false)
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onFailure(message: String?) {
        toggleLoading(false)
        layout_email_input.error = loginViewModel.emailErrorMessage
        layout_password_input.error = loginViewModel.passwordErrorMessage
        message?.let {
            root_layout.longSnackbar(it)
        }
    }

    private fun toggleLoading(boolean: Boolean){
        if(boolean){
            progress_bar.visibility = View.VISIBLE
            btn_next.visibility = View.GONE
        }else{
            progress_bar.visibility = View.GONE
            btn_next.visibility = View.VISIBLE
        }
    }
}
