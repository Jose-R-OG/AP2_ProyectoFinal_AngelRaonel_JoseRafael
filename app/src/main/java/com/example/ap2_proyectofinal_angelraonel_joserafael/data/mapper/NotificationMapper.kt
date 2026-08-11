package com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.notification.NotificationEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.AppNotification

fun NotificationEntity.toDomain() = AppNotification(
    id = id,
    recipientUserId = recipientUserId,
    title = title,
    message = message,
    relatedLoanId = relatedLoanId,
    createdAt = createdAt,
    isRead = isRead
)

fun AppNotification.toEntity() = NotificationEntity(
    id = id,
    recipientUserId = recipientUserId,
    title = title,
    message = message,
    relatedLoanId = relatedLoanId,
    createdAt = createdAt,
    isRead = isRead
)
