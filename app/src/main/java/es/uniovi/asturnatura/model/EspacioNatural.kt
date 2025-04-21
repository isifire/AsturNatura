package es.uniovi.asturnatura.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EspacioNatural(
    @Json(name = "Nombre")
    val nombre: NombreContent?,

    @Json(name = "Informacion")
    val informacion: InformacionContent?,

    @Json(name = "Municipio")
    val municipio: MunicipioContent?,

    @Json(name = "Imagen")
    val imagen: ImagenContent?
)


@JsonClass(generateAdapter = true)
data class NombreContent(
    val content: String?
)

@JsonClass(generateAdapter = true)
data class MunicipioContent(
    val content: String?
)

@JsonClass(generateAdapter = true)
data class ImagenContent(
    val content: String?
)

@JsonClass(generateAdapter = true)
data class InformacionContent(
    @Json(name = "InformacionTitulo")
    val titulo: TituloContent?
)

@JsonClass(generateAdapter = true)
data class TituloContent(
    val content: String?
)
