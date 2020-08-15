package br.com.meiadois.decole.presentation.user.account.listener

import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum

interface AccountListener {
    fun riseValidationError(field: FieldsEnum, errorMessage: String)
    fun onActionError(errorMessage: String?)
    fun onActionSuccess()
    fun onActionStarted()
}