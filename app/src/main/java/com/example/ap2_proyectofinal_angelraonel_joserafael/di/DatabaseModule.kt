package com.example.ap2_proyectofinal_angelraonel_joserafael.di

import android.content.Context
import androidx.room.Room
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cliente.ClienteDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.tarifa.ConfigDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.database.PrestamosDatabase
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.adminrequest.AdminRegisterRequestDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.notification.NotificationDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cierre.CashClosureDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePrestamosDatabase(
        @ApplicationContext context: Context
    ): PrestamosDatabase {
        return Room.databaseBuilder(
            context,
            PrestamosDatabase::class.java,
            "prestamos_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: PrestamosDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideClienteDao(database: PrestamosDatabase): ClienteDao {
        return database.clienteDao()
    }

    @Provides
    @Singleton
    fun provideConfigDao(database: PrestamosDatabase): ConfigDao {
        return database.configDao()
    }

    @Provides
    @Singleton
    fun providePrestamoDao(database: PrestamosDatabase): PrestamoDao {
        return database.prestamoDao()
    }

    @Provides
    @Singleton
    fun provideTransaccionDao(database: PrestamosDatabase): TransaccionDao {
        return database.transaccionDao()
    }

    @Provides
    @Singleton
    fun provideAdminRegisterRequestDao(database: PrestamosDatabase): AdminRegisterRequestDao {
        return database.adminRegisterRequestDao()
    }

    @Provides
    @Singleton
    fun provideNotificationDao(database: PrestamosDatabase): NotificationDao = database.notificationDao()

    @Provides
    @Singleton
    fun provideCashClosureDao(database: PrestamosDatabase): CashClosureDao = database.cashClosureDao()
}
