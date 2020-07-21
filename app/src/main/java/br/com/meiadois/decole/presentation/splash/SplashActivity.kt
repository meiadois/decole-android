package br.com.meiadois.decole.presentation.splash

import android.content.Intent
import android.net.Uri
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
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.dialog.CustomDialog
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

        Handler().postDelayed({
            getAppInfo()
        }, 500)
    }

    private fun getAppInfo() {
        Coroutines.main {
            val appInfo = splashViewModel.getAppInfo()
            if (needToUpdate(appInfo.appVersion))
                showUpdateDialog()
            else
                startNextActivity(appInfo.underMaintenance.toBoolean())
        }
    }

    private fun needToUpdate(latestVersionString: String): Boolean {
        try {
            val appVersionString = packageManager.getPackageInfo(packageName, 0).versionName
            val appVersionArray = appVersionString.split('.')
            val latestVersionArray = latestVersionString.split('.')

            appVersionArray.forEachIndexed { index, element ->
                if (element.toInt() < latestVersionArray[index].toInt())
                    return true
            }
        } catch (ex: Exception) {
        }
        return false
    }

    private fun showUpdateDialog() {
        val message = getString(R.string.new_app_version_dialog_message)
        val title = getString(R.string.new_app_version_dialog_title)

        CustomDialog(this)
            .create(title, message)
            .setIcon(R.drawable.ic_system_update_green)
            .setNegativeButton(getString(R.string.exit)) { finish() }
            .setPositiveButton(getString(R.string.advance)) {
                openPlayStore()
                finish()
            }
            .show()
    }

    private fun openPlayStore() {
        val appPackageName = packageName
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (ex: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    private fun startNextActivity(underMaintenance: Boolean) {
        if (underMaintenance)
            Intent(this, MaintenanceActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        else
            splashViewModel.getLoggedInUser().observe(this, Observer { user ->
                if (user != null)
                    startNonMaintenanceActivity(user.introduced)
                else
                    Intent(this, LoginActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
            })
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