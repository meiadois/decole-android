package br.com.meiadois.decole.util.extension

import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.localdb.entity.User
import br.com.meiadois.decole.data.model.Lesson
import br.com.meiadois.decole.data.network.response.RouteDTO
import br.com.meiadois.decole.data.network.response.UserDTO
import br.com.meiadois.decole.presentation.user.education.LessonItem
import br.com.meiadois.decole.presentation.user.education.RouteItem

fun UserDTO.parseEntity() = User(this.jwt, this.name, this.email, this.introduced)

fun List<RouteDTO>.parseEntity() = this.map { dto ->
    Route(
        dto.id,
        dto.title,
        dto.description,
        dto.locked,
        dto.progress
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
