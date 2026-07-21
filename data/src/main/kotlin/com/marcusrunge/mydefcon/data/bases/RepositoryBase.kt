package com.marcusrunge.mydefcon.data.bases

import android.content.Context
import androidx.room3.Room
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.marcusrunge.mydefcon.data.interfaces.CheckItems

/**
 * Abstract base class for repositories.
 *
 * @param context Application context used to initialize the database.
 */
internal abstract class RepositoryBase(
    context: Context
) {

    /**
     * DAO for accessing check items.
     *
     * This property must be initialized by the concrete repository.
     */
    protected lateinit var _checkItems: CheckItems

    /**
     * Room database instance used by the repository.
     */
    internal val myDefconDatabase: MyDefconDatabase

    init {
        myDefconDatabase = Room.databaseBuilder<MyDefconDatabase>(
            context = context.applicationContext,
            name = "mydefcon_database"
        )
            .addMigrations(MIGRATION_2_3)
            .build()
    }

    private companion object {

        /**
         * Migrates the database schema from version 2 to version 3.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {

            override suspend fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    """
                    CREATE TABLE checkitem_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        uuid TEXT NOT NULL,
                        text TEXT,
                        is_checked INTEGER NOT NULL,
                        is_deleted INTEGER NOT NULL,
                        defcon INTEGER NOT NULL,
                        created INTEGER NOT NULL,
                        updated INTEGER NOT NULL,
                        is_export INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )

                connection.execSQL(
                    """
                    INSERT INTO checkitem_new (
                        id,
                        uuid,
                        text,
                        is_checked,
                        is_deleted,
                        defcon,
                        created,
                        updated,
                        is_export
                    )
                    SELECT
                        id,
                        uuid,
                        text,
                        is_checked,
                        is_deleted,
                        defcon,
                        COALESCE(
                            created,
                            CAST(strftime('%s', 'now') AS INTEGER) * 1000
                        ),
                        updated,
                        0
                    FROM checkitem
                    """.trimIndent()
                )

                connection.execSQL(
                    "DROP TABLE checkitem"
                )

                connection.execSQL(
                    "ALTER TABLE checkitem_new RENAME TO checkitem"
                )
            }
        }
    }
}