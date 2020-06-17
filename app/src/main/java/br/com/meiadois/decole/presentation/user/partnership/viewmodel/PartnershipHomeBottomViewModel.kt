package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.*
import br.com.meiadois.decole.data.localdb.entity.MyCompany
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.model.Like
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanyModel
import br.com.meiadois.decole.util.extension.toLikeModelList

class PartnershipHomeBottomViewModel(
    private val companyRepository: CompanyRepository
) : ViewModel() {
    val matchesList: MutableLiveData<List<Like>> = MutableLiveData()
    val sentLikesList: MutableLiveData<List<Like>> = MutableLiveData()
    val receivedLikesList: MutableLiveData<List<Like>> = MutableLiveData()
    val recyclerDataSet: MutableLiveData<List<Like>> = MutableLiveData()
    var company: Company? = null

    suspend fun getUserMatches(companyId: Int) {
        matchesList.value = companyRepository.getUserMatches().toLikeModelList(companyId)
    }

    suspend fun getSentLikes(companyId: Int) {
        sentLikesList.value = companyRepository.getUserSentLikes().map {
            Like(
                it.id,
                it.status,
                it.recipient_company.toCompanyModel(),
                Company(id = companyId),
                true
            )
        }
    }

    suspend fun getReceivedLikes(companyId: Int) {
        receivedLikesList.value = companyRepository.getUserReceivedLikes().map {
            Like(
                it.id,
                it.status,
                it.sender_company.toCompanyModel(),
                Company(id = companyId),
                false
            )
        }
    }

    fun removeMatch(likeId: Int) {
        matchesList.value = matchesList.value?.filter { like -> like.id != likeId }
    }

    fun removeSentLike(likeId: Int) {
        sentLikesList.value = sentLikesList.value?.filter { like -> like.id != likeId }
    }

    fun removeReceivedLike(likeId: Int) {
        receivedLikesList.value = receivedLikesList.value?.filter { like -> like.id != likeId }
    }

    suspend fun getUserCompany(): LiveData<MyCompany> = companyRepository.getMyCompany()
}