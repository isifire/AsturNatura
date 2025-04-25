package es.uniovi.asturnatura.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse(
    val articles: ArticleWrapper
)

@JsonClass(generateAdapter = true)
data class ArticleWrapper(
    val article: List<EspacioNatural>
)

