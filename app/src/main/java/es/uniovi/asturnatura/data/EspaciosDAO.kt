package es.uniovi.asturnatura.data

import androidx.room.*
import es.uniovi.asturnatura.model.EspacioNaturalEntity

@Dao
interface EspaciosDAO {

    @Query("SELECT * FROM espacios_naturales")
    suspend fun getAll(): List<EspacioNaturalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(espacios: List<EspacioNaturalEntity>)

    @Query("SELECT * FROM espacios_naturales WHERE nombre LIKE '%' || :query || '%'")
    suspend fun searchByName(query: String): List<EspacioNaturalEntity>
}
