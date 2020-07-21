package br.com.meiadois.decole.data.network

import br.com.meiadois.decole.data.localdb.AppDatabase
import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor(private val db: AppDatabase) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request()
            .newBuilder()
            .addHeader(
                "x-access-token",
                db.getUserDao().findJWT() ?: ""
            )
            .build()
        return chain.proceed(newRequest)
    }
}