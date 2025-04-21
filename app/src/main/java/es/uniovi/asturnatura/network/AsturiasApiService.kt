package es.uniovi.asturnatura.network


import es.uniovi.asturnatura.model.ApiResponse
import retrofit2.http.GET

interface AsturiasApiService {
    @GET("documents/portlet_file_entry/39908/espacios-naturales.json/580094f8-4608-d911-f4de-da4c152073e7?download=true")
    suspend fun getEspaciosNaturales(): ApiResponse

}
