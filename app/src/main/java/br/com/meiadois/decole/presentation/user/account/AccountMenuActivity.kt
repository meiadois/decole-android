package br.com.meiadois.decole.presentation.user.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.auth.LoginActivity
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountMenuViewModel
import br.com.meiadois.decole.presentation.user.account.viewmodel.factory.AccountMenuViewModelFactory
import br.com.meiadois.decole.util.Coroutines
import kotlinx.android.synthetic.main.activity_user_menu.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AccountMenuActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: AccountMenuViewModelFactory by instance()
    private lateinit var viewModel: AccountMenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_menu)

        viewModel = ViewModelProvider(this, factory).get(AccountMenuViewModel::class.java)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        toolbar_back_button.setOnClickListener { finish() }

        account_button.setOnClickListener {
            Intent(this, AccountProfileActivity::class.java)
                .also { startActivity(it) }
        }

        change_pw_button.setOnClickListener {
            Intent(this, AccountChangePasswordActivity::class.java)
                .also { startActivity(it) }
        }

        company_button.setOnClickListener {
            Intent(this, AccountCompanyActivity::class.java)
                .also { startActivity(it) }
        }

        logout_button.setOnClickListener {
            Coroutines.main {
                viewModel.onLogOutButtonClick()
                Intent(this, LoginActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }
        }
    }
}