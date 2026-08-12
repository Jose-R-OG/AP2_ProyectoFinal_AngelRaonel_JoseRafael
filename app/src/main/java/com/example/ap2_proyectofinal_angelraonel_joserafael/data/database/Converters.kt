package com.example.ap2_proyectofinal_angelraonel_joserafael.data.database

import androidx.room.TypeConverter
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.PaymentMethod
import java.time.LocalDate
import java.math.BigDecimal

class Converters {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromFrecuenciaPago(value: FrecuenciaPago?): String? {
        return value?.name
    }

    @TypeConverter
    fun toFrecuenciaPago(value: String?): FrecuenciaPago? {
        return value?.let { FrecuenciaPago.valueOf(it) }
    }

    @TypeConverter
    fun fromLoanStatus(value: LoanStatus?): String? {
        return value?.name
    }

    @TypeConverter
    fun toLoanStatus(value: String?): LoanStatus? {
        return value?.let { LoanStatus.valueOf(it) }
    }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod?): String? = value?.name

    @TypeConverter
    fun toPaymentMethod(value: String?): PaymentMethod? = value?.let(PaymentMethod::valueOf)

    @TypeConverter
    fun fromUserRole(value: com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole?): String? = value?.name

    @TypeConverter
    fun toUserRole(value: String?): com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole? = value?.let { 
        com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole.valueOf(it) 
    }
}
