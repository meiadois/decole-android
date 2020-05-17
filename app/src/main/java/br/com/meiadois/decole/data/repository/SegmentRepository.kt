package br.com.meiadois.decole.data.repository

import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.response.SegmentResponse

class SegmentRepository(
    private val client: DecoleClient
) : RequestHandler() {
    suspend fun getAllSegments(): List<SegmentResponse> {
        return callClient {
            client.getAllSegments()
        }
    }
}