package br.com.meiadois.decole.presentation.user.education

import java.lang.Exception

interface InteractiveModeListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(ex: Exception)
}