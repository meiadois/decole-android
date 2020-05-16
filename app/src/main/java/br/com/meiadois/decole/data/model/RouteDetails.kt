package br.com.meiadois.decole.data.model

import androidx.room.Embedded
import androidx.room.Relation
import br.com.meiadois.decole.data.localdb.entity.Lesson
import br.com.meiadois.decole.data.localdb.entity.Route

data class RouteDetails(
    @Embedded val route: Route,
    @Relation(
        parentColumn = "id",
        entityColumn = "routeId"
    ) val lessons: List<Lesson>
)