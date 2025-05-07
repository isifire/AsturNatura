package es.uniovi.asturnatura.data.repository

import android.content.Context
import android.text.Html
import android.util.Log
import androidx.room.Room
import es.uniovi.asturnatura.data.AppDatabase
import es.uniovi.asturnatura.model.EspacioNatural
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.network.RetrofitInstance
import es.uniovi.asturnatura.util.JsonImage
import es.uniovi.asturnatura.util.JsonImage.construirUrlImagen


class EspaciosRepository(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "asturnatura-db"
    ).build()

    private val dao = db.espaciosDao()

    // Obtiene los espacios naturales desde la base de datos o desde la API si no hay datos
    suspend fun getEspaciosNaturales(forceRefresh: Boolean = false): List<EspacioNaturalEntity> {
        val local = dao.getAll()
        return if (local.isEmpty() || forceRefresh) {

            val remote = RetrofitInstance.api.getEspaciosNaturales().articles.article.map {
                EspacioNaturalEntity(
                    id = it.nombre?.content ?: "sin-id-${System.currentTimeMillis()}",
                    nombre = it.nombre?.content ?: "Sin nombre",
                    descripcion = limpiarHtml(it.informacion?.titulo?.content),
                    ubicacion = limpiarHtml(it.informacion?.localizacion?.content),
                    tipo = "Espacio Natural",
                    imagen = obtenerPrimeraImagenVisualizador(it),
                    municipio = limpiarHtml(it.contacto?.concejo?.content),
                    zona = limpiarHtml(it.contacto?.zona?.content),
                    coordenadas = limpiarHtml(it.geolocalizacion?.coordenadas?.content),
                    flora = limpiarHtml(it.informacion?.flora?.content),
                    fauna = limpiarHtml(it.informacion?.fauna?.content),
                    queVer = limpiarHtml(it.informacion?.queVer?.content),
                    altitud = limpiarHtml(it.contacto?.altitudMaxima?.content),
                    observaciones = limpiarHtml(it.observaciones?.observacion?.content),
                    facebook = it.redesSociales?.facebook?.title,
                    instagram = it.redesSociales?.instagram?.title,
                    twitter = it.redesSociales?.twitter?.title,
                    imagenes = it.visualizador?.slide
                        ?.mapNotNull { slide -> JsonImage.construirUrlImagen(slide.value) }
                        ?.joinToString("|") ?: "",
                    youtubeUrl = obtenerUrlVideo(it)



                )
            }
            dao.insertAll(remote)
            remote
        } else {
            local
        }


    }

    suspend fun getEspacioById(id: String): EspacioNaturalEntity? {
        return dao.getById(id)
    }

    suspend fun searchEspacios(query: String): List<EspacioNaturalEntity> {
        return dao.searchByText(query)
    }

    suspend fun insertarEspacios(lista: List<EspacioNaturalEntity>) {
        dao.insertAll(lista)
    }
}

@Suppress("DEPRECATION")
fun limpiarHtml(html: String?): String {
    return if (!html.isNullOrBlank()) {
        Html.fromHtml(html).toString().trim()
    } else {
        ""
    }
}

private fun obtenerPrimeraImagenVisualizador(espacio: EspacioNatural): String? {
    val primeraSlideConImagen = espacio.visualizador?.slide?.firstOrNull { !it.value.isNullOrBlank() }
    return primeraSlideConImagen?.value?.let { JsonImage.construirUrlImagen(it) }
}

private fun obtenerUrlVideo(espacio: EspacioNatural): String? {
    espacio.visualizador?.slide?.forEachIndexed { index, slide ->
        Log.d("VideoDebug", "Slide[$index] SlideUrl: ${slide.slideUrl?.content}")
    }

    return espacio.visualizador?.slide
        ?.firstOrNull { it.slideUrl?.content?.contains("youtube.com") == true }
        ?.slideUrl?.content
}



