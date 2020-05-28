package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.*
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.model.Like
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanyModel
import br.com.meiadois.decole.util.extension.toLikeModelList

class PartnershipHomeBottomViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    val matchesList : MutableLiveData<List<Like>> = MutableLiveData()
    val sentLikesList : MutableLiveData<List<Like>> = MutableLiveData()
    val receivedLikesList : MutableLiveData<List<Like>> = MutableLiveData()
    val recyclerDataSet : MutableLiveData<List<Like>> = MutableLiveData()
    var company: Company? = null

    fun getUserMatches(companyId: Int){
        Coroutines.main {
            matchesList.value = userRepository.getUserMatches().toLikeModelList(companyId)
        }
    }

    fun getSentLikes(companyId: Int){
        Coroutines.main {
            sentLikesList.value = userRepository.getUserSentLikes().map {
                Like(
                    it.id,
                    it.status,
                    it.recipient_company.toCompanyModel(),
                    Company(companyId, "", "", "", "", "", "", "", "", false, "", "", null),
                    true
                )
            }
        }
    }

    fun getReceivedLikes(companyId: Int){
        Coroutines.main {
            receivedLikesList.value = userRepository.getUserReceivedLikes().map {
                Like(
                    it.id,
                    it.status,
                    it.sender_company.toCompanyModel(),
                    Company(companyId, "", "", "", "", "", "", "", "", false, "", "", null),
                    false
                )
            }
        }
    }

    fun removeMatch(likeId: Int){
        matchesList.value = matchesList.value?.filter { like -> like.id != likeId }
    }

    fun removeSentLike(likeId: Int){
        sentLikesList.value = matchesList.value?.filter { like -> like.id != likeId }
    }

    fun removeReceivedLike(likeId: Int){
        receivedLikesList.value = matchesList.value?.filter { like -> like.id != likeId }
    }

    suspend fun getUserCompany() : Company {
        if (company == null)
            company = userRepository.getUserCompany().toCompanyModel()
        return company!!
    }
}