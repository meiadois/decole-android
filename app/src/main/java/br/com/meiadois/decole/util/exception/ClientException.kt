package br.com.meiadois.decole.util.exception

import java.io.IOException

class ClientException(val code: Int,
                      message: String) : IOException(message)