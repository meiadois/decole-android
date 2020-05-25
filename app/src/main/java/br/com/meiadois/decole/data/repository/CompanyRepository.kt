package br.com.meiadois.decole.data.repository

import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.request.CompanyBySegmentRequest
import br.com.meiadois.decole.data.network.request.LikeRequest
import br.com.meiadois.decole.data.network.request.LikeSenderRequest
import br.com.meiadois.decole.data.network.response.*

class CompanyRepository(
    private val client: DecoleClient
) : RequestHandler() {
    suspend fun getCompanyById(companyId: Int): CompanyResponse {
        return callClient {
            client.getCompanyById(companyId)
        }
    }

    suspend fun getCompaniesBySegment(segmentId: Int): List<CompanySearchResponse>{
        return callClient {
            client.getCompaniesBySegment(segmentId)
        }
    }

    suspend fun getAllCompanies(): List<CompanySearchResponse>{
        return callClient{
            client.getAllCompanies()
        }
    }

    suspend fun undoPartnership(likeId: Int, senderId: Int, recipientId: Int): LikePutResponse {
        return callClient {
            client.undoPartnership(likeId, LikeRequest("deleted", senderId, recipientId))
        }
    }

    suspend fun sendLikes(senderId: Int, recipientId: Int): LikePutResponse{
        return callClient {
            client.sendLike(LikeSenderRequest(senderId, recipientId))
        }
    }
}