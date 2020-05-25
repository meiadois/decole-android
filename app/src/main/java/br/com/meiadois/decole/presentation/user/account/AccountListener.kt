package br.com.meiadois.decole.presentation.user.account

import br.com.meiadois.decole.presentation.user.account.binding.FieldsEnum

interface AccountListener {
    fun riseError(field: FieldsEnum, errorMessage: String)
}