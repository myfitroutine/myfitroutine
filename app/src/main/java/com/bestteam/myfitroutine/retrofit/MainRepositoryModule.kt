package com.bestteam.myfitroutine.retrofit

import com.bestteam.myfitroutine.Repository.MainRepository
import com.bestteam.myfitroutine.Repository.MainRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainRepositoryModule {

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideMainRepository(db: FirebaseFirestore): MainRepository {
        return MainRepositoryImpl(db)
    }
}