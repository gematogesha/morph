package com.wheatley.morph.di

import android.content.Context
import androidx.room.Room
import com.wheatley.morph.model.challenge.AppDatabase
import com.wheatley.morph.model.challenge.ChallengeDao
import com.wheatley.morph.model.challenge.ChallengeScreenModel
import com.wheatley.morph.model.challenge.repository.ChallengeRepository
import com.wheatley.morph.model.challenge.repository.ChallengeRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object DiModules {
    private fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            //TODO: Изменить имя DB
            "challenge-db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
            .also {
                // Можно добавить callback для миграций
            }
    }

    private fun provideChallengeDao(database: AppDatabase): ChallengeDao {
        return database.challengeDao()
    }

    private fun provideChallengeRepository(dao: ChallengeDao): ChallengeRepository {
        return ChallengeRepositoryImpl(dao)
    }

    val databaseModule = module {
        single { provideDatabase(androidContext()) }
        single { provideChallengeDao(get()) }
    }

    val repositoryModule = module {
        singleOf(::provideChallengeRepository)
    }

    val screenModelModule = module {
        factory { ChallengeScreenModel(get()) }
    }
}

fun initKoinModules(context: Context) {
    startKoin {
        androidContext(context)
        modules(
            DiModules.databaseModule,
            DiModules.repositoryModule,
            DiModules.screenModelModule
        )
    }
}