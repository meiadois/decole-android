package br.com.meiadois.decole.presentation.user.account

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityAccountBinding
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModel
import br.com.meiadois.decole.presentation.user.account.viewmodel.AccountViewModelFactory
import br.com.meiadois.decole.util.extension.longSnackbar
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
        val binding: ActivityAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_account)

        accountViewModel = ViewModelProvider(this, factory).get(AccountViewModel::class.java)

        //if (!accountViewModel.init()) showErrorMessageAndFinish()

        binding.apply {
            viewModel = accountViewModel
        }

        // colocar as duas views da img
        // colocar listener no cep para fazer a req pra buscar os outros dados
        // colocar mascara no cep e cnpj

        // para implementar o dialog va nesse site https://levelup.gitconnected.com/android-alert-dialogs-in-kotlin-tutorial-fbbe1f787015
        // e pesquise por showRadioButtonListDialog e veja tambem uma forma bonita de mostrar a escolha

        // veja esse aqui tb mandou https://material.io/develop/android/components/dialogs/

        toolbar_back_button.setOnClickListener { finish() }
    }

    private fun showErrorMessageAndFinish(){
        account_root_layout.longSnackbar(getString(R.string.getting_data_failed_error_message))
        finish()
    }
}