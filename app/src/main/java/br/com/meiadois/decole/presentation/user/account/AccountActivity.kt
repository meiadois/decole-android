package br.com.meiadois.decole.presentation.user.account

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityAccountBinding
import br.com.meiadois.decole.databinding.ActivityLoginBinding
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModel
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AccountActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: AccountViewModelFactory by instance<AccountViewModelFactory>()
    private lateinit var accountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val binding: ActivityAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_account)

        accountViewModel = ViewModelProvider(this, factory).get(AccountViewModel::class.java)

        binding.apply {
            viewModel = accountViewModel
        }
        // va botar o boolean de mostrar ou nao na pesquisa mano

        // para implementar o dialog va nesse site https://levelup.gitconnected.com/android-alert-dialogs-in-kotlin-tutorial-fbbe1f787015
        // e pesquise por showRadioButtonListDialog e veja tambem uma forma bonita de mostrar a escolha

        // veja esse aqui tb q o cara la mandou https://material.io/develop/android/components/dialogs/
    }
}