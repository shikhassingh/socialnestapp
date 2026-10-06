package com.android.app.socialnestapplication.domain.repository

import com.android.app.socialnestapplication.domain.model.Group

interface GroupRepository {
    suspend fun listGroups(): List<Group>
    suspend fun getGroup(groupId: String): Group?
    suspend fun listMemberIds(groupId: String): List<String>
}
