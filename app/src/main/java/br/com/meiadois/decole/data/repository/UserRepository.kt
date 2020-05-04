package br.com.meiadois.decole.data.repository

import br.com.meiadois.decole.data.http.client.DecoleClient
import br.com.meiadois.decole.data.http.client.RequestHandler
import br.com.meiadois.decole.data.http.request.LoginRequest
import br.com.meiadois.decole.data.http.response.LoginResponse

class UserRepository : RequestHandler() {

    suspend fun login(email: String, password: String): LoginResponse {
        return handle {
            DecoleClient().login(LoginRequest(email, password))
        }
    }
}