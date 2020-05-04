package br.com.meiadois.decole.presentation.activity.auth.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.http.client.ClientException
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.activity.auth.AuthListener
import br.com.meiadois.decole.util.Coroutines

class RegisterViewModel : ViewModel() {

    var username: String = ""
    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""
    var authListener: AuthListener? = null

    fun onRegisterButtonClick(view: View) {
        authListener?.onStarted()
        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || confirmPassword.isEmpty()) {
            authListener?.onFailure("Você precisa preencher todos os campos")
            return
        }
        if(password.compareTo(confirmPassword) != 0) {
            authListener?.onFailure("As senhas não coincidem")
            return
        }

        Coroutines.main {

            try {
                val res = UserRepository().register(username, email, password)
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