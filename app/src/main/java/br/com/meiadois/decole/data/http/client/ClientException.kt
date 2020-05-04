package br.com.meiadois.decole.data.http.client

class ClientException(val code: Int,
                      message: String) : RuntimeException(message)