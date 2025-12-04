package com.mushi.mediolimon.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mushi.mediolimon.data.database.dao.IngredienteDao
import com.mushi.mediolimon.data.database.entities.Ingrediente

/**
 * Clase principal de la base de datos de Room
 *
 * Esta clase es el punto de acceso a la base de datos de Room
 */

@Database(entities = [Ingrediente::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    //Metodos para acceder a los DAO
    abstract fun IngredienteDao(): IngredienteDao

    //Singleton para obtener la instancia de la base de datos
    //Si ya existe una instancia, la devuelve, si no, la crea
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        //Metodo para obtener la instancia de la base de datos
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}