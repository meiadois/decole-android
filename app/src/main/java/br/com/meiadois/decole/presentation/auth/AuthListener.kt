package br.com.meiadois.decole.presentation.auth

import br.com.meiadois.decole.data.localdb.entity.User

interface AuthListener {
    fun onStarted()
    fun onSuccess(user: User, message: String?)
    fun onFailure(message: String?)
}