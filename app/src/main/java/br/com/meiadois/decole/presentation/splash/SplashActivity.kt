package br.com.meiadois.decole.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivitySplashBinding
import br.com.meiadois.decole.presentation.auth.LoginActivity
import br.com.meiadois.decole.presentation.maintenance.MaintenanceActivity
import br.com.meiadois.decole.presentation.splash.viewmodel.SplashViewModel
import br.com.meiadois.decole.presentation.splash.viewmodel.SplashViewModelFactory
import br.com.meiadois.decole.presentation.user.HomeActivity
import br.com.meiadois.decole.presentation.welcome.WelcomeInfoActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SplashActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: SplashViewModelFactory by instance<SplashViewModelFactory>()

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivitySplashBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_splash)

        splashViewModel = ViewModelProvider(this, factory).get(SplashViewModel::class.java)

        binding.apply {
            viewModel = splashViewModel
        }

        // TODO validar com o backend e remover o delay se necessário
        Handler().postDelayed({
            startNextActivity(false)
        }, 1000)
    }

    private fun startNextActivity(underMaintenance: Boolean) {

        if (underMaintenance)
            Intent(this, MaintenanceActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        else {
            splashViewModel.getLoggedInUser().observe(this, Observer { user ->
                if (user != null) {
                    startNonMaintenanceActivity(user.introduced)
                } else {
                    Intent(this, LoginActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                }
            })
        }

    }

    private fun startNonMaintenanceActivity(introduced: Boolean) {
        if (introduced)
            Intent(this, HomeActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        else
            Intent(this, WelcomeInfoActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
    }

}