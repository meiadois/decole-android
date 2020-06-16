package br.com.meiadois.decole.util.extension

import br.com.meiadois.decole.data.localdb.entity.Lesson
import br.com.meiadois.decole.data.localdb.entity.Segment as SegmentEntity
import br.com.meiadois.decole.data.localdb.entity.Company as CompanyEntity
import br.com.meiadois.decole.data.localdb.entity.Account as AccountEntity
import br.com.meiadois.decole.data.localdb.entity.MyCompany
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.localdb.entity.User
import br.com.meiadois.decole.data.model.*
import br.com.meiadois.decole.data.network.response.*
import br.com.meiadois.decole.presentation.user.account.binding.CompanyAccountData
import br.com.meiadois.decole.presentation.user.account.binding.ImageData
import br.com.meiadois.decole.presentation.user.account.binding.UserAccountData
import br.com.meiadois.decole.presentation.user.account.binding.UserSocialNetwork
import br.com.meiadois.decole.presentation.user.education.binding.LessonItem
import br.com.meiadois.decole.presentation.user.education.binding.RouteItem

fun UserDTO.parseToUserEntity() = User(this.jwt, this.name, this.email, this.introduced)

fun UserAccountData.parseToUserEntity() = User(jwt, name, email, introduced)

fun User.parseToUserAccountData() = UserAccountData(name, email, jwt, introduced)

fun RouteDTO.parseEntity() = Route(
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

fun MetricsResponse.toMetricModel() = Metrics(this.sucess, this.error_message, this.value)

fun List<SegmentResponse>.toSegmentEntityList(): List<SegmentEntity> {
    return this.map {
        it.toSegmentEntity()
    }
}

fun Segment.toSegmentEntity() = SegmentEntity(id!!, name)

fun SegmentResponse.toSegmentEntity() = SegmentEntity(id!!, name)

fun SegmentEntity.toSegmentModel(): Segment = Segment(id, name)

fun List<SegmentEntity>.parseToSegmentModelList(): List<Segment> {
    return this.map {
        it.toSegmentModel()
    }
}

fun CompanyResponse.toCompanyModel(): Company {
    return Company(
        id,
        name,
        cep,
        thumbnail,
        banner,
        cnpj,
        cellphone,
        email,
        description,
        visible,
        city,
        neighborhood,
        segment?.toSegmentModel()
    )
}

fun Company.toCompanyEntity(): CompanyEntity {
    return CompanyEntity(
        id,
        name,
        thumbnail,
        cep,
        banner,
        cnpj,
        cellphone,
        email,
        description,
        visible,
        city,
        neighborhood,
        segment?.id ?: 0
    )
}

fun CompanyResponse.toCompanyEntity(): CompanyEntity {
    return CompanyEntity(
        id,
        name,
        thumbnail,
        cep,
        banner,
        cnpj,
        cellphone,
        email,
        description,
        visible,
        city,
        neighborhood,
        segment?.id ?: 0
    )
}

fun CompanyResponse.toMyCompany(): MyCompany {
    return MyCompany(
        this.toCompanyEntity(),
        this.segment?.toSegmentEntity()
    )
}

fun MyCompany.toCompanyAccountData(): CompanyAccountData {
    return CompanyAccountData(
        company.id,
        company.name,
        company.cep,
        ImageData(path = company.thumbnail),
        ImageData(path = company.banner),
        company.cnpj,
        company.cellphone,
        company.email,
        company.description,
        company.visible,
        company.city!!,
        company.neighborhood!!,
        segment?.id ?: -1,
        segment?.name
    )
}

fun MyCompany.toCompanyModel(): Company {
    return Company(
        company.id,
        company.name,
        company.cep,
        company.thumbnail,
        company.banner,
        company.cnpj,
        company.cellphone,
        company.email,
        company.description,
        company.visible,
        company.city,
        company.neighborhood,
        segment?.toSegmentModel()
    )
}

fun CompanySearchResponse.toCompanySearchModel(): Company {
    return Company(
        id = id,
        name = name,
        banner = banner,
        cnpj = cnpj,
        cellphone = cellphone,
        email = email,
        description = description,
        segment = segment?.toSegmentModel()
    )
}

fun AnalyticsResponse.toAnalytics(): Analytics {
    return Analytics(
        mean_of_hashtags.toMetricModel(),
        mean_of_mentions.toMetricModel(),
        mean_of_comments.toMetricModel(),
        mean_of_likes.toMetricModel(),
        posts_with_hashtags.toMetricModel(),
        followers_per_following.toMetricModel(),
        followers.toMetricModel(),
        following.toMetricModel(),
        publications.toMetricModel()
    )
}

fun List<CompanySearchResponse>.toCompanySearchModelList(): List<Company> {
    if (this.isNotEmpty())
        return this.map {
            it.toCompanySearchModel()
        }
    return listOf()
}

fun List<LikeResponse>.toLikeModelList(userCompanyId: Int): List<Like> {
    return this.map {
        val userCompany: CompanyResponse
        val partnerCompany: CompanyResponse
        val isSender: Boolean

        if (it.sender_company.id == userCompanyId) {
            partnerCompany = it.recipient_company
            userCompany = it.sender_company
            isSender = true
        } else {
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

fun AccountResponse.toAccountEntity(): AccountEntity =
    AccountEntity(id, username, channel?.name ?: "", channel?.category ?: "")

fun List<AccountResponse>.parseToAccountEntityList(): List<AccountEntity> {
    return this.map {
        it.toAccountEntity()
    }
}

fun AccountResponse.toAccountModel(): Account =
    Account(id, username, channel?.name ?: "", channel?.category ?: "")

fun List<AccountResponse>.parseToAccountModelList(): List<Account> {
    return this.map {
        it.toAccountModel()
    }
}

fun AccountEntity.toAccountModel(): Account = Account(id, userName, channelName, channelCategory)

fun List<AccountEntity>.toAccountModelList(): List<Account> {
    return this.map {
        it.toAccountModel()
    }
}

fun Account.toAccountEntity(): AccountEntity = AccountEntity(id, userName, channelName, channelCategory)

fun List<Account>.toAccountEntityList(): List<AccountEntity> {
    return this.map {
        it.toAccountEntity()
    }
}

fun UserSocialNetwork.toAccountModel(): Account = Account(id, userName, channelName, category)