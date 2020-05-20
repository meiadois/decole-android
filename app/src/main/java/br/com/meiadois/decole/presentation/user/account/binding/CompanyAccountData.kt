package br.com.meiadois.decole.presentation.user.account.binding

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

data class CompanyAccountData(
    var id: Int = -1,
    @Bindable
    var name: String = "",
    @Bindable
    var cep: String = "",
    var thumbnail: String = "",
    var banner: String = "",
    @Bindable
    var cnpj: String = "",
    @Bindable
    var cellphone: String = "",
    @Bindable
    var email: String = "",
    @Bindable
    var description: String = "",
    @Bindable
    var visible: Boolean = false,
    @Bindable
    var city: String?,
    @Bindable
    var neighborhood: String?,
    var segmentId: Int = -1,
    @Bindable
    var segmentName: String? = ""
) : BaseObservable()