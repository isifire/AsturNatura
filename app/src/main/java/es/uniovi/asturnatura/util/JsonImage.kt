package es.uniovi.asturnatura.util

import android.util.Log
import com.squareup.moshi.Moshi
import es.uniovi.asturnatura.model.ImagenInfo
import org.json.JSONObject

object JsonImage {
    fun construirUrlImagen(infoJson: String?): String? {
        Log.d("Imagen", "Llamando a construirUrlImagen con: $infoJson")
        if (infoJson.isNullOrBlank()) return null
        return try {
            val adapter = Moshi.Builder().build().adapter(ImagenInfo::class.java)
            val info = adapter.fromJson(infoJson)
            if (info != null) {
                Log.d("Imagen" ,"https://www.turismoasturiasprofesional.es/documents/${info.groupId}/${info.uuid}")
                "https://www.turismoasturiasprofesional.es/documents/${info.groupId}/${info.uuid}"

            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun extraerUrlYoutube(infoJson: String): String? {
        return try {
            val json = JSONObject(infoJson)
            val url = json.optString("content", "")
            if (url.contains("youtube.com")) url else null
        } catch (e: Exception) {
            null
        }
    }

}
