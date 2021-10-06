package uz.juo.hobee.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    var BASE_URL = "http://83.69.136.138:3000/api/"

    fun getRetrofit(): Retrofit {
        var httpInterceptor = HttpLoggingInterceptor()
        httpInterceptor.level = HttpLoggingInterceptor.Level.BODY
        var okkHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpInterceptor)
//            .connectTimeout(5, TimeUnit.MINUTES) // connect timeout
//            .writeTimeout(5, TimeUnit.MINUTES) // write timeout
//            .readTimeout(5, TimeUnit.MINUTES) // read timeout
            .build()

        return Retrofit.Builder().baseUrl(BASE_URL)
            .client(okkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .build()

    }

    var apiService = getRetrofit().create(ApiService::class.java)
}