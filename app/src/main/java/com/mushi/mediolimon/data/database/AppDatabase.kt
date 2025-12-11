package com.mushi.mediolimon.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mushi.mediolimon.data.database.dao.IngredienteDao
import com.mushi.mediolimon.data.database.entities.Ingrediente

/**
 * Clase principal de la base de datos de Room
 *
 * Esta clase es el punto de acceso a la base de datos de Room
 */

@Database(entities = [Ingrediente::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    //Metodos para acceder a los DAO
    abstract fun IngredienteDao(): IngredienteDao

    //Singleton para obtener la instancia de la base de datos
    //Si ya existe una instancia, la devuelve, si no, la crea
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migración de la versión 1 a la 2 (vacía).
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // No hubo cambios de esquema en esta versión.
            }
        }

        // Migración de la versión 2 a la 3: añade un índice único a la tabla 'ingredientes'.
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE UNIQUE INDEX index_ingredientes_nombre ON ingredientes(nombre)")
            }
        }

        //Metodo para obtener la instancia de la base de datos
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Añadimos los planes de migración.
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}