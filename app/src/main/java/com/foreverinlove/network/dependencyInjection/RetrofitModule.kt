package com.foreverinlove.network.dependencyInjection

import    android.annotation.SuppressLint
import com.foreverinlove.network.model.ApiServiceClass
import com.foreverinlove.network.repository.DefaultMainRepository
import com.foreverinlove.network.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.*

private const val BASE_URL =
    //"https://ikbautomation.com/laravel/for_ever_us_in_love/public/api/"
    //"https://gurutechnolabs.co.in/website/laravel/foreverus_in_love/public/api/"
  //  "https://app-backend.foreverusinlove.com/public/api/"
    "https://app-backend.foreverusinlove.com/public/api/"

//bethebethu
@Module
@SuppressLint("TrustAllX509TrustManager", "CustomX509TrustManager")
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    // lazy first time object call thay
    private val httpClient: OkHttpClient by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

        OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
    fun provideKanyeWestApi(): ApiServiceClass = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()
        .create(ApiServiceClass::class.java)

    @Singleton
    @Provides
    fun provideRepository(api: ApiServiceClass): MainRepository =
        DefaultMainRepository(api)

}