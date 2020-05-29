package br.com.meiadois.decole.data.repository

import androidx.lifecycle.MutableLiveData
import br.com.meiadois.decole.data.localdb.AppDatabase
import br.com.meiadois.decole.data.localdb.entity.MyCompany
import br.com.meiadois.decole.data.localdb.entity.Segment
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.network.RequestHandler
import br.com.meiadois.decole.data.network.client.DecoleClient
import br.com.meiadois.decole.data.network.request.CompanyBySegmentRequest
import br.com.meiadois.decole.data.network.request.LikeRequest
import br.com.meiadois.decole.data.network.request.LikeSenderRequest
import br.com.meiadois.decole.data.network.response.*
import br.com.meiadois.decole.util.Coroutines
import br.com.meiadois.decole.util.extension.toCompanyModel

class CompanyRepository(
    private val client: DecoleClient,
    private val db: AppDatabase,
    private val segmentRepository: SegmentRepository
) : RequestHandler() {
    private val companyuser = MutableLiveData<Company>()

    init {
        companyuser.observeForever{
            saveCompany(it)
        }
    }

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

    suspend fun fetchCompany() {
        val res = callClient { client.getUserCompany() }
        companyuser.postValue(res.toCompanyModel())
    }
    // muda para company entity
    // metodo para salvar no banco a company
    private fun saveCompany(company:Company) {
        val comp : MyCompany = MyCompany(company.id,company.name,company.cep,company.thumbnail,company.banner,company.cnpj,company.cellphone,company.email,company.description,company.visible,company.city,company.neighborhood,2);
        Coroutines.io {
            db.getCompanyDao().upsert(comp);
        }
    }

    suspend fun getUserCompanyDB(): Company {
        val companyDb = db.getCompanyDao().getUserCompanyLocal()
        var segmentDb: Segment = db.getSegmentDao().getSegmentLocal(companyDb.segmentId)
        var segment: br.com.meiadois.decole.data.model.Segment
//        if(segmentDb == null){
//            segment = br.com.meiadois.decole.data.model.Segment(segmentDb.id, segmentDb.name)
//        }else{
        //segmentRepository.fetchSegment()
        segment = br.com.meiadois.decole.data.model.Segment(segmentDb.id, segmentDb.name)

        val company: Company = Company(companyDb.id,companyDb.name,companyDb.cep,companyDb.thumbnail,companyDb.banner,companyDb.cnpj,companyDb.cellphone,companyDb.email,companyDb.description,companyDb.visible,companyDb.city,companyDb.neighborhood,segment);
        return company
    }

}