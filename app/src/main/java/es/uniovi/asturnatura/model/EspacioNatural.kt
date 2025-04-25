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
    val imagen: ImagenContent?,

    @Json(name = "Geolocalizacion")
    val geolocalizacion: GeolocalizacionContent?,

    @Json(name = "Contacto")
    val contacto: ContactoContent?,

    @Json(name = "Observaciones")
    val observaciones: ObservacionesContent?,

    @Json(name = "RedesSociales")
    val redesSociales: RedesSocialesContent?,

    @Json(name = "Visualizador")
    val visualizador: VisualizadorContent?
)

@JsonClass(generateAdapter = true)
data class NombreContent(val content: String?)

@JsonClass(generateAdapter = true)
data class MunicipioContent(val content: String?)

@JsonClass(generateAdapter = true)
data class HtmlContent(val content: String?)

@JsonClass(generateAdapter = true)
data class InformacionContent(
    @Json(name = "InformacionTitulo")
    val titulo: HtmlContent?,
    @Json(name = "Localizacion")
    val localizacion: HtmlContent?,
    @Json(name = "QueVer")
    val queVer: HtmlContent?,
    @Json(name = "Flora")
    val flora: HtmlContent?,
    @Json(name = "Fauna")
    val fauna: HtmlContent?
)

@JsonClass(generateAdapter = true)
data class GeolocalizacionContent(
    @Json(name = "Coordenadas")
    val coordenadas: HtmlContent?
)

@JsonClass(generateAdapter = true)
data class ContactoContent(
    @Json(name = "Concejo")
    val concejo: HtmlContent?,
    @Json(name = "Zona")
    val zona: HtmlContent?,
    @Json(name = "AltitudMaxima")
    val altitudMaxima: HtmlContent?
)

@JsonClass(generateAdapter = true)
data class ObservacionesContent(
    @Json(name = "Observacion")
    val observacion: HtmlContent?
)

@JsonClass(generateAdapter = true)
data class RedesSocialesContent(
    @Json(name = "Facebook")
    val facebook: SocialItem?,
    @Json(name = "Instagram")
    val instagram: SocialItem?,
    @Json(name = "Twitter")
    val twitter: SocialItem?
)

@JsonClass(generateAdapter = true)
data class SocialItem(
    val title: String?
)

@JsonClass(generateAdapter = true)
data class ImagenContent(
    val content: ImagenValue?
)

@JsonClass(generateAdapter = true)
data class ImagenValue(
    val value: String?
)



