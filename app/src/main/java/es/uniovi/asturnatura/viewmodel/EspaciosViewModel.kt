package es.uniovi.asturnatura.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.*
import es.uniovi.asturnatura.model.EspacioNatural
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.network.RetrofitInstance
import es.uniovi.asturnatura.data.repository.EspaciosRepository
import es.uniovi.asturnatura.model.ImagenInfo
import es.uniovi.asturnatura.util.JsonImage
import kotlinx.coroutines.launch

class EspaciosViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = EspaciosRepository(application)

    private val _espacios = MutableLiveData<List<EspacioNatural>>()
    val espacios: LiveData<List<EspacioNatural>> get() = _espacios

    private val _espaciosLocales = MutableLiveData<List<EspacioNaturalEntity>>()
    val espaciosLocales: LiveData<List<EspacioNaturalEntity>> get() = _espaciosLocales

    // Obtener desde la API (sin almacenar)
    fun cargarEspaciosDesdeApi() {
        viewModelScope.launch {
            try {
                val resultado = RetrofitInstance.api.getEspaciosNaturales().articles.article
                _espacios.value = resultado
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Obtener desde la base de datos local
    fun cargarEspaciosLocales() {
        viewModelScope.launch {
            _espaciosLocales.value = repo.getEspaciosNaturales()
        }
    }

    // Buscar en la base de datos
    fun buscarEspacios(query: String) {
        viewModelScope.launch {
            _espaciosLocales.value = repo.searchEspacios(query)
        }
    }

    // Guardar en base de datos local
    fun guardarEspaciosLocalmente(espacios: List<EspacioNatural>) {
        viewModelScope.launch {
            val entidades = espacios.map {
                EspacioNaturalEntity(
                    id = it.nombre?.content ?: "sin-id-${System.currentTimeMillis()}",
                    nombre = it.nombre?.content ?: "Sin nombre",
                    descripcion = it.informacion?.titulo?.content ?: "Sin descripción",
                    ubicacion = it.municipio?.content ?: "Ubicación no disponible",
                    tipo = "Espacio Natural", // Puedes cambiar esto si añades el campo
                    imagen = it.imagen?.content?.value,
                    municipio = it.municipio?.content ?: "Municipio no disponible",
                    zona = it.contacto?.zona?.content ?: "Zona no disponible",
                    coordenadas = it.geolocalizacion?.coordenadas?.content ?: "Coordenadas no disponibles",
                    flora = it.informacion?.flora?.content,
                    fauna = it.informacion?.fauna?.content,
                    queVer = it.informacion?.queVer?.content,
                    altitud = it.contacto?.altitudMaxima?.content,
                    observaciones = it.observaciones?.observacion?.content,
                    facebook = it.redesSociales?.facebook?.title,
                    instagram = it.redesSociales?.instagram?.title,
                    twitter = it.redesSociales?.twitter?.title,
                    imagenes = it.visualizador?.slide
                        ?.mapNotNull { slide -> JsonImage.construirUrlImagen(slide.value) }
                        ?.joinToString("|") ?: "",

                    youtubeUrl = it.video?.content


                )
            }
            repo.insertarEspacios(entidades)
        }
    }

    fun cargarDatosInteligente(context: Context) {
        viewModelScope.launch {
            var locales = repo.getEspaciosNaturales()
            if (locales.isEmpty() && isOnline(context)) {
                try {
                    val desdeApi = RetrofitInstance.api.getEspaciosNaturales().articles.article
                    guardarEspaciosLocalmente(desdeApi)
                    locales = repo.getEspaciosNaturales()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            _espaciosLocales.value = locales
        }
    }

}

@Suppress("DEPRECATION")
private fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
}


