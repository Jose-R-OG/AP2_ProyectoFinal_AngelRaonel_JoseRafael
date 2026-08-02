package com.example.ap2_proyectofinal_angelraonel_joserafael.di

import android.content.Context
import androidx.room.Room
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.local.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.database.PrestamosDatabase
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
    fun providePrestamosDatabase(@ApplicationContext context: Context): PrestamosDatabase {
        return Room.databaseBuilder(
            context,
            PrestamosDatabase::class.java,
            "prestamos_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: PrestamosDatabase): UserDao {
        return database.userDao()
    }
}