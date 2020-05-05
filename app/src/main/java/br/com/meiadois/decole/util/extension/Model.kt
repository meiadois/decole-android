package br.com.meiadois.decole.util.extension

import br.com.meiadois.decole.data.localdb.entity.User
import br.com.meiadois.decole.data.network.response.UserDTO

fun UserDTO.parseEntity() = User(this.jwt, this.name, this.email, this.introduced)