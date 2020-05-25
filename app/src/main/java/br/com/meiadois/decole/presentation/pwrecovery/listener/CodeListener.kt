package br.com.meiadois.decole.presentation.pwrecovery.listener

interface CodeListener {
    fun onStarted()
    fun onFailure(message: String?)
    fun onSuccess()
}