package br.com.meiadois.decole.data.repository

import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.response.CompanyResponse

class CompanyRepository(
    private val client: DecoleClient
) : RequestHandler() {
    suspend fun getCompanyById(companyId: Int): CompanyResponse {
        return callClient {
            client.getCompanyById(companyId)
        }
    }
}