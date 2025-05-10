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

class EspaciosViewModel : AndroidViewModel {

    private val repository: EspaciosRepository
    val favoritos: LiveData<List<EspacioNaturalEntity>>

    // Constructor para la app real
    constructor(application: Application) : super(application) {
        this.repository = EspaciosRepository(application)
        this.favoritos = repository.favoritos
    }

    // Constructor para test
    constructor(application: Application, repository: EspaciosRepository) : super(application) {
        this.repository = repository
        this.favoritos = repository.favoritos
    }

    private val _espaciosLocales = MutableLiveData<List<EspacioNaturalEntity>>()
    val espaciosLocales: LiveData<List<EspacioNaturalEntity>> get() = _espaciosLocales

    private val _espaciosRemotos = MutableLiveData<List<EspacioNatural>>()
    val espaciosRemotos: LiveData<List<EspacioNatural>> get() = _espaciosRemotos

    private val espaciosOriginales = MutableLiveData<List<EspacioNaturalEntity>>()
    private val allEspacios = MutableLiveData<List<EspacioNaturalEntity>>()
    private val filtroTexto = MutableLiveData<String>()
    private val filtroCategorias = MutableLiveData<Set<String>>(emptySet())

    private var textoBusquedaActual: String = ""

    private val espaciosFiltrados_ = MediatorLiveData<List<EspacioNaturalEntity>>()
    val espaciosFiltrados: LiveData<List<EspacioNaturalEntity>> get() = espaciosFiltrados_

    init {
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
            //Log.d("Debug", "Datos cargados: ${espacios.size}")
            _espaciosLocales.value = espacios
            espaciosOriginales.value = espacios
            allEspacios.value = espacios
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    fun actualizarTextoBusqueda(texto: String) {
        textoBusquedaActual = texto
        viewModelScope.launch {
            val resultado = if (texto.isNotBlank()) {
                repository.searchEspacios(texto)
            } else {
                espaciosOriginales.value ?: repository.getEspaciosNaturales()
            }
            allEspacios.value = resultado
            //Log.d("Search", "Búsqueda ejecutada: ${resultado.size} resultados para '$texto'")
        }
    }

    fun actualizarFiltroCategorias(nuevas: Set<String>) {
        filtroCategorias.value = nuevas
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

        //Log.d("Filtro", "Filtrados: ${resultado.size} elementos con '$texto' y ${categorias.size} filtros")
        espaciosFiltrados_.value = resultado
    }

    fun categoriaMatches(espacio: EspacioNaturalEntity, filtro: String): Boolean {
        val nombre = espacio.nombre.lowercase()
        val descripcion = espacio.descripcion.lowercase()
        val tipo = espacio.tipo.lowercase()
        return when (filtro.lowercase()) {
            "playa" -> Regex("playa", RegexOption.IGNORE_CASE).containsMatchIn(nombre)
            "parque" -> Regex("parque", RegexOption.IGNORE_CASE).containsMatchIn(nombre)
            "área recreativa" -> Regex("área\\s+recreativa", RegexOption.IGNORE_CASE).containsMatchIn(nombre)
                    || Regex("área\\s+recreativa", RegexOption.IGNORE_CASE).containsMatchIn(descripcion)
                    || Regex("área\\s+recreativa", RegexOption.IGNORE_CASE).containsMatchIn(tipo)
            "picos" -> !espacio.altitud.isNullOrBlank()
            "lago" -> Regex("lago", RegexOption.IGNORE_CASE).containsMatchIn(nombre)
            "río" -> Regex("río|rio", RegexOption.IGNORE_CASE).containsMatchIn(nombre)
            else -> true
        }
    }

    fun getEspacioById(id: String): LiveData<EspacioNaturalEntity?> {
        val resultado = MutableLiveData<EspacioNaturalEntity?>()
        resultado.value = allEspacios.value?.firstOrNull { it.id == id }
        return resultado

    }

    fun toggleFavorito(espacio: EspacioNaturalEntity) {
        viewModelScope.launch {
            val nuevoValor = !espacio.esFavorito
            espacio.esFavorito = nuevoValor
            repository.actualizarFavorito(espacio.id, nuevoValor)
            actualizarTextoBusqueda(textoBusquedaActual)
        }
    }

    private val _usarMapaOscuro = MutableLiveData<Boolean>()
    val usarMapaOscuro: LiveData<Boolean> = _usarMapaOscuro

    fun cargarPreferencias(context: Context) {
        val nightMode = android.preference.PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean("night_mode", false)
        _usarMapaOscuro.value = nightMode
    }

    fun setEspaciosOriginalesTest(lista: List<EspacioNaturalEntity>) {
        espaciosOriginales.value = lista
        allEspacios.value = lista
    }

}
