package es.uniovi.asturnatura.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "espacios_naturales")
data class EspacioNaturalEntity(
    @PrimaryKey val id: String,  // Cambia si tienes un ID más adecuado
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val tipo: String,
    val imagen: String?
)
