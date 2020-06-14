package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.model.Like
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.presentation.user.partnership.PartnerActionListener
import br.com.meiadois.decole.util.Coroutines

class PartnerBottomSheetViewModel(
    private val companyRepository: CompanyRepository
) : ViewModel() {
    lateinit var inviteInfo: Like

    lateinit var listener: PartnerActionListener

    fun deletePartnership() = Coroutines.main {

        listener.onStarted()

        companyRepository.deletePartnership(
            inviteInfo.id,
            if (inviteInfo.isSender) inviteInfo.userCompany.id else inviteInfo.partnerCompany.id,
            if (inviteInfo.isSender) inviteInfo.partnerCompany.id else inviteInfo.userCompany.id
        )

        listener.onSuccess()
    }

    fun confirmPartnership() = Coroutines.main {

        listener.onStarted()

        companyRepository.confirmPartnership(
            inviteInfo.id,
            if (inviteInfo.isSender) inviteInfo.userCompany.id else inviteInfo.partnerCompany.id,
            if (inviteInfo.isSender) inviteInfo.partnerCompany.id else inviteInfo.userCompany.id
        )

        listener.onSuccess()
    }

    fun cancelPartnership() = Coroutines.main {

        listener.onStarted()

        companyRepository.cancelPartnership(
            inviteInfo.id,
            if (inviteInfo.isSender) inviteInfo.userCompany.id else inviteInfo.partnerCompany.id,
            if (inviteInfo.isSender) inviteInfo.partnerCompany.id else inviteInfo.userCompany.id
        )

        listener.onSuccess()

    }
}