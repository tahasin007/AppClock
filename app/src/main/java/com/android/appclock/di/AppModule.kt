package com.android.appclock.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.android.appclock.core.utils.AppIconLoader
import com.android.appclock.data.alarm.AlarmScheduler
import com.android.appclock.data.monitoring.UsageAlertSchedulerImpl
import com.android.appclock.data.repository.ScheduleRepositoryImpl
import com.android.appclock.data.repository.UsageMonitoringRepositoryImpl
import com.android.appclock.data.source.DatabaseMigrations
import com.android.appclock.data.source.ScheduleDao
import com.android.appclock.data.source.ScheduleDatabase
import com.android.appclock.data.source.UsageMonitoringAlertStateDao
import com.android.appclock.data.source.UsageMonitoringDao
import com.android.appclock.domain.repository.ScheduleRepository
import com.android.appclock.domain.repository.UsageMonitoringRepository
import com.android.appclock.domain.service.AppLaunchScheduler
import com.android.appclock.domain.service.UsageAlertScheduler
import com.android.appclock.domain.usecase.AddScheduleUseCase
import com.android.appclock.domain.usecase.AddUsageMonitoringRuleUseCase
import com.android.appclock.domain.usecase.DeleteScheduleByIdUseCase
import com.android.appclock.domain.usecase.DeleteScheduleUseCase
import com.android.appclock.domain.usecase.DeleteUsageMonitoringRuleByIdUseCase
import com.android.appclock.domain.usecase.EditScheduleUseCase
import com.android.appclock.domain.usecase.EditUsageMonitoringRuleUseCase
import com.android.appclock.domain.usecase.GetScheduleByIdUseCase
import com.android.appclock.domain.usecase.GetSchedulesUseCase
import com.android.appclock.domain.usecase.GetUsageMonitoringRuleByIdUseCase
import com.android.appclock.domain.usecase.GetUsageMonitoringRuleByPackageNameUseCase
import com.android.appclock.domain.usecase.GetUsageMonitoringRulesUseCase
import com.android.appclock.domain.usecase.ScheduleUseCases
import com.android.appclock.domain.usecase.UsageMonitoringUseCases
import com.android.appclock.tracking.AppLaunchTracker
import com.android.appclock.tracking.AppUsageStatsReader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): ScheduleDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ScheduleDatabase::class.java,
            "schedule_database"
        )
            .addMigrations(
                DatabaseMigrations.MIGRATION_1_2,
                DatabaseMigrations.MIGRATION_2_3,
                DatabaseMigrations.MIGRATION_3_4,
                DatabaseMigrations.MIGRATION_4_5
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideScheduleDao(database: ScheduleDatabase): ScheduleDao {
        return database.scheduleDao()
    }

    @Provides
    @Singleton
    fun provideUsageMonitoringDao(database: ScheduleDatabase): UsageMonitoringDao {
        return database.usageMonitoringDao()
    }

    @Provides
    @Singleton
    fun provideUsageMonitoringAlertStateDao(
        database: ScheduleDatabase
    ): UsageMonitoringAlertStateDao {
        return database.usageMonitoringAlertStateDao()
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(dao: ScheduleDao): ScheduleRepository {
        return ScheduleRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideUsageMonitoringRepository(dao: UsageMonitoringDao): UsageMonitoringRepository {
        return UsageMonitoringRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideScheduleUseCases(repository: ScheduleRepository): ScheduleUseCases {
        return ScheduleUseCases(
            getSchedules = GetSchedulesUseCase(repository),
            addSchedule = AddScheduleUseCase(repository),
            editSchedule = EditScheduleUseCase(repository),
            deleteSchedule = DeleteScheduleUseCase(repository),
            getScheduleById = GetScheduleByIdUseCase(repository),
            deleteScheduleById = DeleteScheduleByIdUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideUsageMonitoringUseCases(
        repository: UsageMonitoringRepository
    ): UsageMonitoringUseCases {
        return UsageMonitoringUseCases(
            getRules = GetUsageMonitoringRulesUseCase(repository),
            addRule = AddUsageMonitoringRuleUseCase(repository),
            editRule = EditUsageMonitoringRuleUseCase(repository),
            getRuleById = GetUsageMonitoringRuleByIdUseCase(repository),
            getRuleByPackageName = GetUsageMonitoringRuleByPackageNameUseCase(repository),
            deleteRuleById = DeleteUsageMonitoringRuleByIdUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideAlarmScheduler(context: Context): AppLaunchScheduler {
        return AlarmScheduler(context)
    }

    @Provides
    @Singleton
    fun provideUsageAlertScheduler(scheduler: UsageAlertSchedulerImpl): UsageAlertScheduler {
        return scheduler
    }

    @Provides
    @Singleton
    fun provideAppIconLoader(context: Context): AppIconLoader {
        return AppIconLoader(context)
    }

    @Provides
    @Singleton
    fun provideAppLaunchTracker(application: Application): AppLaunchTracker {
        return AppLaunchTracker(application)
    }

    @Provides
    @Singleton
    fun provideAppUsageStatsReader(context: Context): AppUsageStatsReader {
        return AppUsageStatsReader(context)
    }
}