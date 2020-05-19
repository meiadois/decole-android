package br.com.meiadois.decole.presentation.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityWelcomeInfoBinding
import br.com.meiadois.decole.presentation.auth.LoginActivity
import br.com.meiadois.decole.presentation.welcome.viewmodel.WelcomeInfoViewModel
import br.com.meiadois.decole.presentation.welcome.viewmodel.WelcomeInfoViewModelFactory
import br.com.meiadois.decole.util.extension.longSnackbar
import kotlinx.android.synthetic.main.activity_welcome_info.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class WelcomeInfoActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: WelcomeInfoViewModelFactory by instance()

    private lateinit var mViewModel: WelcomeInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityWelcomeInfoBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_welcome_info)

        mViewModel = ViewModelProvider(this, factory).get(WelcomeInfoViewModel::class.java)

        binding.apply {
            viewModel = mViewModel
        }

        handleMessage()

        btn_next.setOnClickListener {
            Intent(this, WelcomeSlideActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        btn_logout.setOnClickListener {
            mViewModel.onLogoutButtonClicked()
            Intent(this, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }

    private fun handleMessage() {
        val message = intent.getStringExtra("message")
        message?.let {
            root_layout.longSnackbar(it)
        }
    }
}
