package es.uniovi.asturnatura.data

import androidx.room.Database
import androidx.room.RoomDatabase
import es.uniovi.asturnatura.model.EspacioNaturalEntity

@Database(entities = [EspacioNaturalEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun espaciosDao(): EspaciosDAO
}
