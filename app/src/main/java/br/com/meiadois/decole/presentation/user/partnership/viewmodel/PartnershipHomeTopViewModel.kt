package br.com.meiadois.decole.presentation.user.partnership.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.localdb.entity.MyCompany
import br.com.meiadois.decole.data.localdb.entity.Segment
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.repository.CompanyRepository
import br.com.meiadois.decole.data.repository.SegmentRepository

class PartnershipHomeTopViewModel(
    private val companyRepository: CompanyRepository
   // private val segmentRepository: SegmentRepository
) : ViewModel() {

    suspend fun getUserCompany(): LiveData<MyCompany> = companyRepository.getMyCompany()


   /* suspend fun getUserCompany(): LiveData<MyCompany> {

        val company = MyCompany(companyRepository.getMyCompany())
        val seg = Segment(segmentRepository.getSegmentComDb(company.segment.id))
        val company = Company(company.id,company.name,company.cep,company.thumbnail,company.banner,company.cnpj,company.cellphone,company.email,company.description,company.visible,company.city,company.neighborhood,seg)
        return company
    }*/
}
