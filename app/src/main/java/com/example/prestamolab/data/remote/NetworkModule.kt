package com.example.prestamolab.data.remote

import com.example.prestamolab.BuildConfig
import com.example.prestamolab.data.security.TokenStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente HTTP (Semana 8). Usa exclusivamente HTTPS (nunca cleartext),
 * timeouts controlados y un interceptor que añade el token conceptual
 * cifrado sin exponer secretos en texto plano.
 */
object NetworkModule {

    fun crearClienteHttp(tokenStore: TokenStore): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer ${tokenStore.obtenerToken()}")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()
    }

    fun crearApi(tokenStore: TokenStore, baseUrl: String = BuildConfig.API_BASE_URL): PrestamoApiService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(crearClienteHttp(tokenStore))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrestamoApiService::class.java)
}