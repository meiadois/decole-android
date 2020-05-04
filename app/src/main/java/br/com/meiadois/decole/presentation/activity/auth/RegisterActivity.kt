package br.com.meiadois.decole.presentation.activity.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.activity.auth.viewmodel.RegisterViewModel
import br.com.meiadois.decole.presentation.activity.user.HomeActivity
import br.com.meiadois.decole.util.extension.longToast

class RegisterActivity : AppCompatActivity(), AuthListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityRegisterBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_register)
        val viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.authListener = this
    }

    override fun onStarted() {
    }

    override fun onSuccess(jwt: String) {
        Log.i("DEBUG", "JWT: $jwt")
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onFailure(message: String) {
        longToast(message)
    }

}