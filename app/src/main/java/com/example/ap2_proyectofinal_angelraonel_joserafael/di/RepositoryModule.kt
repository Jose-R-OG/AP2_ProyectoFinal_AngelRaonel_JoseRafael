package com.example.ap2_proyectofinal_angelraonel_joserafael.di

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.repository.AuthRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.repository.ClienteRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Tarifario.repository.TarifarioRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TarifarioRepository
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

    @Binds
    @Singleton
    abstract fun bindClienteRepository(
        clienteRepositoryImpl: ClienteRepositoryImpl
    ): ClienteRepository

    @Binds
    @Singleton
    abstract fun bindTarifarioRepository(
        tarifarioRepositoryImpl: TarifarioRepositoryImpl
    ): TarifarioRepository
}