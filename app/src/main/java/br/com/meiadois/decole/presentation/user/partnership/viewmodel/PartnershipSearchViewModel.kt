package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.data.repository.SegmentRepository
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanyModelList
import br.com.meiadois.decole.util.extension.toSegmentModelList

class PartnershipSearchViewModel (
    private val companyRepository: CompanyRepository,
    private val segmentRepository: SegmentRepository
) : ViewModel() {

    var segments: MutableLiveData<List<Segment>> = MutableLiveData<List<Segment>>()
    var companies: MutableLiveData<List<Company>> = MutableLiveData<List<Company>>()
    init{
        getSegments()
    }

    private fun getSegments() {
        Coroutines.main {
            try {
                segments.value = segmentRepository.getAllSegments().toSegmentModelList()
            } catch (ex: Exception) {
                Log.i("getSegments.ex", ex.message!!)
            }
        }
    }

    private fun getCompanyBySegment(segmentId: Int) {
        Coroutines.main {
            try {
                companies.value = companyRepository.getCompaniesBySegment(segmentId).toCompanyModelList()
            } catch (ex: Exception) {
                Log.i("getCompanyBySegment.ex", ex.message!!)
            }
        }
    }
    private fun getAllCompanies(){
        Coroutines.main {
            try{
                companies.value = companyRepository.getAllCompanies().toCompanyModelList()
            }catch (ex: Exception){
                Log.i("getAllCompanies.ex", ex.message!!)
            }
        }
    }
}
