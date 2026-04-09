package com.marcusrunge.mydefcon.data.bases

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.marcusrunge.mydefcon.data.interfaces.CheckItems

/**
 * An abstract base class for repositories.
 *
 * This class provides common functionality for repositories, such as initializing the
 * database and holding the database instance. Subclasses are responsible for initializing
 * the DAOs.
 *
 * @param context The application context, which is used to build the Room database instance.
 * @throws IllegalStateException if the provided context is null.
 */
internal abstract class RepositoryBase(context: Context?) {
    /**
     * The backing field for the [CheckItems].
     *
     * This property is intended to be initialized by a subclass (e.g., `RepositoryImpl`) and holds
     * the concrete implementation of the [CheckItems]. It is `protected` to be accessible
     * only within this class and its subclasses.
     */
    protected lateinit var _checkItems: CheckItems

    /**
     * The Room database instance for the application.
     *
     * This property provides access to the `MyDefconDatabase` instance, which is built
     * using the application context. It is marked as `internal` to be accessible within
     * the same module.
     */
    internal val myDefconDatabase: MyDefconDatabase

    init {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {

                // 1. Create new table with the updated schema
                db.execSQL("""
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
                """)

                // 2. Copy data from old → new
                // For 'created', replace NULL with 0 or a sane default (e.g., current timestamp)
                db.execSQL("""
                    INSERT INTO checkitem_new (
                        id, uuid, text, is_checked, is_deleted, defcon, created, updated, is_export
                    )
                    SELECT 
                        id,
                        uuid,
                        text,
                        is_checked,
                        is_deleted,
                        defcon,
                        COALESCE(created, strftime('%s','now') * 1000) AS created,
                        updated,
                        0 AS is_export
                        FROM checkitem
                    """)

                // 3. Drop old table
                db.execSQL("DROP TABLE checkitem")

                // 4. Rename new → old
                db.execSQL("ALTER TABLE checkitem_new RENAME TO checkitem")
            }
        }

        // A non-null context is required to initialize the database.
        requireNotNull(context) { "Context must not be null for database initialization." }
        myDefconDatabase = Room.databaseBuilder(
            context,
            MyDefconDatabase::class.java,
            "mydefcon_database")
            .addMigrations(MIGRATION_2_3)
            .build()
    }
}
