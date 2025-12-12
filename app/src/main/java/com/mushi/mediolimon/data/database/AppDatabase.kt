package com.mushi.mediolimon.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mushi.mediolimon.data.database.dao.IngredienteDao
import com.mushi.mediolimon.data.database.dao.RecetaGuardadaDao
import com.mushi.mediolimon.data.database.entities.Ingrediente
import com.mushi.mediolimon.data.database.entities.RecetaGuardada

/**
 * Clase principal de la base de datos de Room.
 *
 * Define la configuración de la base de datos, incluyendo las entidades,
 * la versión, los DAOs y los conversores de tipos para interactuar con ella.
 */
@Database(entities = [Ingrediente::class, RecetaGuardada::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun IngredienteDao(): IngredienteDao
    abstract fun recetaGuardadaDao(): RecetaGuardadaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {}
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE UNIQUE INDEX index_ingredientes_nombre ON ingredientes(nombre)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `recetas_guardadas` (`id` INTEGER NOT NULL, `title` TEXT NOT NULL, `imageUrl` TEXT, PRIMARY KEY(`id`))")
            }
        }

        // Migración de la versión 4 a la 5: añade las columnas para ingredientes e instrucciones.
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `recetas_guardadas` ADD COLUMN `instructions` TEXT")
                database.execSQL("ALTER TABLE `recetas_guardadas` ADD COLUMN `extendedIngredients` TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}