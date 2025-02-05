package com.android.appclock.di

import android.content.Context
import androidx.room.Room
import com.android.appclock.data.repository.ScheduleRepositoryImpl
import com.android.appclock.data.source.ScheduleDao
import com.android.appclock.data.source.ScheduleDatabase
import com.android.appclock.domain.repository.ScheduleRepository
import com.android.appclock.domain.usecase.AddScheduleUseCase
import com.android.appclock.domain.usecase.DeleteScheduleUseCase
import com.android.appclock.domain.usecase.EditScheduleUseCase
import com.android.appclock.domain.usecase.GetScheduleByIdUseCase
import com.android.appclock.domain.usecase.GetSchedulesUseCase
import com.android.appclock.domain.usecase.ScheduleUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): ScheduleDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ScheduleDatabase::class.java,
            "schedule_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideScheduleDao(database: ScheduleDatabase): ScheduleDao {
        return database.scheduleDao()
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(dao: ScheduleDao): ScheduleRepository {
        return ScheduleRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideScheduleUseCases(repository: ScheduleRepository): ScheduleUseCases {
        return ScheduleUseCases(
            getSchedules = GetSchedulesUseCase(repository),
            addSchedule = AddScheduleUseCase(repository),
            editSchedule = EditScheduleUseCase(repository),
            deleteSchedule = DeleteScheduleUseCase(repository),
            getScheduleById = GetScheduleByIdUseCase(repository)
        )
    }
}