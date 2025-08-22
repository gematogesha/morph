package com.wheatley.morph.di

import android.content.Context
import androidx.room.Room
import com.wheatley.morph.core.app.UpdateManager
import com.wheatley.morph.data.local.challenge.AppDatabase
import com.wheatley.morph.data.local.challenge.ChallengeDao
import com.wheatley.morph.data.local.challenge.ChallengeScreenModel
import com.wheatley.morph.data.remote.UpdateApi
import com.wheatley.morph.domain.repository.ChallengeRepository
import com.wheatley.morph.data.repository.ChallengeRepositoryImpl
import com.wheatley.morph.presentation.add.AddChallengeScreenModel
import com.wheatley.morph.presentation.onboarding.OnBoardingScreenModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object DiModules {
    private fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "morph-db"
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

    val addChallengeModule = module {
        factory { AddChallengeScreenModel(get()) } // get() = ChallengeRepository
    }

    val onBoardingModule = module {
        factory { OnBoardingScreenModel(get()) }
    }

    val updateModule = module {
        single { UpdateApi() }
        single { UpdateManager(context = get(), api = get()) }
    }
}

fun initKoinModules(context: Context) {
    startKoin {
        androidContext(context)
        modules(
            DiModules.databaseModule,
            DiModules.repositoryModule,
            DiModules.screenModelModule,
            DiModules.addChallengeModule,
            DiModules.onBoardingModule,
            DiModules.updateModule
        )
    }
}