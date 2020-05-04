package br.com.meiadois.decole.data.repository

import br.com.meiadois.decole.data.http.client.DecoleClient
import br.com.meiadois.decole.data.http.client.RequestHandler
import br.com.meiadois.decole.data.http.request.LoginRequest
import br.com.meiadois.decole.data.http.request.RegisterRequest
import br.com.meiadois.decole.data.http.response.LoginResponse
import br.com.meiadois.decole.data.http.response.RegisterResponse

class UserRepository : RequestHandler() {

    suspend fun login(email: String, password: String): LoginResponse {
        return handle {
            DecoleClient().login(LoginRequest(email, password))
        }
    }
    suspend fun register(username: String,email: String, password: String): RegisterResponse {
        return handle {
            DecoleClient().register(RegisterRequest(username, email, password))
        }
    }
}