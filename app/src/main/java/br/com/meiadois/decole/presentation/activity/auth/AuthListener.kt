package br.com.meiadois.decole.presentation.activity.auth

import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.http.response.LoginResponse

interface AuthListener {
    fun onStarted()
    fun onSuccess(jwt: String)
    fun onFailure(message: String)
}