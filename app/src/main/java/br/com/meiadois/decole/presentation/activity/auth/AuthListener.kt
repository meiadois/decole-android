package br.com.meiadois.decole.presentation.activity.auth

import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.http.response.LoginResponse
import br.com.meiadois.decole.data.model.User

interface AuthListener {
    fun onStarted()
    fun onSuccess(user: User)
    fun onFailure(message: String?)
}