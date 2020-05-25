package br.com.meiadois.decole.data.model

class Company() {
    var id: Int = 0
    var name: String = ""
    var cep: String = ""
    var thumbnail: String = ""
    var banner: String = ""
    var cnpj: String = ""
    var cellphone: String = ""
    var email: String = ""
    var description: String = ""
    var visible: Boolean = false
    var city: String? = ""
    var neighborhood: String? = ""
    var segment: Segment? = null

    constructor(
        id: Int,
        name: String,
        cep: String,
        thumbnail: String,
        banner: String,
        cnpj: String,
        cellphone: String,
        email: String,
        description: String,
        visible: Boolean,
        city: String?,
        neighborhood: String?,
        segment: Segment?
    ) : this(){

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
        this.segment = segment
    }
}


