package es.uniovi.asturnatura

import es.uniovi.asturnatura.model.EspacioNaturalEntity
import es.uniovi.asturnatura.ui.EspaciosAdapter
import org.junit.Assert.*
import org.junit.Test

class EspaciosAdapterTest {

    private fun crear(nombre: String, tipo: String): EspacioNaturalEntity {
        return EspacioNaturalEntity(
            id = nombre,
            nombre = nombre,
            tipo = tipo,
            municipio = "test",
            descripcion = "desc",
            imagen = "",
            esFavorito = false,
            ubicacion = "",
            zona = "",
            coordenadas = "",
            flora = "",
            fauna = "",
            queVer = "",
            altitud = "",
            observaciones = "",
            facebook = "",
            instagram = "",
            twitter = "",
            imagenes = "",
            youtubeUrl = ""
        )
    }

    @Test
    fun testAdapterCount() {
        val adapter = EspaciosAdapter(listOf(crear("Playa X", "playa")), {}, {})
        assertEquals(1, adapter.itemCount)
    }

    @Test
    fun testFiltradoManual() {
        val lista = listOf(crear("Playa A", "playa"), crear("Monte B", "monte"))
        val filtrados = lista.filter { it.tipo == "playa" }
        assertEquals(1, filtrados.size)
        assertEquals("Playa A", filtrados[0].nombre)
    }
}
