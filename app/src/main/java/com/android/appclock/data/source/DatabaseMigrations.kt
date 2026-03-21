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

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE schedules ADD COLUMN recurringType TEXT NOT NULL DEFAULT 'NONE'")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS usage_monitoring_rules (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        appName TEXT NOT NULL,
                        packageName TEXT NOT NULL,
                        dailyLimitMinutes INTEGER NOT NULL,
                        notifyAt80Percent INTEGER NOT NULL DEFAULT 1,
                        notifyAt100Percent INTEGER NOT NULL DEFAULT 1,
                        isActive INTEGER NOT NULL DEFAULT 1
                    )
                """.trimIndent()
            )
            db.execSQL(
                """
                    CREATE UNIQUE INDEX IF NOT EXISTS index_usage_monitoring_rules_packageName
                    ON usage_monitoring_rules(packageName)
                """.trimIndent()
            )
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                    CREATE TABLE IF NOT EXISTS usage_monitoring_alert_state (
                        ruleId INTEGER NOT NULL,
                        dateKey TEXT NOT NULL,
                        notifiedAt80 INTEGER NOT NULL DEFAULT 0,
                        notifiedAt100 INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(ruleId, dateKey)
                    )
                """.trimIndent()
            )
        }
    }
}

