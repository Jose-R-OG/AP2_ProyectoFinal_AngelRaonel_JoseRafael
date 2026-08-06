package com.example.ap2_proyectofinal_angelraonel_joserafael.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.local.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.local.UserEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.local.ClienteDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.local.ClienteEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.adminrequest.AdminRegisterRequestDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.adminrequest.AdminRegisterRequestEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.CuotaEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Tarifario.local.ConfigDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Tarifario.local.ConfigEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionEntity

@Database(
    entities = [
        UserEntity::class,
        ClienteEntity::class,
        ConfigEntity::class,
        PrestamoEntity::class,
        CuotaEntity::class,
        TransaccionEntity::class,
        AdminRegisterRequestEntity::class
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PrestamosDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun clienteDao(): ClienteDao
    abstract fun configDao(): ConfigDao
    abstract fun prestamoDao(): PrestamoDao
    abstract fun transaccionDao(): TransaccionDao
    abstract fun adminRegisterRequestDao(): AdminRegisterRequestDao

}