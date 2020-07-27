package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Like
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.presentation.user.partnership.PartnerActionListener
import br.com.meiadois.decole.util.Coroutines
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

class PartnerBottomSheetViewModel(
    private val companyRepository: CompanyRepository
) : ViewModel() {
    lateinit var inviteInfo: Like

    lateinit var listener: PartnerActionListener

    fun deletePartnership() = Coroutines.main {
        try {
            listener.onStarted()
            companyRepository.deletePartnership(
                inviteInfo.id,
                if (inviteInfo.isSender) inviteInfo.userCompany.id else inviteInfo.partnerCompany.id,
                if (inviteInfo.isSender) inviteInfo.partnerCompany.id else inviteInfo.userCompany.id
            )
            listener.onSuccess()
        } catch (ex: Exception) {
            Firebase.crashlytics.recordException(ex)
            listener.onFailure()
        }
    }

    fun confirmPartnership() = Coroutines.main {
        try {
            listener.onStarted()
            companyRepository.confirmPartnership(
                inviteInfo.id,
                if (inviteInfo.isSender) inviteInfo.userCompany.id else inviteInfo.partnerCompany.id,
                if (inviteInfo.isSender) inviteInfo.partnerCompany.id else inviteInfo.userCompany.id
            )
            listener.onSuccess()
        } catch (ex: Exception) {
            Firebase.crashlytics.recordException(ex)
            listener.onFailure()
        }
    }

    fun cancelPartnership() = Coroutines.main {
        try {
            listener.onStarted()
            companyRepository.cancelPartnership(
                inviteInfo.id,
                if (inviteInfo.isSender) inviteInfo.userCompany.id else inviteInfo.partnerCompany.id,
                if (inviteInfo.isSender) inviteInfo.partnerCompany.id else inviteInfo.userCompany.id
            )
            listener.onSuccess()
        } catch (ex: Exception) {
            Firebase.crashlytics.recordException(ex)
            listener.onFailure()
        }
    }

    fun deleteLike() = Coroutines.main {
        try {
            listener.onStarted()
            companyRepository.deleteLike(inviteInfo.id)
            listener.onSuccess()
        } catch (ex: Exception) {
            Firebase.crashlytics.recordException(ex)
            listener.onFailure()
        }
    }
}