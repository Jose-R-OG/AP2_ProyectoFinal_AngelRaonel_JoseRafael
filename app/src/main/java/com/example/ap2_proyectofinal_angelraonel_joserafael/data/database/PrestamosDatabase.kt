package com.example.ap2_proyectofinal_angelraonel_joserafael.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.local.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.local.UserEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.local.ClienteDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.local.ClienteEntity

@Database(
    entities = [UserEntity::class, ClienteEntity::class],
    version = 2,
    exportSchema = false
)
abstract class PrestamosDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clienteDao(): ClienteDao
}