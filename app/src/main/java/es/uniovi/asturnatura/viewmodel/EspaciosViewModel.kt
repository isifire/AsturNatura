package es.uniovi.asturnatura.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
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

    private val allEspacios = MutableLiveData<List<EspacioNaturalEntity>>()
    private val filtroTexto = MutableLiveData<String>()
    private val filtroCategorias = MutableLiveData<Set<String>>()

    private val espaciosFiltrados_ = MediatorLiveData<List<EspacioNaturalEntity>>()
    val espaciosFiltrados: LiveData<List<EspacioNaturalEntity>> get() = espaciosFiltrados_

    init {
        filtroCategorias.value = emptySet()
        espaciosFiltrados_.addSource(allEspacios) { filtrarDatos() }
        espaciosFiltrados_.addSource(filtroTexto) { filtrarDatos() }
        espaciosFiltrados_.addSource(filtroCategorias) { filtrarDatos() }
    }

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

    fun cargarEspaciosLocales() {
        viewModelScope.launch {
            _espaciosLocales.value = repository.getEspaciosNaturales()
        }
    }

    fun buscarEspaciosLocales(query: String) {
        viewModelScope.launch {
            _espaciosLocales.value = repository.searchEspacios(query)
        }
    }

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
            Log.d("Debug", "Datos cargados: ${espacios.size}")
            _espaciosLocales.value = espacios
            allEspacios.value = espacios
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    private fun filtrarDatos() {
        val texto = filtroTexto.value?.lowercase() ?: ""
        val categorias = filtroCategorias.value ?: emptySet()
        val original = allEspacios.value ?: emptyList()

        val resultado = original.filter { espacio ->
            val nombre = espacio.nombre.lowercase()
            val coincideTexto = texto.isEmpty() || nombre.contains(texto)
            val coincideCategoria = categorias.isEmpty() || categorias.any { tipo -> categoriaMatches(espacio, tipo) }
            coincideTexto && coincideCategoria
        }

        Log.d("Filtro", "Filtrados: ${resultado.size} elementos con '$texto' y ${categorias.size} filtros")
        espaciosFiltrados_.value = resultado
    }

    private fun categoriaMatches(espacio: EspacioNaturalEntity, filtro: String): Boolean {
        val nombre = espacio.nombre.lowercase()
        return when (filtro) {
            "Playa" -> Regex("playa", RegexOption.IGNORE_CASE).containsMatchIn(nombre)
            "Parque" -> Regex("parque", RegexOption.IGNORE_CASE).containsMatchIn(nombre)
            "Área Recreativa" -> Regex("área\\s+recreativa", RegexOption.IGNORE_CASE).containsMatchIn(nombre)
            "Picos" -> !espacio.altitud.isNullOrBlank()
            "Lago" -> Regex("lago", RegexOption.IGNORE_CASE).containsMatchIn(nombre)
            "Río" -> Regex("río|rio", RegexOption.IGNORE_CASE).containsMatchIn(nombre)
            else -> true
        }
    }

    fun actualizarTextoBusqueda(texto: String) {
        filtroTexto.value = texto
    }

    fun actualizarFiltroCategorias(nuevas: Set<String>) {
        filtroCategorias.value = nuevas
    }
}