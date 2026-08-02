package com.example.ap2_proyectofinal_angelraonel_joserafael.di

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.repository.AuthRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}