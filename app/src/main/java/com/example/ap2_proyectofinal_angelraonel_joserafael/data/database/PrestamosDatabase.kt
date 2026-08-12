package com.example.ap2_proyectofinal_angelraonel_joserafael.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user.UserEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cliente.ClienteDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cliente.ClienteEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.adminrequest.AdminRegisterRequestDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.adminrequest.AdminRegisterRequestEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.CuotaEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.LoanStatusHistoryEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.tarifa.ConfigDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.tarifa.ConfigEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.notification.NotificationDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.notification.NotificationEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cierre.CashClosureDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cierre.CashClosureEntity

@Database(
    entities = [
        UserEntity::class,
        ClienteEntity::class,
        ConfigEntity::class,
        PrestamoEntity::class,
        CuotaEntity::class,
        TransaccionEntity::class,
        AdminRegisterRequestEntity::class,
        NotificationEntity::class,
        CashClosureEntity::class,
        LoanStatusHistoryEntity::class
    ],
    version = 14,
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
    abstract fun notificationDao(): NotificationDao
    abstract fun cashClosureDao(): CashClosureDao

}
