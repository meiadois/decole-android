package br.com.meiadois.decole.presentation.activity.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.localdb.entity.User
import br.com.meiadois.decole.databinding.ActivityLoginBinding
import br.com.meiadois.decole.presentation.activity.auth.viewmodel.LoginViewModel
import br.com.meiadois.decole.presentation.activity.auth.viewmodel.LoginViewModelFactory
import br.com.meiadois.decole.presentation.activity.user.HomeActivity
import br.com.meiadois.decole.presentation.activity.welcome.WelcomeInfoActivity
import br.com.meiadois.decole.util.extension.longSnackbar
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), AuthListener, KodeinAware {

    override val kodein by kodein()
    private val factory: LoginViewModelFactory by instance()

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)

        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        binding.apply {
            viewModel = loginViewModel
        }

        loginViewModel.authListener = this

        loginViewModel.getLoggedInUser().observe(this, Observer { user ->
            user?.let {
                startNextActivity(it)
            }
        })
    }

    override fun onStarted() {
        toggleLoading(true)
    }

    override fun onSuccess(user: User) {
        toggleLoading(false)
        startNextActivity(user)
    }

    override fun onFailure(message: String?) {
        toggleLoading(false)
        layout_email_input.error = loginViewModel.emailErrorMessage
        layout_password_input.error = loginViewModel.passwordErrorMessage
        message?.let {
            root_layout.longSnackbar(it)
        }
    }

    private fun toggleLoading(boolean: Boolean) {
        if (boolean) {
            progress_bar.visibility = View.VISIBLE
            button_group.visibility = View.GONE
        } else {
            progress_bar.visibility = View.GONE
            button_group.visibility = View.VISIBLE
        }
    }

    private fun startNextActivity(user: User) {
        if (user.introduced) {
            Intent(this, HomeActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        Intent(this, WelcomeInfoActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }

    }

}
