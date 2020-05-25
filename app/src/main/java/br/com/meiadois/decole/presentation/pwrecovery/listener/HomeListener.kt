package br.com.meiadois.decole.presentation.pwrecovery.listener

interface HomeListener {
    fun onStarted()
    fun onFailure(message: String?)
    fun onSuccess()
}