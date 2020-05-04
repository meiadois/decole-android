package br.com.meiadois.decole.presentation.activity.auth.viewmodel

import android.util.Patterns
import android.view.View
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.http.client.ClientException
import br.com.meiadois.decole.data.repository.UserRepository
import br.com.meiadois.decole.presentation.activity.auth.AuthListener
import br.com.meiadois.decole.util.Coroutines

class LoginViewModel : ViewModel() {

    var email: String = ""
    var emailErrorMessage: String? = null
    var passwordErrorMessage: String? = null
    var password: String = ""
    var authListener: AuthListener? = null


    fun onLoginButtonClick(view: View) {
        authListener?.onStarted()
        validateEmail()
        validatePassword()
        if (emailErrorMessage != null || passwordErrorMessage != null) {
            authListener?.onFailure(null)
            return
        }

        Coroutines.main {

            try {
                val res = UserRepository().login(email, password)
                res.user?.let {
                    authListener?.onSuccess(it)
                    return@main
                }
                authListener?.onFailure(res.message!!)
            } catch (ex: ClientException) {
                authListener?.onFailure(ex.message!!)
            }

        }
    }

    private fun validatePassword() {
        passwordErrorMessage = if (password.trim().isEmpty()) "Você precisa inserir uma senha."
        else null
    }

    private fun validateEmail() {
        emailErrorMessage = if (email.trim().isEmpty()) "Você precisa inserir seu e-mail."
        else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) "Você precisa inserir um e-mail válido"
        else null

    }
}