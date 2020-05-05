package br.com.meiadois.decole.presentation.activity.auth

import br.com.meiadois.decole.data.localdb.entity.User

interface AuthListener {
    fun onStarted()
    fun onSuccess(user: User)
    fun onFailure(message: String?)
}