package es.uniovi.asturnatura.network

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    private val moshi = Moshi.Builder().build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://orion.edv.uniovi.es/~arias/json/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val api: AsturiasApiService by lazy {
        retrofit.create(AsturiasApiService::class.java)
    }
}
