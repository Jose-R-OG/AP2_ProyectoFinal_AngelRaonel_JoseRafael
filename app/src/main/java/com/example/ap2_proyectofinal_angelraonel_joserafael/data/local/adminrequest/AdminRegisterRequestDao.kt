package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.adminrequest

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminRegisterRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: AdminRegisterRequestEntity)

    @Query("SELECT * FROM admin_register_requests WHERE email = :email")
    suspend fun getRequestByEmail(email: String): AdminRegisterRequestEntity?

    @Query("SELECT * FROM admin_register_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<AdminRegisterRequestEntity>>
}
