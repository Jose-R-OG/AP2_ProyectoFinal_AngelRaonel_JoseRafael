package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

data class AppNotification(
    val id: Long = 0,
    val recipientUserId: Long,
    val title: String,
    val message: String,
    val relatedLoanId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
