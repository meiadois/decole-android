package br.com.meiadois.decole.data.repository

import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.response.CepResponse

class CepRepository(
    private val client: DecoleClient
) : RequestHandler() {
    suspend fun getCep(cep: String): CepResponse {
        return callClient {
            client.getCep(cep)
        }
    }
}