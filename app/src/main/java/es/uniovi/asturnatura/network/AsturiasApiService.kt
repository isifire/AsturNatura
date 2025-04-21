package es.uniovi.asturnatura.network


import es.uniovi.asturnatura.model.ApiResponse
import retrofit2.http.GET

interface AsturiasApiService {
    @GET("EspaciosNaturales.json")
    suspend fun getEspaciosNaturales(): ApiResponse

}
