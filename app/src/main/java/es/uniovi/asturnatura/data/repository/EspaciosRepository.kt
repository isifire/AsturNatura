package es.uniovi.asturnatura.data.repository

import android.content.Context
import android.text.Html
import android.util.Log
import androidx.room.Room
import es.uniovi.asturnatura.data.AppDatabase
import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.network.RetrofitInstance

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
                val ubicacionHtml = it.informacion?.localizacion?.content
                Log.d("Repo", "Localización bruta: $ubicacionHtml")

                EspacioNaturalEntity(
                    id = it.nombre?.content ?: "sin-id-${System.currentTimeMillis()}",
                    nombre = it.nombre?.content ?: "Sin nombre",
                    descripcion = it.informacion?.titulo?.content ?: "Sin descripción",
                    ubicacion = limpiarHtml(ubicacionHtml).ifBlank { "Ubicación no disponible" },
                    tipo = "Espacio Natural",
                    imagen = it.imagen?.content
                )
            }
            dao.insertAll(remote)
            remote
        } else {
            local
        }
    }

    // Buscar espacios por nombre (Room)
    suspend fun searchEspacios(query: String): List<EspacioNaturalEntity> {
        return dao.searchByName(query)
    }

    // Insertar lista de espacios en la base de datos
    suspend fun insertarEspacios(lista: List<EspacioNaturalEntity>) {
        dao.insertAll(lista)
    }
}

// Función para limpiar HTML, compatible con API 21+
@Suppress("DEPRECATION")
fun limpiarHtml(html: String?): String {
    return if (!html.isNullOrBlank()) {
        Html.fromHtml(html).toString().trim()
    } else {
        ""
    }
}
