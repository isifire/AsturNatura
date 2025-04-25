package es.uniovi.asturnatura.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImagenInfo(
    val classPK: Long,
    val groupId: String,
    val title: String,
    val type: String,
    val uuid: String
)
