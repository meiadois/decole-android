package br.com.meiadois.decole.util.extension

import br.com.meiadois.decole.data.localdb.entity.Lesson
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.localdb.entity.User
import br.com.meiadois.decole.data.model.Company
import br.com.meiadois.decole.data.model.Like
import br.com.meiadois.decole.data.model.Segment
import br.com.meiadois.decole.data.network.response.*
import br.com.meiadois.decole.presentation.user.education.binding.LessonItem
import br.com.meiadois.decole.presentation.user.education.binding.RouteItem

fun UserDTO.parseToUserEntity() = User(this.jwt, this.name, this.email, this.introduced)

fun List<RouteDTO>.parseToRouteEntity() = this.map { dto ->
    Route(
        dto.id,
        dto.title,
        dto.description,
        false,
        dto.progress.percentage
    )
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

fun SegmentResponse.toSegmentModel() = Segment(this.name)

fun CompanyResponse.toCompanyModel() :Company {
    return Company(
        this.id,
        this.name,
        this.cep,
        this.thumbnail,
        this.banner,
        this.cnpj,
        this.cellphone,
        this.email,
        this.description,
        this.visible,
        this.city,
        this.neighborhood,
        this.state,
        this.street,
        this.segment?.toSegmentModel()
    )
}

fun List<LikeResponse>.toMatchItemList(userCompanyId: Int): List<Like> {
    return this.map {
        val partnerCompany = if (it.sender_company.id == userCompanyId) it.recipient_company else it.sender_company
        Like(
            it.id,
            it.status,
            partnerCompany.toCompanyModel()
        )
    }
}
