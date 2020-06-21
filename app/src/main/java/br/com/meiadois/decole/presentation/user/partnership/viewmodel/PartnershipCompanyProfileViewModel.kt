package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import android.util.Log
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

    var state: Int = 0

    var segmentClicked: String? = null
    var segmentFilter: MutableLiveData<String> = MutableLiveData()

    var company: MutableLiveData<Company> = MutableLiveData()

    var segments: MutableLiveData<List<Segment>> = MutableLiveData()
    var companies: MutableLiveData<List<Company>> = MutableLiveData()

    init {
        getSegments()
        getAllCompanies()
    }

    fun getUpdateCompany() {
        try {
            if (state < companies.value!!.count() - 1)
                state += 1
            else
                state = 0
            company.postValue(companies.value?.get(state))
        } catch (ex: Exception) {
            Log.i("getUpdateCompany.ex", ex.message!!)
        }
    }

    private fun getSegments() {
        Coroutines.main {
            try {
                segments.value = segmentRepository.getAllSegmentsHasCompanies().toSegmentModelList()
            } catch (ex: Exception) {
                Log.i("getSegments.ex", ex.message!!)
            }
        }
    }

    fun getCompaniesBySegment(segmentId: Int) {
        Coroutines.main {
            try {
                companies.value =
                    companyRepository.getCompaniesBySegment(segmentId).toCompanySearchModelList()
            } catch (ex: Exception) {
                Log.i("getCompanyBySegment.ex", ex.message!!)
            }
        }
    }

    fun getAllCompanies() {
        Coroutines.main {
            try {
                companies.value = companyRepository.getAllCompanies().toCompanySearchModelList()
                if (companies.value!!.isNotEmpty())
                    company.value = companies.value?.get(0)
            } catch (ex: Exception) {
                Log.i("getAllCompanies.ex", ex.message!!)
            }
        }
    }

    fun removeCompany(companyId: Int) {
        companies.value = companies.value?.filter { company -> company.id != companyId }
    }

    fun sendLike(senderId: Int, recipientId: Int) {
        Coroutines.main {
            try {
                companyRepository.sendLikes(senderId, recipientId)
            } catch (ex: Exception) {
                Log.i("sendLike.ex", ex.message!!)
            }

        }
    }
}