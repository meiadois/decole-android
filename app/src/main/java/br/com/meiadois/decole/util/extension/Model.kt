package br.com.meiadois.decole.util.extension

import br.com.meiadois.decole.data.localdb.entity.Lesson
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.localdb.entity.User
import br.com.meiadois.decole.data.network.response.LessonDTO
import br.com.meiadois.decole.data.network.response.RouteDTO
import br.com.meiadois.decole.data.network.response.UserDTO
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
