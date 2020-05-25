package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import android.media.Image
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.data.network.response.CompanyResponse
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.data.repository.SegmentRepository
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanyModelList
import br.com.meiadois.decole.util.extension.toCompanySearchModelList
import br.com.meiadois.decole.util.extension.toSegmentModelList

class PartnershipCompanyProfileViewModel(
private val companyRepository: CompanyRepository,
private val segmentRepository: SegmentRepository
) : ViewModel() {
    
    var name: String? = null
    var banner: String? = null
    var description: String? = null
    var segment: String? = null

    var state: Int = 0

    var company: MutableLiveData<Company> = MutableLiveData()

    var segments: MutableLiveData<List<Segment>> = MutableLiveData<List<Segment>>()
    var companies: MutableLiveData<List<Company>> = MutableLiveData<List<Company>>()

    init{
        getSegments()
        getAllCompanies()
//
    }

    fun getUpdateCompany(){
        state+=1
        company.postValue(companies.value?.get(state))
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

    fun getCompanyBySegment(segmentId: Int) {
        Coroutines.main {
            try {
                companies.value = companyRepository.getCompaniesBySegment(segmentId).toCompanySearchModelList()
            } catch (ex: Exception) {
                Log.i("getCompanyBySegment.ex", ex.message!!)
            }
        }
    }

    fun getAllCompanies(){
        Coroutines.main {
            try{
                companies.value = companyRepository.getAllCompanies().toCompanyModelList()
                company.value = companies.value?.get(0)
            }catch (ex: Exception){
                Log.i("getAllCompanies.ex", ex.message!!)
            }
        }
    }

    fun sendLike( senderId: Int, recipientId: Int ){
        var a = 1
        Coroutines.main{
            try{
                companyRepository.sendLikes(senderId, recipientId)
            }catch(ex: Exception){
                Log.i("sendLikesInterno.ex", ex.message!!)
            }

        }
    }
}