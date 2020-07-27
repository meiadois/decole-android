package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.data.repository.SegmentRepository
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanySearchModelList
import br.com.meiadois.decole.util.extension.toSegmentModelList

class PartnershipCompanyProfileViewModel(
    private val companyRepository: CompanyRepository,
    private val segmentRepository: SegmentRepository
) : ViewModel() {

    var companyId: Int? = null

    var name: String? = null
    var banner: String? = null
    var description: String? = null

    var segmentClicked: String? = null
    var isFromFilterScreen: Boolean = false
    var segmentFilter: MutableLiveData<String> = MutableLiveData()

    var company: MutableLiveData<Company> = MutableLiveData()

    var segments: MutableLiveData<List<Segment>> = MutableLiveData()
    var companies: MutableLiveData<List<Company>> = MutableLiveData()

    suspend fun init() {
        getSegments()
        getAllCompanies()
    }

    fun getUpdateCompany() :Boolean{
        if (companies.value!!.isNotEmpty()) {
            company.postValue(companies.value?.get(0))
            return true
        }
        return false
    }

    private suspend fun getSegments() {
        segments.value = segmentRepository.getAllSegmentsHasCompanies().toSegmentModelList()
    }

    suspend fun getCompaniesBySegment(segmentId: Int) {
        companies.value = companyRepository.getCompaniesBySegment(segmentId).toCompanySearchModelList()
    }

    suspend fun getAllCompanies() {
        companies.value = companyRepository.getAllCompanies().toCompanySearchModelList()
    }

    fun removeCompany(companyId: Int) {
        companies.value = companies.value?.filter { company -> company.id != companyId }
    }

    suspend fun sendLike(senderId: Int, recipientId: Int) {
        companyRepository.sendLikes(senderId, recipientId)
    }
}