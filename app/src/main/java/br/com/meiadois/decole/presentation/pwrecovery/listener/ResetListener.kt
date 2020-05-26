package br.com.meiadois.decole.presentation.pwrecovery.listener

interface ResetListener {
    fun onStarted()
    fun onFailure(message: String?)
    fun onSuccess()
}