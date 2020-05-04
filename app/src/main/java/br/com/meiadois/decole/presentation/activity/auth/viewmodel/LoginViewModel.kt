package br.com.meiadois.decole.presentation.activity.auth.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.http.client.ClientException
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.activity.auth.AuthListener
import br.com.meiadois.decole.util.Coroutines

class LoginViewModel : ViewModel() {

    var email: String = ""
    var password: String = ""
    var authListener: AuthListener? = null

    fun onLoginButtonClick(view: View) {
        authListener?.onStarted()
        if (email.isEmpty() || password.isEmpty()) {
            authListener?.onFailure("Você precisa preencher todos os campos")
            return
        }

        Coroutines.main {

            try {
                val res = UserRepository().login(email, password)
                res.jwt?.let {
                    authListener?.onSuccess(it)
                    return@main
                }
                authListener?.onFailure(res.message!!)
            } catch (ex: ClientException) {
                authListener?.onFailure(ex.message!!)
            }

        }
    }
}