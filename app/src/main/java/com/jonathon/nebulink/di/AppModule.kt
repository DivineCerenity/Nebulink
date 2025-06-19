package com.jonathon.nebulink.di

import android.app.Application
import androidx.room.Room
import com.jonathon.nebulink.data.local.DatabaseCallback
import com.jonathon.nebulink.data.local.NebulinkDatabase
import com.jonathon.nebulink.data.repository.GameRepositoryImpl
import com.jonathon.nebulink.data.repository.NebuDataStore
import com.jonathon.nebulink.domain.repository.GameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }    @Provides
    @Singleton
    fun provideNebulinkDatabase(
        app: Application,
        applicationScope: CoroutineScope
    ): NebulinkDatabase {
        return Room.databaseBuilder(
            app,
            NebulinkDatabase::class.java,
            "nebulink.db"
        ).addCallback(DatabaseCallback(applicationScope))
         .build()
    }

    @Provides
    @Singleton
    fun provideNebuDataStore(app: Application): NebuDataStore {
        return NebuDataStore(app)
    }

    @Provides
    @Singleton
    fun provideGameRepository(
        db: NebulinkDatabase,
        dataStore: NebuDataStore
    ): GameRepository {
        return GameRepositoryImpl(db.puzzleDao, dataStore)
    }
}
