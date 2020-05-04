package br.com.meiadois.decole.presentation.activity.auth

interface AuthListener {
    fun onStarted()
    fun onSuccess(jwt: String)
    fun onFailure(message: String?)
}