package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import android.media.Image
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.data.network.response.CompanyResponse
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.util.Coroutines

class PartnershipCompanyProfileViewModel(
private val companyRepository: CompanyRepository
) : ViewModel() {
    
    var name: String? = null
    var banner: String? = null
    var description: String? = null
    var segment: String? = null
    var listSegment: List<Segment>? = null
}