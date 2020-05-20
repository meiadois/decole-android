package br.com.meiadois.decole.presentation.user.account

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityAccountBinding
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModel
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModelFactory
import kotlinx.android.synthetic.main.activity_account.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AccountActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: AccountViewModelFactory by instance<AccountViewModelFactory>()
    private lateinit var accountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(this, factory).get(AccountViewModel::class.java)
        val binding: ActivityAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_account)
        binding.apply {
            viewModel = accountViewModel
            lifecycleOwner = this@AccountActivity
        }
        toolbar_back_button.setOnClickListener { finish() }
    }
}