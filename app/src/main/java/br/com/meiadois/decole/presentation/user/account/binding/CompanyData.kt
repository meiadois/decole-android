package br.com.meiadois.decole.presentation.user.account.binding

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import br.com.meiadois.decole.BR

class CompanyData() : BaseObservable() {
    var id: Int = -1
    var name: String = ""
    var cep: String = ""
    var thumbnail: ImageData = ImageData()
    var banner: ImageData = ImageData()
    var cnpj: String = ""
    var cellphone: String = ""
    var email: String = ""
    var description: String = ""
    var visible: Boolean = false
    var segmentId: Int = -1
    var segmentName: String? = ""

    @Bindable
    var city: String = ""

    fun setCityAndNotify(value: String) {
        city = value
        notifyPropertyChanged(BR.city)
    }

    @Bindable
    var neighborhood: String = ""

    fun setNeighborhoodAndNotify(value: String) {
        neighborhood = value
        notifyPropertyChanged(BR.neighborhood)
    }

    constructor(
        id: Int = -1,
        name: String = "",
        cep: String = "",
        thumbnail: ImageData = ImageData(),
        banner: ImageData = ImageData(),
        cnpj: String = "",
        cellphone: String = "",
        email: String = "",
        description: String = "",
        visible: Boolean = false,
        city: String = "",
        neighborhood: String = "",
        segmentId: Int = -1,
        segmentName: String? = ""
    ) : this() {
        this.id = id
        this.name = name
        this.cep = cep
        this.thumbnail = thumbnail
        this.banner = banner
        this.cnpj = cnpj
        this.cellphone = cellphone
        this.email = email
        this.description = description
        this.visible = visible
        this.city = city
        this.neighborhood = neighborhood
        this.segmentId = segmentId
        this.segmentName = segmentName
    }
}

class ImageData(
    var path: String = "",
    var type: String = "",
    var updated: Boolean = false
) : BaseObservable()