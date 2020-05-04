package br.com.meiadois.decole.data.http.client

import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

abstract class RequestHandler {
    suspend fun <T : Any> handle(call: suspend () -> Response<T>): T {
        val response = call.invoke()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val error = response.errorBody()?.string()

            var message = "Não foi possivel completar a operação"
            error?.let {
                try {
                    message = JSONObject(it).getString("message")
                } catch (e: JSONException) {
                }
            }

            throw ClientException(response.code(), message)
        }
    }

}