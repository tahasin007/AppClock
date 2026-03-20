package com.android.appclock.data.source

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS schedules_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        appName TEXT NOT NULL,
                        packageName TEXT NOT NULL,
                        scheduledDateTime INTEGER NOT NULL,
                        description TEXT,
                        status TEXT NOT NULL
                    )
                """.trimIndent()
            )
            db.execSQL(
                """
                    INSERT INTO schedules_new (id, appName, packageName, scheduledDateTime, description, status)
                    SELECT id, appName, packageName, scheduledDateTime, description, status
                    FROM schedules
                """.trimIndent()
            )
            db.execSQL("DROP TABLE schedules")
            db.execSQL("ALTER TABLE schedules_new RENAME TO schedules")
        }
    }
}

