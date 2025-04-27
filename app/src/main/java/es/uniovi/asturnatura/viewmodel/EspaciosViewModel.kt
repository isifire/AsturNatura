package es.uniovi.asturnatura.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.*
import es.uniovi.asturnatura.data.repository.EspaciosRepository
import es.uniovi.asturnatura.model.EspacioNatural
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.network.RetrofitInstance
import es.uniovi.asturnatura.util.JsonImage
import kotlinx.coroutines.launch

class EspaciosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EspaciosRepository(application)

    private val _espaciosLocales = MutableLiveData<List<EspacioNaturalEntity>>()
    val espaciosLocales: LiveData<List<EspacioNaturalEntity>> get() = _espaciosLocales

    private val _espaciosRemotos = MutableLiveData<List<EspacioNatural>>()
    val espaciosRemotos: LiveData<List<EspacioNatural>> get() = _espaciosRemotos

    /** Cargar espacios naturales desde la API (Remoto) */
    fun cargarEspaciosDesdeApi() {
        viewModelScope.launch {
            try {
                val respuesta = RetrofitInstance.api.getEspaciosNaturales()
                _espaciosRemotos.value = respuesta.articles.article
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** Cargar espacios naturales desde la base de datos local */
    fun cargarEspaciosLocales() {
        viewModelScope.launch {
            _espaciosLocales.value = repository.getEspaciosNaturales()
        }
    }

    /** Buscar espacios en local */
    fun buscarEspaciosLocales(query: String) {
        viewModelScope.launch {
            _espaciosLocales.value = repository.searchEspacios(query)
        }
    }

    /** Guardar espacios descargados en la base de datos local */
    fun guardarEspaciosLocalmente(lista: List<EspacioNatural>) {
        viewModelScope.launch {
            val entidades = lista.map { espacio ->
                EspacioNaturalEntity(
                    id = espacio.nombre?.content ?: "sin-id-${System.currentTimeMillis()}",
                    nombre = espacio.nombre?.content ?: "Sin nombre",
                    descripcion = espacio.informacion?.titulo?.content ?: "Sin descripción",
                    ubicacion = espacio.informacion?.localizacion?.content ?: "Ubicación no disponible",
                    tipo = "Espacio Natural",
                    imagen = espacio.imagen?.content?.value,
                    municipio = espacio.contacto?.concejo?.content ?: "Municipio desconocido",
                    zona = espacio.contacto?.zona?.content ?: "Zona desconocida",
                    coordenadas = espacio.geolocalizacion?.coordenadas?.content ?: "Coordenadas no disponibles",
                    flora = espacio.informacion?.flora?.content,
                    fauna = espacio.informacion?.fauna?.content,
                    queVer = espacio.informacion?.queVer?.content,
                    altitud = espacio.contacto?.altitudMaxima?.content,
                    observaciones = espacio.observaciones?.observacion?.content,
                    facebook = espacio.redesSociales?.facebook?.title,
                    instagram = espacio.redesSociales?.instagram?.title,
                    twitter = espacio.redesSociales?.twitter?.title,
                    imagenes = espacio.visualizador?.slide
                        ?.mapNotNull { JsonImage.construirUrlImagen(it.value) }
                        ?.joinToString("|") ?: "",
                    youtubeUrl = espacio.video?.content
                )
            }
            repository.insertarEspacios(entidades)
        }
    }

    /** Carga inteligente: primero local, si no hay, remoto */
    fun cargarDatosInteligente(context: Context) {
        viewModelScope.launch {
            var espacios = repository.getEspaciosNaturales()
            if (espacios.isEmpty() && isOnline(context)) {
                try {
                    val respuesta = RetrofitInstance.api.getEspaciosNaturales()
                    guardarEspaciosLocalmente(respuesta.articles.article)
                    espacios = repository.getEspaciosNaturales()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            _espaciosLocales.value = espacios
        }
    }

    /** Comprobación rápida de conexión a Internet */
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
    }
}
