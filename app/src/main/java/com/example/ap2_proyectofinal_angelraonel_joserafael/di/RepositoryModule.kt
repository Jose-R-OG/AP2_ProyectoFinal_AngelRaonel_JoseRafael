package com.example.ap2_proyectofinal_angelraonel_joserafael.di

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.AuthRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.ClienteRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.PrestamoRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.TarifarioRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.TransaccionRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.adminrequest.AdminRegisterRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.dashboard.DashboardRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.NotificationRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.CashClosureRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TarifarioRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TransaccionRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.adminrequest.AdminRegisterRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.dashboard.DashboardRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.NotificationRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.CashClosureRepository
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

    @Binds
    @Singleton
    abstract fun bindPrestamoRepository(
        prestamoRepositoryImpl: PrestamoRepositoryImpl
    ): PrestamoRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(
        dashboardRepositoryImpl: DashboardRepositoryImpl
    ): DashboardRepository

    @Binds
    @Singleton
    abstract fun bindTransaccionRepository(
        transaccionRepositoryImpl: TransaccionRepositoryImpl
    ): TransaccionRepository

    @Binds
    @Singleton
    abstract fun bindAdminRegisterRepository(
        adminRegisterRepositoryImpl: AdminRegisterRepositoryImpl
    ): AdminRegisterRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindCashClosureRepository(
        cashClosureRepositoryImpl: CashClosureRepositoryImpl
    ): CashClosureRepository
}
