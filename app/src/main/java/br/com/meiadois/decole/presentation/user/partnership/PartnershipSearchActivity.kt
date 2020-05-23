package br.com.meiadois.decole.presentation.user.partnership

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.com.meiadois.decole.R
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipHomeTopViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipHomeTopViewModelFactory
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipSearchPartnerViewModel
import br.com.meiadois.decole.presentation.user.partnership.viewmodel.PartnershipSearchPartnerViewModelFactory
import okhttp3.internal.Internal.instance
import org.kodein.di.android.kodein
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PartnershipSearchActivity: AppCompatActivity(){
    override val kodein by kodein()
    private val factory: PartnershipSearchPartnerViewModelFactory by instance<PartnershipSearchPartnerViewModelFactory>()
    private lateinit var viewModel: PartnershipSearchPartnerViewModel

    fun onCreate(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_partnership_home_top, container, false)
    }
}