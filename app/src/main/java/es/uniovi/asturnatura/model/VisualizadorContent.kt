package es.uniovi.asturnatura.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VisualizadorContent(
    @Json(name = "Slide")
    val slide: List<SlideContent>?
)

@JsonClass(generateAdapter = true)
data class SlideContent(
    @Json(name = "SlideUrl")
    val slideUrl: TitleContent?,
    @Json(name = "SlideTitulo")
    val slideTitulo: SlideTituloContent?,
    val title: String?,
    val value: String?
)

@JsonClass(generateAdapter = true)
data class SlideTituloContent(
    val title: String?,
    val content: String?
)

@JsonClass(generateAdapter = true)
data class TitleContent(
    val title: String?,
    val content: String?
)
