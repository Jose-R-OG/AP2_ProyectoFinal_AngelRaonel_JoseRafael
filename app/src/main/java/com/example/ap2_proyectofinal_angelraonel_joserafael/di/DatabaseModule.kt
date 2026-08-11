package com.example.ap2_proyectofinal_angelraonel_joserafael.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user.UserEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cliente.ClienteDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.tarifa.ConfigDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.database.PrestamosDatabase
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.adminrequest.AdminRegisterRequestDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.notification.NotificationDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cierre.CashClosureDao
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val migration9To10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE users ADD COLUMN profilePhotoPath TEXT")
            db.execSQL("ALTER TABLE users ADD COLUMN businessName TEXT")
            db.execSQL("ALTER TABLE users ADD COLUMN businessLogoPath TEXT")
            db.execSQL("ALTER TABLE transacciones ADD COLUMN paymentMethod TEXT NOT NULL DEFAULT 'EFECTIVO'")
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS notifications (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "recipientUserId INTEGER NOT NULL, title TEXT NOT NULL, " +
                    "message TEXT NOT NULL, relatedLoanId INTEGER, " +
                    "createdAt INTEGER NOT NULL, isRead INTEGER NOT NULL)"
            )
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS cash_closures (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "userId INTEGER NOT NULL, businessDate TEXT NOT NULL, " +
                    "closedAt INTEGER NOT NULL, totalCollected TEXT NOT NULL, " +
                    "cashRegistered TEXT NOT NULL, cashInHand TEXT NOT NULL, " +
                    "transferAmount TEXT NOT NULL, transactionCount INTEGER NOT NULL, " +
                    "visitedCount INTEGER NOT NULL)"
            )
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_cash_closures_userId_businessDate " +
                    "ON cash_closures (userId, businessDate)"
            )
        }
    }

    private val migration10To11 = object : Migration(10, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE users ADD COLUMN address TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE users ADD COLUMN dniFrontPhotoPath TEXT")
            db.execSQL("ALTER TABLE users ADD COLUMN dniBackPhotoPath TEXT")
            db.execSQL("ALTER TABLE clients ADD COLUMN zone TEXT NOT NULL DEFAULT 'SIN ASIGNAR'")
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS loan_status_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "loanId INTEGER NOT NULL, status TEXT NOT NULL, " +
                    "changedAt INTEGER NOT NULL, changedByUserId INTEGER NOT NULL, note TEXT)"
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_loan_status_history_loanId ON loan_status_history (loanId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_loan_status_history_changedAt ON loan_status_history (changedAt)")
            db.execSQL(
                "INSERT INTO loan_status_history (loanId, status, changedAt, changedByUserId, note) " +
                    "SELECT id, estado, COALESCE(fechaInicio, fechaCreacion), " +
                    "COALESCE(aprobadoPorAdminId, empleadoId), 'Estado importado al historial' FROM prestamos"
            )
        }
    }

    private val migration11To12 = object : Migration(11, 12) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE users ADD COLUMN canCreateClients INTEGER NOT NULL DEFAULT 1")
            db.execSQL("ALTER TABLE users ADD COLUMN canCollectPayments INTEGER NOT NULL DEFAULT 1")
            db.execSQL("ALTER TABLE users ADD COLUMN canViewRoute INTEGER NOT NULL DEFAULT 1")
            db.execSQL("ALTER TABLE users ADD COLUMN canCloseCash INTEGER NOT NULL DEFAULT 1")
            db.execSQL("ALTER TABLE users ADD COLUMN canShareDocuments INTEGER NOT NULL DEFAULT 1")
            db.execSQL("ALTER TABLE prestamos ADD COLUMN diaPagoPreferido INTEGER")
            db.execSQL("ALTER TABLE prestamos ADD COLUMN diaPagoDescripcion TEXT")
        }
    }

    @Provides
    @Singleton
    fun providePrestamosDatabase(
        @ApplicationContext context: Context,
        userDaoProvider: Provider<UserDao>
    ): PrestamosDatabase {
        return Room.databaseBuilder(
            context,
            PrestamosDatabase::class.java,
            "prestamos_db"
        )
            .addMigrations(migration9To10, migration10To11, migration11To12)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val userDao = userDaoProvider.get()
                        
                        // Insertar Admin por defecto
                        userDao.insertUser(
                            UserEntity(
                                nombreCompleto = "Administrador Principal",
                                username = "admin",
                                identificacion = "ADMIN-001",
                                telefono = "8090000000",
                                pin = "1234",
                                role = UserRole.ADMINISTRADOR,
                                isActive = true
                            )
                        )

                        // Insertar Empleado por defecto
                        userDao.insertUser(
                            UserEntity(
                                nombreCompleto = "Empleado de Prueba",
                                username = "empleado",
                                identificacion = "EMP-001",
                                telefono = "8091111111",
                                pin = "1234",
                                role = UserRole.EMPLEADO,
                                isActive = true
                            )
                        )
                    }
                }
            })
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
