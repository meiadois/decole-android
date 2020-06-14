package br.com.meiadois.decole.presentation.user.partnership

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.meiadois.decole.R
import br.com.meiadois.decole.databinding.ActivityPartnershipDiscoveryBinding
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipCompanyProfileViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipCompanyProfileViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PartnershipDiscoveryActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: PartnershipCompanyProfileViewModelFactory by instance<PartnershipCompanyProfileViewModelFactory>()
    private lateinit var mViewModel: PartnershipCompanyProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)

        mViewModel =
            ViewModelProvider(this, factory).get(PartnershipCompanyProfileViewModel::class.java)

        val binding: ActivityPartnershipDiscoveryBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_partnership_discovery)

        binding.apply {
            viewModel = mViewModel
        }

        mViewModel.companyId = intent.getIntExtra(PARTNERSHIP_SEARCH_COMPANY_ID, 0)


        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = PartnershipSearchFragment()
        fragmentTransaction.add(R.id.fragment_container, fragment)
        fragmentTransaction.commit()

    }

    companion object {
        const val PARTNERSHIP_SEARCH_COMPANY_ID = "company_id"
    }
}