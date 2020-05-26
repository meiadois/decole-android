package br.com.meiadois.decole.util.extension

import br.com.meiadois.decole.data.localdb.entity.Lesson
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.localdb.entity.User
import br.com.meiadois.decole.data.model.Step
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.model.Like
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.data.network.response.*
import br.com.meiadois.decole.presentation.user.account.binding.CompanyAccountData
import br.com.meiadois.decole.presentation.user.education.binding.LessonItem
import br.com.meiadois.decole.presentation.user.education.binding.RouteItem

fun UserDTO.parseToUserEntity() = User(this.jwt, this.name, this.email, this.introduced)

fun RouteDTO.parseEntity() = Route (
    this.id,
    this.title,
    this.description,
    this.locked,
    this.progress.done,
    this.progress.total,
    this.progress.percentage
)

fun List<RouteDTO>.parseToRouteEntity() = this.map { dto ->
    Route(
        dto.id,
        dto.title,
        dto.description,
        dto.locked,
        dto.progress.done,
        dto.progress.total,
        dto.progress.percentage
    )
}

fun List<StepDTO>.parseToStepEntity() = this.map { dto ->
    Step(dto.message, 0, 0)
}

fun List<LessonDTO>.parseToLessonEntity(routeId: Long) = this.map { dto ->
    Lesson(
        dto.id,
        dto.title,
        dto.completed,
        routeId
    )
}

fun List<Route>.toRouteItemList(): List<RouteItem> {
    return this.map {
        RouteItem(it)
    }
}

fun List<Lesson>.toLessonItemList(): List<LessonItem> {
    return this.map {
        LessonItem(it)
    }
}

fun List<SegmentResponse>.toSegmentModelList(): List<Segment> {
    return this.map {
        it.toSegmentModel()
    }
}

fun SegmentResponse.toSegmentModel() = Segment(this.id, this.name)

fun CompanyResponse.toCompanyModel() :Company {
    return Company(
        id, name, cep, thumbnail, banner, cnpj, cellphone, email, description, visible, city, neighborhood, segment?.toSegmentModel()
    )
}

fun CompanySearchResponse.toCompanySearchModel() :Company {
    return Company(
        id, name, "", "", banner, cnpj, cellphone, email, description, false, "", "", segment?.toSegmentModel()
    )
}

fun List<CompanySearchResponse>.toCompanySearchModelList(): List<Company>{
    if(this.isNotEmpty())
        return this.map{
            it.toCompanySearchModel()
        }
    return listOf()
}

fun CompanyResponse.toCompanyAccountData(): CompanyAccountData {
    return CompanyAccountData(
        id, name, cep, thumbnail, banner, cnpj, cellphone, email, description, visible, city, neighborhood, segment?.id ?: -1, segment?.name
    )
}

fun List<LikeResponse>.toMatchItemList(userCompanyId: Int): List<Like> {
    return this.map {
        val userCompany: CompanyResponse
        val partnerCompany: CompanyResponse
        val isSender: Boolean

        if (it.sender_company.id == userCompanyId){
            partnerCompany = it.recipient_company
            userCompany = it.sender_company
            isSender = true
        }else{
            partnerCompany = it.sender_company
            userCompany = it.recipient_company
            isSender = false
        }

        Like(
            it.id,
            it.status,
            partnerCompany.toCompanyModel(),
            userCompany.toCompanyModel(),
            isSender
        )
    }
}
