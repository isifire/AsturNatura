package es.uniovi.asturnatura.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "espacios_naturales")
data class EspacioNaturalEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val tipo: String,
    val imagen: String?, // Imagen principal (opcional)

    // Campos adicionales
    val municipio: String?,
    val zona: String?,
    val coordenadas: String?,
    val flora: String?,
    val fauna: String?,
    val queVer: String?,
    val altitud: String?,
    val observaciones: String?,
    val facebook: String?,
    val instagram: String?,
    val twitter: String?,

    // 🆕 Galería de imágenes (JSONs en cadena separados por |)
    val imagenes: String?
)
