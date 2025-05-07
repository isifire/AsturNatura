package es.uniovi.asturnatura.data

import androidx.room.*
import es.uniovi.asturnatura.model.EspacioNaturalEntity

@Dao
interface EspaciosDAO {

    @Query("SELECT * FROM espacios_naturales")
    suspend fun getAll(): List<EspacioNaturalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(espacios: List<EspacioNaturalEntity>)

    @Query("""
        SELECT * FROM espacios_naturales 
        WHERE nombre LIKE '%' || :query || '%'
           OR descripcion LIKE '%' || :query || '%'
           OR ubicacion LIKE '%' || :query || '%'
           OR municipio LIKE '%' || :query || '%'
           OR zona LIKE '%' || :query || '%'
           OR flora LIKE '%' || :query || '%'
           OR fauna LIKE '%' || :query || '%'
           OR queVer LIKE '%' || :query || '%'
           OR observaciones LIKE '%' || :query || '%'
    """)
    suspend fun searchByText(query: String): List<EspacioNaturalEntity>


    @Query("SELECT * FROM espacios_naturales WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): EspacioNaturalEntity?
}
