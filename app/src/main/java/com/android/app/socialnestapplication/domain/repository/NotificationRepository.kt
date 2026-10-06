package com.android.app.socialnestapplication.domain.repository

import com.android.app.socialnestapplication.domain.model.NotificationItem

interface NotificationRepository {
    suspend fun listNotifications(): List<NotificationItem>
}
