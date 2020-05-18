package br.com.meiadois.decole.data.repository

import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.request.LikeRequest
import br.com.meiadois.decole.data.network.response.CompanyResponse
import br.com.meiadois.decole.data.network.response.LikePutResponse

class CompanyRepository(
    private val client: DecoleClient
) : RequestHandler() {
    suspend fun getCompanyById(companyId: Int): CompanyResponse {
        return callClient {
            client.getCompanyById(companyId)
        }
    }

    suspend fun undoPartnership(likeId: Int, senderId: Int, recipientId: Int): LikePutResponse {
        return callClient {
            client.undoPartnership(likeId, LikeRequest("deleted", senderId, recipientId))
        }
    }
}