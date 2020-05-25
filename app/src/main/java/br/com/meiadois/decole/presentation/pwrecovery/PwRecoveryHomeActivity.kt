package br.com.meiadois.decole.presentation.pwrecovery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityPwRecoveryHomeBinding
import br.com.meiadois.decole.presentation.pwrecovery.viewmodel.PwRecoveryViewModel
import br.com.meiadois.decole.presentation.pwrecovery.viewmodel.PwRecoveryViewModelFactory
import kotlinx.android.synthetic.main.activity_pw_recovery_home.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PwRecoveryHomeActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: PwRecoveryViewModelFactory by instance()

    private lateinit var mViewModel: PwRecoveryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityPwRecoveryHomeBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_pw_recovery_home)

        mViewModel = ViewModelProvider(this, factory).get(PwRecoveryViewModel::class.java)

        binding.apply {
            viewModel = mViewModel
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = PwRecoveryHomeFragment()
        fragmentTransaction.add(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}
