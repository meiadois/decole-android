package br.com.meiadois.decole.presentation.activity.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityLoginBinding
import br.com.meiadois.decole.presentation.activity.auth.viewmodel.LoginViewModel
import br.com.meiadois.decole.presentation.activity.user.HomeActivity
import br.com.meiadois.decole.util.extension.longToast

class LoginActivity : AppCompatActivity(), AuthListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        val viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.authListener = this
    }

    override fun onStarted() {
    }

    override fun onSuccess(jwt: String) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onFailure(message: String) {
        longToast(message)
    }

}
